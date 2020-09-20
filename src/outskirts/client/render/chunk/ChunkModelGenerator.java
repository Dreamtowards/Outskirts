package outskirts.client.render.chunk;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.CollectionUtils;
import outskirts.util.vector.Vector3f;
import outskirts.world.Chunk;
import outskirts.world.ChunkPos;
import outskirts.world.World;

import java.util.ArrayList;
import java.util.List;

public class ChunkModelGenerator {

    public Model buildModel(ChunkPos chunkPos, World world) {

        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        for (int x = 0;x < 16;x++) {
            for (int y = 0;y < 256;y++) {
                for (int z = 0;z < 16;z++) {
                    byte b = world.getBlock(chunkPos.x+x,y,chunkPos.z+z);
                    if (b != 0) {
                        putCubeVtx(positions, normals, new Vector3f(x,y,z), chunkPos, world);
                    }
                }
            }
        }

        int vsz = positions.size()/3;
        return Loader.loadModelTAN(CollectionUtils.range(vsz), CollectionUtils.toArrayf(positions), new float[vsz*2], CollectionUtils.toArrayf(normals));
    }

    private Vector3f[] fcsDls = new Vector3f[] {
            new Vector3f(1,0,0),
            new Vector3f(-1,0,0),
            new Vector3f(0,1,0),
            new Vector3f(0,-1,0),
            new Vector3f(0,0,1),
            new Vector3f(0,0,-1),
    };

    private void putCubeVtx(List<Float> positions, List<Float> normals, Vector3f cubepos, ChunkPos chunkPos, World world) {
        for (int i = 0;i < fcsDls.length;i++) {
            Vector3f fDir = fcsDls[i];
            byte b = world.getBlock((int)(chunkPos.x+cubepos.x + fDir.x), (int)(cubepos.y+fDir.y), (int)(chunkPos.z+cubepos.z+fDir.z));
            if (b == 0) {
                putCubeFace(positions, normals, cubepos, i);
            }
        }
    }

    public void putCubeFace(List<Float> positions, List<Float> normals, Vector3f cubepos, int faceIdx) {
        final int FACE_FNUM = 6*3;
        int vci = 0;
        float[] rp = ModelRenderer.MODEL_CUBE.attribute(0).data;
        float[] rn = ModelRenderer.MODEL_CUBE.attribute(2).data;
        for (int i = 0;i < FACE_FNUM;i++) {
            positions.add((rp[faceIdx*FACE_FNUM+i]+1f)/2f + Vector3f.get(cubepos, vci));
            normals.add(rn[faceIdx*FACE_FNUM+i]);
            vci++;
            vci %= 3;
        }
    }

}
