package outskirts.entity;

import outskirts.util.Identifier;

// abstruct class
public class EntityModel extends Entity {

    private Identifier resModel, resDiffuseMap;

    public EntityModel() {
        setRegistryID("modelentity");


    }

    //    public static EntityModel staticModel(int[] idx, float[] vts) {
//        EntityModel entity = new EntityModel();
//        entity.getRigidBody()
//                .setMass(0)
//                .setCollisionShape(new TriangleMeshShape(idx, vts));
//        return entity;
//    }
//
//    public static EntityModel dynHull(float[] vts) {
//        EntityModel entity = new EntityModel();
//        entity.getRigidBody()
//                .setCollisionShape(new ConvexHullShape(QuickHull.quickHull(vts)));
//        return entity;
//    }


}
