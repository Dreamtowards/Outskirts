package outskirts.physics.dynamics;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.ObjectPool;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

public class RigidBody extends CollisionObject {

    /**
     * the gravity field may looks not a hardcore property(than velocities..), but always is needed..
     * gravity's type actually is acceleration
     * (by F=G*m1*m2/r^2 CONSTANT:[m1,G,r] AND F=(m2)a, then a=G*m1*r^2) ~= -9.81
     * this field will be get and apply to the body when CollisionWorld::stepSimulation's pre stage. (body.forces += body.grav * body.mass
     */
    private Vector3f gravity = new Vector3f(0, -9.81f, 0);


    private Vector3f linearVelocity = new Vector3f();  // linVel.length m/s, xyz is direction to linear-movement, vec3(2, 0, 0) means position.x add 2 per second
    private Vector3f angularVelocity = new Vector3f(); // angVel.length rad/s, xyz is rotation-axis, vec3(2PI, 0, 0) means rotate around +X axis(right hand law) a full circle per second
    // ? already had to worldspace

    private float inverseMass = 1; // F=ma == a=invM*F, unit as kg

    private Matrix3f invInertiaTensorWorld = new Matrix3f(); // world coordinate
    private Vector3f invInertiaTensorLocalDiag = new Vector3f(1, 1 ,1); // there is not vec3, actually its a mat3, its diagonal in mat3. actually mat3 rows: [x, 0, 0], [0, y, 0], [0, 0, z]

    /**
     * this forces are use for calculate acceleration (V'=a) (for integrate velocity)
     * in current/one simulation iteration. and will be clear/setzero after velocities-integration or curr-step/physframe/iteration
     * there are just Forces, not Momentum. there are not time info.
     */
    private Vector3f totalForce = new Vector3f(); // SUM(LinearForce) in curr frame/step. not time info. struc pos off
    private Vector3f totalTorque = new Vector3f();// SUM(Torque)      in curr frame/step. not time info  struc axis-angle

    /**
     * 0 == non-inertia, 1 == non-damping(keep moving)
     */
    private float linearDamping = 1f;
    private float angularDamping = 0.95f;  // ori 0.5.  or 0.95f.?

    private float restitution = 0f; // 0 == non-elastic, 1 == non-cost, always in [0-1]  // (speedaftercollision/speedbeforecollision)
    private float friction = 0.8f;

//    private List<Constraint> constraints = new ArrayList<>();



    void integrateVelocities(float delta) {
        // v' = a dt

        // linear
        // F=ma, then a=1/m*F    totalForce=SUM(Force)
        linearVelocity.addScaled(inverseMass * delta, totalForce);

        // angular
        // t=Ia, then a=1/I*t
        angularVelocity.addScaled(delta, Matrix3f.transform(invInertiaTensorWorld, new Vector3f(totalTorque)));

        // clamp angular velocity. collision calculations will fail on higher angular velocities
    }

    void performDamping(float delta) {
        // velocity damping
        // v *= d^t
        float E = 0.000025f;

        linearVelocity.scale(linearVelocity.lengthSquared()<E?0: (float)Math.pow(linearDamping, delta));

        angularVelocity.scale(angularVelocity.lengthSquared()<E?0: (float)Math.pow(angularDamping, delta));
        if (Outskirts.isKeyDown(GLFW.GLFW_KEY_0)) {
            angularVelocity.scale(0);
        }
        if (Outskirts.isKeyDown(GLFW.GLFW_KEY_8)) {
            transform().basis.setIdentity();
        }
        if (Outskirts.isKeyDown(GLFW.GLFW_KEY_9)) {
            linearVelocity.scale(0);
        }
    }

    // calls when rotated, integrated ..?
    // update when invInertiaTensorLocalDiagonal or transform().basis changed.
    /**
     * I_withrot = Q * I * Q^T. which Q is rot-trans-mat, I is the local-inertia-tensor(matrix).
     * https://en.wikipedia.org/wiki/Moment_of_inertia#Principal_axes
     * makes invInertiaTensor(LocalDiagonal) can applies/fits to rotated situation.
     */
    final void refreshInertiaTensorWorld() {
        Matrix3f m1 = Matrix3f.scale(invInertiaTensorLocalDiag, new Matrix3f(transform().basis));   // basis mul invInertiaTensorLocal(scale mat3)

        Matrix3f m2 = new Matrix3f(transform().basis).transpose();

        Matrix3f.mul(m1, m2, invInertiaTensorWorld);
    }
    public Matrix3f getInvInertiaTensorWorld() { // actually this is internal tmp-cache, should not be public.
        return invInertiaTensorWorld;
    }




    public RigidBody setMass(float mass) {
        this.inverseMass = mass==0?0: 1f/mass;
        updateBodyInertia(this);
        return this;
    }
    public final float getMass() {
        return inverseMass==0?0: 1f/inverseMass;
    }
    public final float getInvMass() {
        return inverseMass;
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }
    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public float getLinearDamping() {
        return linearDamping;
    }
    public RigidBody setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
        return this;
    }

    public float getAngularDamping() {
        return angularDamping;
    }
    public RigidBody setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
        return this;
    }

    public float getRestitution() {
        return restitution;
    }
    public RigidBody setRestitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    public float getFriction() {
        return friction;
    }
    public RigidBody setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    public final Vector3f getGravity() {
        return gravity;
    }



    public final void clearForces() {
        totalForce.set(0, 0, 0);
        totalTorque.set(0, 0, 0);
    }

    public void applyForce(Vector3f force) { // needs a special applyCentralForce() ..?
        totalForce.add(force);
    }
    public void applyTorque(Vector3f torque) {
        if (inverseMass == 0) return;
        totalTorque.add(torque);
    }
    public void applyForce(Vector3f force, Vector3f relpos) {
        if (inverseMass == 0) return;
        applyForce(force);
        if (relpos.lengthSquared() != 0) { // quick exit
            applyTorque(Vector3f.cross(relpos, force, null));
        }
    }

    public void applyImpulse(Vector3f impulse) { // applyCentralImpulse(vec3)/applyImpulse(vec3) ..?
        linearVelocity.addScaled(inverseMass, impulse); // I=mv, v=invM*I
    }
    public void applyTorqueImpulse(Vector3f impulse) {
        if (inverseMass == 0) return;
        angularVelocity.add(Matrix3f.transform(invInertiaTensorWorld, new Vector3f(impulse)));
    }
    public void applyImpulse(Vector3f impulse, Vector3f relpos) {
        if (inverseMass == 0) return;
        applyImpulse(impulse);
        if (relpos.lengthSquared() != 0) {
            applyTorqueImpulse(Vector3f.cross(relpos, impulse, null));
        }
    }



    public Vector3f getVelocity(Vector3f relpos, Vector3f dest) {  // getVelocityInLocalPoint()
        return Vector3f.cross(angularVelocity, relpos, dest).add(linearVelocity);
    }

    public final void setInertiaTensorLocal(float m00, float m11, float m22) {
        invInertiaTensorLocalDiag.set(m00==0?0: 1f/m00, m11==0?0: 1f/m11, m22==0?0: 1f/m22);
    }
    public final void setInertiaTensorLocal(Vector3f tensordiag) {
        setInertiaTensorLocal(tensordiag.x, tensordiag.y, tensordiag.z);
    }


    @Override
    public RigidBody setCollisionShape(CollisionShape collisionShape) {
        super.setCollisionShape(collisionShape);
        updateBodyInertia(this);
        return this;
    }

    private static void updateBodyInertia(RigidBody body) {
        Vector3f v = body.getCollisionShape().calculateLocalInertia(body.getMass(), body.invInertiaTensorLocalDiag);
        body.setInertiaTensorLocal(v);
    }
}
