package outskirts.client.render.chunk;

import outskirts.client.material.ModelData;
import outskirts.util.CollectionUtils;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector3i;
import outskirts.world.chunk.Octree;

import java.util.ArrayList;
import java.util.List;

public class ChunkModelGeneratorCube {

    static List<Vector3f> positions = new ArrayList<>();
    static List<Float> textureCoords = new ArrayList<>();
    static List<Vector3f> normals = new ArrayList<>();

    public static ModelData generate(Octree octree, Vector3i base) {
        positions.clear();
        textureCoords.clear();
        normals.clear();

        genOct(octree, base.x, base.y, base.z);

//        Log.info("GEN: pos.len=" + positions.size() + " base: " + base);
        return new ModelData(null,
                CollectionUtils.toArray(ChunkModelGeneratorMC.toFloatList(positions)),
                CollectionUtils.toArray(textureCoords),
                CollectionUtils.toArray(ChunkModelGeneratorMC.toFloatList(normals))
        );
    }

    private static void genOct(Octree octree, float x, float y, float z) {

        if (octree.hasChildren()) {
            float size = octree.size();
            for (int i = 0; i < Octree.LENGTH; i++) {
                Octree child = octree.child(i);

//                genOct(child,
//                        x + Octree.CHILDREN_POSITIONS[i].x*size*16,
//                        y + Octree.CHILDREN_POSITIONS[i].y*size*16,
//                        z + Octree.CHILDREN_POSITIONS[i].z*size*16
//                );

            }
        } else if (octree.hasBody()) {
            ChunkModelGeneratorMC.putCube(positions, textureCoords, normals, x, y, z, 16*octree.size());
        }
    }

}
