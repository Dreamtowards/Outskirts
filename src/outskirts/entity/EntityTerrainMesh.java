package outskirts.entity;

import outskirts.block.Block;
import outskirts.client.material.Model;
import outskirts.init.Textures;
import outskirts.init.ex.Models;
import outskirts.physics.collision.shapes.concave.BvhTriangleMeshShape;

public class EntityTerrainMesh extends Entity {

    public EntityTerrainMesh() {
        setRegistryID("terrainmesh");

        setModel(Models.GEO_CUBE);
        rigidbody().setMass(0);
        material().setDiffuseMap(Block.TEXTURE_ATLAS.getAtlasTexture());
//        material().setDiffuseMap(Textures.GRASS);
    }

    @Override
    public void setModel(Model model) {
        super.setModel(model);

        rigidbody().setCollisionShape(new BvhTriangleMeshShape(
                getModel().indices,
                getModel().attribute(0).data
        ));
    }
}
