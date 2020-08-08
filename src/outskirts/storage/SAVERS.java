package outskirts.storage;

import outskirts.client.Loader;
import outskirts.client.material.Material;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.BytesConvert;
import outskirts.util.Transform;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL15.glGetBufferSubData;

public final class SAVERS {




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
            SAVERS.TRANSFORM.read(obj.transform(), (DataMap)mp.get("transform"));
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
            mp.put("transform", SAVERS.TRANSFORM.write(obj.transform(), new DataMap()));
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


    public static final Saver<Model> MODEL = new Saver<Model>() {
        @Override
        public void read(Model obj, DataMap mp) {
            obj.createEBO(BytesConvert.toIntArray((byte[])mp.get("indices")));

            List<DataMap> attrls = (List)mp.get("attributes");
            for (int i = 0;i < attrls.size();i++) {
                DataMap attrmp = attrls.get(i);
                obj.createAttribute(i, (int)attrmp.get("vsize"), BytesConvert.toFloatArray((byte[])attrmp.get("data")));
            }
        }

        @Override
        public DataMap write(Model obj, DataMap mp) {
            mp.put("indices", BytesConvert.toByteArray(obj.indices));

            List attrls = new ArrayList();
            for (int i = 0;i < 16;i++) {
                Model.VAttribute vattr = obj.attribute(i);
                if (vattr == null) break;
                DataMap attrmp = new DataMap();
                attrmp.put("vsize", vattr.vertexSize());
                attrmp.put("data", BytesConvert.toByteArray(vattr.data));
                attrls.add(attrmp);
            }
            mp.put("attributes", attrls);
            return mp;
        }
    };

    public static final Saver<Material> MATERIAL = new Saver<Material>() {
        @Override
        public void read(Material obj, DataMap mp) {

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
        public DataMap write(Material obj, DataMap mp) {

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
