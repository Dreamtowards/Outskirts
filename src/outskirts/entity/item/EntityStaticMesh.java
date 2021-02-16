package outskirts.entity.item;

import outskirts.client.render.Model;
import outskirts.entity.Entity;
import outskirts.init.ex.Models;
import outskirts.physics.collision.shapes.GhostShape;
import outskirts.physics.collision.shapes.concave.BvhTriangleMeshShape;
import outskirts.storage.dst.DObject;

public class EntityStaticMesh extends Entity {

    public EntityStaticMesh() {
        setRegistryID("staticmesh");

        setModel(Models.EMPTY);
        rigidbody().setMass(0);
    }

    @Override
    public void setModel(Model model) {
        super.setModel(model);

        if (model.vertexCount() == 0) {
            getRigidBody().setCollisionShape(new GhostShape());
        } else {
            rigidbody().setCollisionShape(new BvhTriangleMeshShape(
                    getModel().indices,
                    getModel().attribute(0).data
            ));
        }
    }

    @Override
    public void onRead(DObject mp) {
        super.onRead(mp);

//        setModel(Loader.loadOBJ(new ByteArrayInputStream((byte[])mp.get("modelobj"))));
//
//        SAVERS.MATERIAL.read(getMaterial(), (DObject)mp.get("material"));
    }

    @Override
    public DObject onWrite(DObject mp) {
        super.onWrite(mp);

//        mp.put("modelobj", Loader.saveOBJ(getModel()));
//
//        mp.put("material", SAVERS.MATERIAL.write(getMaterial(), new DObject()));

        return mp;
    }
}
