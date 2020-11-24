package outskirts.entity;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.physics.collision.shapes.concave.BvhTriangleMeshShape;
import outskirts.storage.SAVERS;
import outskirts.storage.dst.DObject;

import java.io.ByteArrayInputStream;

public class EntityStaticMesh extends Entity {

    public EntityStaticMesh() {
        setRegistryID("staticmesh");

        rigidbody().setMass(0);
    }

    @Override
    public void setModel(Model model) {
        super.setModel(model);

        rigidbody().setCollisionShape(new BvhTriangleMeshShape(
                getModel().indices,
                getModel().attribute(0).data
        ));
    }

    @Override
    public void onRead(DObject mp) {
        super.onRead(mp);

        setModel(Loader.loadOBJ(new ByteArrayInputStream((byte[])mp.get("modelobj"))));

        SAVERS.MATERIAL.read(getMaterial(), (DObject)mp.get("material"));
    }

    @Override
    public DObject onWrite(DObject mp) {
        super.onWrite(mp);

        mp.put("modelobj", Loader.saveOBJ(getModel()));

        mp.put("material", SAVERS.MATERIAL.write(getMaterial(), new DObject()));

        return mp;
    }
}
