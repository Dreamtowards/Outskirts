package outskirts.entity;

import outskirts.storage.dat.DATObject;
import outskirts.storage.SAVERS;

import java.util.Map;

// abstruct class
public class EntityModel extends Entity {

    public EntityModel() {
        setRegistryID("jusmodel");



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
    public void onRead(DATObject mp) {
        super.onRead(mp);

        SAVERS.MODEL.read(getModel(), (DATObject)mp.get("model"));

        SAVERS.MATERIAL.read(getMaterial(), (DATObject)mp.get("material"));
    }

    @Override
    public Map onWrite(DATObject mp) {
        super.onWrite(mp);

        mp.put("model", SAVERS.MODEL.write(getModel(), new DATObject()));

        mp.put("material", SAVERS.MATERIAL.write(getMaterial(), new DATObject()));

        return mp;
    }
}
