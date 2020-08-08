package outskirts.entity;

import outskirts.storage.DataMap;
import outskirts.storage.SAVERS;
import outskirts.util.Identifier;

import java.util.Map;

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


    @Override
    public void onRead(DataMap mp) {
        super.onRead(mp);

        SAVERS.MODEL.read(getModel(), (DataMap)mp.get("model"));
    }

    @Override
    public Map onWrite(DataMap mp) {
        super.onWrite(mp);

        mp.put("model", SAVERS.MODEL.write(getModel(), new DataMap()));

        return mp;
    }
}
