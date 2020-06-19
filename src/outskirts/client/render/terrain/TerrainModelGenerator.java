package outskirts.client.render.terrain;

import outskirts.client.material.ModelData;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.terrain.Terrain;

public class TerrainModelGenerator {

    public ModelData generateModel(Terrain terrain) {
        int[] indices = new int[Terrain.DENSITY*Terrain.DENSITY * 6];  // every terrain point needs 2*triangle
        float[] positions = new float[(Terrain.DENSITY+1)*(Terrain.DENSITY+1) * 3];
        float[] textureCoords = new float[positions.length/3*2];
        float[] normals = new float[positions.length];

        // gen pos/tex/norm
        int posptr = 0, texptr = 0, normptr = 0;
        for (float z = 0;z <= Terrain.DENSITY;z++) {
            for (float x = 0;x <= Terrain.DENSITY;x++) {
                float wrdX = terrain.x + x/Terrain.DENSITY*Terrain.SIZE;
                float wrdZ = terrain.z + z/Terrain.DENSITY*Terrain.SIZE;

                positions[posptr++] = wrdX;
                positions[posptr++] = gt_height(terrain, wrdX, wrdZ);
                positions[posptr++] = wrdZ;

                textureCoords[texptr++] = x/Terrain.DENSITY;
                textureCoords[texptr++] = 1f - (z/Terrain.DENSITY);

                float gridsz = 1f / Terrain.DENSITY * Terrain.SIZE; // grid size
                float hL = gt_height(terrain, wrdX-gridsz, wrdZ);
                float hR = gt_height(terrain, wrdX+gridsz, wrdZ);
                float hF = gt_height(terrain, wrdX, wrdZ-gridsz);
                float hB = gt_height(terrain, wrdX, wrdZ+gridsz);
                Vector3f norm = new Vector3f(hL-hR, 2f, hF-hB).normalize();
                normals[normptr++] = norm.x;
                normals[normptr++] = norm.y;
                normals[normptr++] = norm.z;
            }
        }

        // gen indices
        int VERT_CT_LIN = Terrain.DENSITY+1; // vert count in row
        int i = 0;
        for (int z = 0;z < Terrain.DENSITY;z++) {
            for (int x = 0;x < Terrain.DENSITY;x++) {

                indices[i++] = z*VERT_CT_LIN + x;
                indices[i++] = z*VERT_CT_LIN + (x+1);
                indices[i++] = (z+1)*VERT_CT_LIN + (x+1);
                indices[i++] = (z+1)*VERT_CT_LIN + (x+1);
                indices[i++] = (z+1)*VERT_CT_LIN + x;
                indices[i++] = z*VERT_CT_LIN + x;
            }
        }

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
