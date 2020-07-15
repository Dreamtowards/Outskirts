package outskirts.storage;

import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.Transform;

import java.util.HashMap;
import java.util.Map;

public final class Savers {




    public static final Saver<Transform> TRANSFORM = new Saver<Transform>() {
        @Override
        public void read(Transform obj, DataMap mp) {
            mp.getVector3f("origin", obj.origin);
            mp.getMatrix3f("basis",  obj.basis);
        }
        @Override
        public DataMap write(Transform obj, DataMap mp) {
            mp.put("origin", obj.origin);
            mp.put("basis",  obj.basis);
            return mp;
        }
    };

    public static final Map<Class, Saver> COLLISIONSHAPE_SMAP = new HashMap<>();
    static {
        COLLISIONSHAPE_SMAP.put(BoxShape.class, new Saver<BoxShape>() {
            @Override
            public void read(BoxShape obj, DataMap mp) {
                mp.getVector3f("halfextent", obj.getHalfExtent());
            }
            @Override
            public DataMap write(BoxShape obj, DataMap mp) {
                mp.putVector3f("halfextent", obj.getHalfExtent());
                return mp;
            }
        });
    }

    public static final Saver<RigidBody> RIGIDBODY = new Saver<RigidBody>() {
        // Transform
        // CollisionShape
        // gravity
        // linvel, angvel
        // mass
        // lindamping, angdamping
        // restitution, friction
        @Override
        public void read(RigidBody obj, DataMap mp) {
            Savers.TRANSFORM.read(obj.transform(), (DataMap)mp.get("transform"));
            try {
                DataMap mpCollisionshape = (DataMap)mp.get("collisionshape");
                CollisionShape collisionShape = (CollisionShape)Class.forName((String)mpCollisionshape.get("type")).newInstance();
                COLLISIONSHAPE_SMAP.get(collisionShape.getClass()).read(collisionShape, mpCollisionshape);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            mp.getVector3f("gravity", obj.getGravity());
            mp.getVector3f("linearvelocity", obj.getLinearVelocity());
            mp.getVector3f("angularvelocity", obj.getAngularVelocity());
            obj.setMass((float)mp.get("mass"));
            obj.setLinearDamping((float)mp.get("lineardamping"));
            obj.setAngularDamping((float)mp.get("angulardamping"));
            obj.setRestitution((float)mp.get("restitution"));
            obj.setFriction((float)mp.get("friction"));
        }
        @Override
        public DataMap write(RigidBody obj, DataMap mp) {
            mp.put("transform", Savers.TRANSFORM.write(obj.transform(), new DataMap()));
            {
                DataMap mpCollisionShape = new DataMap();
                mpCollisionShape.put("type", obj.getCollisionShape().getClass().getName());
                COLLISIONSHAPE_SMAP.get(obj.getCollisionShape().getClass()).write(obj.getCollisionShape(), mpCollisionShape);
                mp.put("collisionshape", mpCollisionShape);
            }
            mp.putVector3f("gravity", obj.getGravity());
            mp.putVector3f("linearvelocity", obj.getLinearVelocity());
            mp.putVector3f("angularvelocity", obj.getAngularVelocity());
            mp.put("mass", obj.getMass());
            mp.put("lineardamping", obj.getLinearDamping());
            mp.put("angulardamping", obj.getAngularDamping());
            mp.put("restitution", obj.getRestitution());
            mp.put("friction", obj.getFriction());
            return mp;
        }
    };

}
