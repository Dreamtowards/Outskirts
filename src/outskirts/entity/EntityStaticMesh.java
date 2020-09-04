package outskirts.entity;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.physics.collision.shapes.concave.BvhTriangleMeshShape;
import outskirts.storage.SAVERS;
import outskirts.storage.dat.DATObject;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class EntityStaticMesh extends Entity {

    public EntityStaticMesh() {
        setRegistryID("staticmesh");

        getRigidBody().setMass(0);
    }

    @Override
    public void setModel(Model model) {
        super.setModel(model);

        getRigidBody().setCollisionShape(new BvhTriangleMeshShape(
                getModel().indices,
                getModel().attribute(0).data
        ));
    }

    @Override
    public void onRead(DATObject mp) {
        super.onRead(mp);

        setModel(Loader.loadOBJ(new ByteArrayInputStream((byte[])mp.get("modelobj"))));

        SAVERS.MATERIAL.read(getMaterial(), (DATObject)mp.get("material"));
    }

    @Override
    public Map onWrite(DATObject mp) {
        super.onWrite(mp);

        mp.put("modelobj", Loader.saveOBJ(getModel()));

        mp.put("material", SAVERS.MATERIAL.write(getMaterial(), new DATObject()));

        return mp;
    }
}
