package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.material.ex.ModelData;
import outskirts.client.render.Light;
import outskirts.entity.EntityStaticMesh;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.collision.shapes.convex.ConvexHullShape;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.physics.extras.quickhull.QuickHull;
import outskirts.util.mex.PlacementUtils;
import outskirts.world.WorldClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SceneIniter {

    public static void init(WorldClient world) {




//        {
            EntityStaticMesh planeGround = new EntityStaticMesh();
            world.addEntity(planeGround);
        try {
            planeGround.setModel(Loader.loadOBJ(new FileInputStream("/Users/dreamtowards/Documents/Modeling/Blender/islandblend.obj")));

            planeGround.getMaterial().setDiffuseMap(Loader.loadTexture(new FileInputStream("/Users/dreamtowards/Documents/Modeling/Blender/island_diff.png")));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//              planeGround.tmp_boxSphere_scale.set(100, 10, 100);
//            planeGround.getRigidBody().setCollisionShape(new BoxShape(100, 1, 100)).setMass(0).getGravity().set(0,0,0);
            planeGround.getRigidBody().transform().origin.set(0, 0, 0);
//
//
//        }

//        try {
//            EntityModel gravestone = new EntityModel();
//            world.addEntity(gravestone);
//            ModelData[] mdatptr = new ModelData[1];
//            gravestone.setModel(Loader.loadOBJ(new FileInputStream("/Users/dreamtowards/Documents/Modeling/Blender/cbin.obj"), mdatptr, true));//.setModel(Models.GEO_CUBE)
//            gravestone.getMaterial().setDiffuseMap(Loader.loadTexture(new FileInputStream("/Users/dreamtowards/Documents/Modeling/Blender/cbin_diff.png")));
//            gravestone.getRigidBody().setCollisionShape(new ConvexHullShape(QuickHull.quickHull(mdatptr[0].positions)));
////            gravestone.getRigidBody().setCollisionShape(new SphereShape(1));
//            gravestone.getRigidBody().setMass(50);
//            gravestone.getRigidBody().transform().origin.set(10, 0, 0);
//            PlacementUtils.setOnTopOf(gravestone.getRigidBody(), planeGround.getRigidBody());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        {
//            EntityModel gravestone = new EntityModel();
//            world.addEntity(gravestone);
//            ModelData[] mdatptr = new ModelData[1];
//            gravestone.getMaterial().setModel(Loader.loadOBJ(new Identifier("elements/stool/stool.obj").getInputStream(), mdatptr, true))//.setModel(Models.GEO_CUBE)
//                    .setDiffuseMap(Loader.loadTexture(new Identifier("elements/stool/stool_diff.png").getInputStream()));
//            gravestone.getRigidBody().setCollisionShape(new ConvexHullShape(QuickHull.quickHull(mdatptr[0].positions)));
//            PlacementUtils.setOnTopOf(gravestone.getRigidBody(), planeGround.getRigidBody());
//        }

        {
            // stack tst

//            for (int i = 0;i < 0;i++) {
//                EntityModel ebox = new EntityModel();
//                world.addEntity(ebox);
//                ebox.tmp_boxSphere_scale.set(3-i*0.2f,1, 3-i*0.2f).set(1,1,1).scale(1+i*0.2f);
////                ebox.getRigidBody().setCollisionShape(new BoxShape(3-i*0.2f,1, 3-i*0.2f)).setMass(40);
//                ebox.getRigidBody().setCollisionShape(new SphereShape(1+i*0.2f)).setMass(40);
//                ebox.getRigidBody().transform().origin.y = i*4+1;
//                ebox.setModel(Models.GEO_SPHERE);
//                ebox.getMaterial().setDiffuseMap(Textures.FLOOR);
//            }
        }


//        EntityModel model = new EntityModel();
//        world.addEntity(model);
//        ModelData[] mdat = new ModelData[1];
//        model.getMaterial()
//                .setModel(Loader.loadOBJ(new Identifier("materials/mount/untitled2.obj").getInputStream(), mdat))
//                .setDiffuseMap(Textures.CONTAINER);
//        model.getRigidBody()
//                .setCollisionShape(new TriangleMeshShape(mdat[0].indices, mdat[0].positions))
//                .setMass(0);


    }

}
