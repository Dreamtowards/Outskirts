package outskirts.entity;

import outskirts.client.material.Texture;
import outskirts.init.Models;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.util.Side;

public class EntityGeoShape extends Entity {

    public EntityGeoShape(CollisionShape shape) {
        setRegistryID("geo_shape");

        if (Side.CURRENT == Side.CLIENT) {
            getMaterial().setDiffuseMap(Texture.UNIT);
            if (shape instanceof BoxShape) {
                getMaterial().setModel(Models.GEO_CUBE);
                tmp_boxSphere_scale.set(((BoxShape)shape).getHalfExtent());
            } else if (shape instanceof SphereShape) {
                getMaterial().setModel(Models.GEO_SPHERE);
                tmp_boxSphere_scale.scale(((SphereShape)shape).getRadius());
            }
        }

        getRigidBody().setCollisionShape(shape);
    }
}
