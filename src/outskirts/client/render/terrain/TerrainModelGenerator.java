package outskirts.client.render.terrain;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.ModelData;
import outskirts.client.material.Texture;
import outskirts.entity.EntityGeoShape;
import outskirts.entity.EntityMaterialDisplay;
import outskirts.init.Models;
import outskirts.init.Textures;
import outskirts.physics.collision.shapes.concave.TriangleMeshShape;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.terrain.Terrain;

import static outskirts.world.terrain.Terrain.SIZE;
import static outskirts.world.terrain.Terrain.VERT_SIZE;

public class TerrainModelGenerator {

    public ModelData generateModel(Terrain terrain) {
        int[] indices = new int[VERT_SIZE*VERT_SIZE * 6];  // every terrain point needs 2*triangle
        float[] positions = new float[(VERT_SIZE+1)*(VERT_SIZE+1) * 3];
        float[] textureCoords = new float[positions.length/3*2];
        float[] normals = new float[positions.length];

        // gen pos/tex/norm
        float d = (float)SIZE/VERT_SIZE;
        int posptr = 0, texptr = 0, normptr = 0;
        for (float z = 0;z <= SIZE;z+=d) {
            for (float x = 0;x <= SIZE;x+=d) {
                float vWrdX = terrain.x + x;
                float vWrdZ = terrain.z + z;

                positions[posptr++] = vWrdX;
                positions[posptr++] = gt_height(terrain, vWrdX, vWrdZ);
                positions[posptr++] = vWrdZ;

                textureCoords[texptr++] = x/SIZE;
                textureCoords[texptr++] = 1f - (z/SIZE);

//                float gridsz = 1f / Terrain.DENSITY * Terrain.SIZE; // grid size
                float hL = gt_height(terrain, vWrdX-d, vWrdZ);
                float hR = gt_height(terrain, vWrdX+d, vWrdZ);
                float hF = gt_height(terrain, vWrdX, vWrdZ-d);
                float hB = gt_height(terrain, vWrdX, vWrdZ+d);
                Vector3f norm = new Vector3f(hL-hR, 2f, hF-hB).normalize();
                normals[normptr++] = norm.x;
                normals[normptr++] = norm.y;
                normals[normptr++] = norm.z;
            }
        }

        // gen indices
        int VERT_CT_LIN = VERT_SIZE+1; // vert count in row
        int i = 0;
        for (int z = 0;z < VERT_SIZE;z++) {
            for (int x = 0;x < VERT_SIZE;x++) {

                indices[i++] = z*VERT_CT_LIN + x;     // "left-front"
                indices[i++] = (z+1)*VERT_CT_LIN + x; // left-back
                indices[i++] = (z+1)*VERT_CT_LIN + (x+1); // right-back
                indices[i++] = (z+1)*VERT_CT_LIN + (x+1); // right-back
                indices[i++] = z*VERT_CT_LIN + (x+1); // right-top
                indices[i++] = z*VERT_CT_LIN + x;     // left-front
            }
        }

        EntityGeoShape eFloor = new EntityGeoShape(new BoxShape(new Vector3f(100,10,100)));
        Outskirts.getWorld().addEntity(eFloor);
        eFloor.tmp_boxSphere_scale.set(1,1,1);
        eFloor.getRigidBody().setMass(0).setRestitution(0.1f);
        eFloor.getRigidBody().setInertiaTensorLocal(0, 0, 0);
        eFloor.getRigidBody().setCollisionShape(new TriangleMeshShape(indices, positions));

        return new ModelData(indices, positions, textureCoords, normals);
    }

    private static float gt_height(Terrain terrain, float wrdX, float wrdZ) {
        if (wrdX < terrain.x + Terrain.SIZE && wrdZ < terrain.z + Terrain.SIZE) {
            return terrain.getHeight(wrdX, wrdZ);
        } else {
            return terrain.getWorld().getTerrainHeight(wrdX, wrdZ);
        }
    }
}
