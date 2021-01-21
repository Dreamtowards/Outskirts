package outskirts.storage;

import outskirts.client.Loader;
import outskirts.client.render.renderer.preferences.RenderPerferences;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.collision.shapes.convex.ConvexHullShape;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.physics.dynamics.RigidBody;
import outskirts.storage.dst.DArray;
import outskirts.storage.dst.DObject;
import outskirts.util.Transform;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL15.glGetBufferSubData;

public final class SAVERS {



    // should basis/rotation uses mat3x3 or quat4 .?
    public static final Saver<Transform> TRANSFORM = new Saver<Transform>() {
        @Override
        public void read(Transform obj, DObject mp) {
            mp.getVector3f("origin", obj.origin);

            Quaternion tmpquat = new Quaternion(); // opt cache
            mp.getVector4f("basis",  tmpquat);
            Quaternion.toMatrix(tmpquat, obj.basis);
        }
        @Override
        public DObject write(Transform obj, DObject mp) {
            mp.putVector3f("origin", obj.origin);

            Quaternion tmpquat = Quaternion.fromMatrix(obj.basis, null); // opt: do the quat cache
            mp.putVector4f("basis",  tmpquat);
            return mp;
        }
    };

    // should CollisionShape properties saves with system-lv-CollShape field(s).? (e.g. "type": "Shape-Class-Name" Field.
    // when yes (isolate): more boundary, more safeity.  when no (not isolate shape-properties): more mainly feeling, but may not 100% safe.
    public static final Map<Class, Saver> COLLISIONSHAPE_SMAP = new HashMap<>();
    static {
        COLLISIONSHAPE_SMAP.put(BoxShape.class, new Saver<BoxShape>() {
            @Override
            public void read(BoxShape obj, DObject mp) {
                mp.getVector3f("halfextent", obj.getHalfExtent());
            }
            @Override
            public DObject write(BoxShape obj, DObject mp) {
                mp.putVector3f("halfextent", obj.getHalfExtent());
                return mp;
            }
        });
        COLLISIONSHAPE_SMAP.put(SphereShape.class, new Saver<SphereShape>() {
            @Override
            public void read(SphereShape obj, DObject mp) {
                obj.setRadius((float)mp.get("radius"));
            }
            @Override
            public DObject write(SphereShape obj, DObject mp) {
                mp.put("radius", obj.getRadius());
                return mp;
            }
        });
        // sometimes not recormended to load/save the ConvexHullShape data,
        // cuz the data sometimes is runtime-defined/dependent by other data like the Model/Meshes.
        COLLISIONSHAPE_SMAP.put(ConvexHullShape.class, new Saver<ConvexHullShape>() {
            @Override
            public void read(ConvexHullShape obj, DObject mp) {
                DArray<DArray> lsVertices = mp.getDArray("vertices");
                List<Vector3f> vts = new ArrayList<>();
                for (DArray v3dat : lsVertices) {
                    vts.add(DArray.toVector3f(v3dat, null));
                }
                obj.getVertices().clear();
                obj.getVertices().addAll(vts);
            }
            @Override
            public DObject write(ConvexHullShape obj, DObject mp) {
                DArray lsVertices = new DArray();
                for (Vector3f v : obj.getVertices()) {
                    lsVertices.add(DArray.fromVector3f(v));
                }
                mp.put("vertices", lsVertices);
                return mp;
            }
        });
    }

    public static boolean OP_RIGIDBODY_WRITECOLLISIONSHAPE = false;
    public static final Saver<RigidBody> RIGIDBODY = new Saver<RigidBody>() {
        // Transform
        // CollisionShape
        // gravity
        // linvel, angvel
        // mass
        // lindamping, angdamping
        // restitution, friction
        @Override
        public void read(RigidBody obj, DObject mp) {
            SAVERS.TRANSFORM.read(obj.transform(), (DObject)mp.get("transform"));
            //todo: option to Custom set CollShape, not 100% nesecary do load.
            if (mp.containsKey("collisionshape")) {
                try {
                    DObject mpCollisionshape = (DObject) mp.get("collisionshape");
                    CollisionShape collisionshape = (CollisionShape) Class.forName((String) mpCollisionshape.get("type")).newInstance();
                    COLLISIONSHAPE_SMAP.get(collisionshape.getClass()).read(collisionshape, mpCollisionshape);
                    obj.setCollisionShape(collisionshape);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
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
        public DObject write(RigidBody obj, DObject mp) {
            mp.put("transform", SAVERS.TRANSFORM.write(obj.transform(), new DObject()));
            if (OP_RIGIDBODY_WRITECOLLISIONSHAPE) {
                DObject mpCollisionShape = new DObject();
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


    // deprecated(load model to an existed Model instance).
    // entity Model should be Constantfy. not like vec/mat that been set. cuz Model is huge object, not like vec that tiny, fixed length.
    // always no needs. when needs, can use txt-obj. tho its big and slower.
//    public static final Saver<Model> MODEL = new Saver<Model>() {
//        @Override
//        public void read(Model obj, DATObject mp) {
//            obj.createEBO(BytesConvert.toIntArray((byte[])mp.get("indices")));
//            List<DATObject> attrls = (List)mp.get("attribs");
//            for (int i = 0;i < attrls.size();i++) {
//                DATObject attrmp = attrls.get(i);
//                obj.createAttribute(i, (int)attrmp.get("vsize"), BytesConvert.toFloatArray((byte[])attrmp.get("data")));
//            }
//        }
//        @Override
//        public DATObject write(Model obj, DATObject mp) {
//            mp.put("indices", BytesConvert.toByteArray(obj.indices));
//            List attrls = new ArrayList();
//            for (int i = 0;i < 16;i++) {
//                Model.VAttribute vattr = obj.attribute(i);
//                if (vattr == null) break;
//                DATObject attrmp = new DATObject();
//                attrmp.put("vsize", vattr.vertexSize());
//                attrmp.put("data", BytesConvert.toByteArray(vattr.data));
//                attrls.add(attrmp);
//            }
//            mp.put("attribs", attrls);
//            return mp;
//        }
//    };

    public static final Saver<RenderPerferences> MATERIAL = new Saver<RenderPerferences>() {
        @Override
        public void read(RenderPerferences obj, DObject mp) {

            obj.setDiffuseMap(Loader.loadTexture((byte[])mp.get("diffuseMap")));
            obj.setEmissionMap(Loader.loadTexture((byte[])mp.get("emissionMap")));
            obj.setNormalMap(Loader.loadTexture((byte[])mp.get("normalMap")));

            obj.setSpecularMap(Loader.loadTexture((byte[])mp.get("specularMap")));
            obj.setSpecularStrength((float)mp.get("specularStrength"));
            obj.setShininess((float)mp.get("shininess"));

            obj.setDisplacementMap(Loader.loadTexture((byte[])mp.get("displacementMap")));
            obj.setDisplacementScale((float)mp.get("displacementScale"));

        }

        @Override
        public DObject write(RenderPerferences obj, DObject mp) {

            mp.put("diffuseMap", Loader.savePNG(obj.getDiffuseMap()));
            mp.put("emissionMap", Loader.savePNG(obj.getEmissionMap()));
            mp.put("normalMap", Loader.savePNG(obj.getNormalMap()));

            mp.put("specularMap", Loader.savePNG(obj.getSpecularMap()));
            mp.put("specularStrength", obj.getSpecularStrength());
            mp.put("shininess", obj.getShininess());

            mp.put("displacementMap", Loader.savePNG(obj.getDisplacementMap()));
            mp.put("displacementScale", obj.getDisplacementScale());

            return mp;
        }
    };

}
