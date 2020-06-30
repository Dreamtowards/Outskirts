package outskirts.entity;

import outskirts.client.render.Light;
import outskirts.init.Models;
import outskirts.init.Textures;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.util.Side;
import outskirts.util.vector.Vector3f;

public class EntityStall extends Entity {

    private Light light = new Light();

    public EntityStall() {
        setRegistryID("stall");

        if (Side.CURRENT.isClient()) {
            getMaterial()
                    .setModel(Models.STALL)
                    .setDiffuseMap(Textures.BRICK);
        }

//        getRigidBody().setCollisionShape(new BoxShape(new Vector3f(5, 4.5f, 3)));
    }
}
