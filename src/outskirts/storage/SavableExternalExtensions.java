package outskirts.storage;

import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.physics.dynamics.RigidBody;
import outskirts.storage.dst.DObject;
import outskirts.util.ReflectionUtils;
import outskirts.util.Transform;
import outskirts.util.vector.Quaternion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SavableExternalExtensions {

    public static Map<Class, Class<? extends Savable>> REG = new HashMap<>();



    static {
        REG.put(Transform.class, SE_Transform.class);
        REG.put(RigidBody.class, SE_RigidBody.class);

        REG.put(BoxShape.class, SE_BoxShape.class);
        REG.put(SphereShape.class, SE_SphereShape.class);
    }

    static class SE_Transform implements Savable {
        Transform transform;
        SE_Transform(Transform in) { transform=in; }

        @Override
        public void onRead(DObject mp) {

            mp.getVector3f("origin", transform.origin);

            Quaternion tq = mp.getVector4f("basis", new Quaternion());
            Quaternion.toMatrix(tq, transform.basis);
        }

        @Override
        public DObject onWrite(DObject mp) {

            mp.putVector3f("origin", transform.origin);

            Quaternion tq = Quaternion.fromMatrix(transform.basis, null);
            mp.putVector4f("basis",  tq);

            return mp;
        }
    }


    static class SE_RigidBody implements Savable {
        RigidBody rigidbody;
        SE_RigidBody(RigidBody in) { rigidbody=in; }

        @Override
        public void onRead(DObject mp) throws IOException {

            Savable.of(rigidbody.transform()).onRead(mp.getDObject("transform"));

            mp.getVector3f("linearvelocity", rigidbody.getLinearVelocity());
            mp.getVector3f("angularvelocity", rigidbody.getAngularVelocity());
            rigidbody.setLinearDamping(mp.getFloat("lineardamping"));
            rigidbody.setAngularDamping(mp.getFloat("angulardamping"));

            mp.getVector3f("gravity", rigidbody.getGravity());
            rigidbody.setMass(mp.getFloat("mass"));
            rigidbody.setFriction(mp.getFloat("friction"));
            rigidbody.setRestitution(mp.getFloat("restitution"));

            // optional collisionshape. may not save. because most times,
            // Entity's CollisionShape always is been setup in runtime programmly.
            DObject mpCollisionShape = mp.getDObject("collisionshape");
            if (mpCollisionShape != null) {
                CollisionShape shape = ReflectionUtils.newInstance(mpCollisionShape.getString("type"));
                Savable.of(shape).onRead(mpCollisionShape);
                rigidbody.setCollisionShape(shape);
            }
        }

        @Override
        public DObject onWrite(DObject mp) throws IOException {

            mp.put("transform", Savable.of(rigidbody.transform()).onWrite(new DObject()));

            mp.putVector3f("linearvelocity", rigidbody.getLinearVelocity());
            mp.putVector3f("angularvelocity", rigidbody.getAngularVelocity());
            mp.putFloat("lineardamping", rigidbody.getLinearDamping());
            mp.putFloat("angulardamping", rigidbody.getAngularDamping());

            mp.putVector3f("gravity", rigidbody.getGravity());
            mp.putFloat("mass", rigidbody.getMass());
            mp.putFloat("friction", rigidbody.getFriction());
            mp.putFloat("restitution", rigidbody.getRestitution());

            CollisionShape shape = rigidbody.getCollisionShape();
            if (shape != null) {
                DObject mpCollisionShape = Savable.of(shape).onWrite(new DObject());
                mpCollisionShape.put("type", shape.getClass().getName());
                mp.put("collisionshape", mpCollisionShape);
            }

            return mp;
        }
    }


    static class SE_BoxShape implements Savable {
        BoxShape boxshape;
        SE_BoxShape(BoxShape in) { boxshape=in; }

        @Override
        public void onRead(DObject mp) throws IOException {
            mp.getVector3f("halfextent", boxshape.getHalfExtent());
        }

        @Override
        public DObject onWrite(DObject mp) throws IOException {
            mp.putVector3f("halfextent", boxshape.getHalfExtent());
            return mp;
        }
    }

    static class SE_SphereShape implements Savable {
        SphereShape sphereshape;
        SE_SphereShape (SphereShape in) { sphereshape=in; }

        @Override
        public void onRead(DObject mp) throws IOException {
            sphereshape.setRadius(mp.getFloat("radius"));
        }

        @Override
        public DObject onWrite(DObject mp) throws IOException {
            mp.putFloat("radius", sphereshape.getRadius());
            return mp;
        }
    }

}
