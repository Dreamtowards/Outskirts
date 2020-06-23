package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.material.ModelData;
import outskirts.client.render.Light;
import outskirts.entity.EntityModel;
import outskirts.physics.collision.shapes.concave.TriangleMeshShape;
import outskirts.util.Identifier;
import outskirts.world.World;
import outskirts.world.WorldClient;

public class SceneIniter {

    public static void init(WorldClient world) {



        Light lightSun = new Light();
        lightSun.getPosition().set(100, 100, 100);
        lightSun.getDirection().set(-1, -1, -1).normalize();
        lightSun.getColor().set(5, 5, 5);
        Light.calculateApproximateAttenuation(100, lightSun.getAttenuation());
        world.lights.add(lightSun);


        EntityModel model = new EntityModel();
        world.addEntity(model);

        ModelData[] mdat = new ModelData[1];
        model.getMaterial()
                .setModel(Loader.loadOBJ(new Identifier("materials/mount/untitled2.obj").getInputStream(), mdat))
                .setDiffuseMap(Textures.CONTAINER);

        model.getRigidBody()
                .setCollisionShape(new TriangleMeshShape(mdat[0].indices, mdat[0].positions))
                .setMass(0);


    }

}
