package outskirts.client.render.chunk;

import outskirts.block.Block;
import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.client.material.TextureAtlas;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.CollectionUtils;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.World;

import java.util.ArrayList;
import java.util.List;

public class ChunkModelGenerator {

    public Model buildModel(ChunkPos chunkPos, World world) {

        List<Float> positions = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        for (int x = 0;x < 16;x++) {
            for (int y = 0;y < 256;y++) {
                for (int z = 0;z < 16;z++) {
                    Block b = world.getBlock(chunkPos.x+x,y,chunkPos.z+z);
                    if (b != null) {
                        putCubeVtx(positions, normals, textureCoords, new Vector3f(x,y,z), chunkPos, world, b);
                    }
                }
            }
        }

        int vsz = positions.size()/3;
        return Loader.loadModelTAN(CollectionUtils.range(vsz), CollectionUtils.toArrayf(positions), CollectionUtils.toArrayf(textureCoords), CollectionUtils.toArrayf(normals));
    }

    private Vector3f[] fcsDls = new Vector3f[] {
            new Vector3f(1,0,0),
            new Vector3f(-1,0,0),
            new Vector3f(0,1,0),
            new Vector3f(0,-1,0),
            new Vector3f(0,0,1),
            new Vector3f(0,0,-1),
    };

    private void putCubeVtx(List<Float> positions, List<Float> normals, List<Float> textureCoords, Vector3f cubepos, ChunkPos chunkPos, World world, Block theBlock) {
        for (int i = 0;i < fcsDls.length;i++) {
            Vector3f fDir = fcsDls[i];
            Block nbBlock = world.getBlock((int)(chunkPos.x+cubepos.x + fDir.x), (int)(cubepos.y+fDir.y), (int)(chunkPos.z+cubepos.z+fDir.z));
            if (nbBlock == null) {
                putCubeFace(positions, normals, textureCoords, cubepos, i, theBlock.theTxFrag);
            }
        }
    }

    public void putCubeFace(List<Float> positions, List<Float> normals, List<Float> textureCoords, Vector3f cubepos, int faceIdx, TextureAtlas.Fragment txFrag) {
        final int FACE_FNUM = 6*3;  // numFloat components of a QuadFace(2triangles)  i.e. 2*3*vec3 = 6*vec3 floats.
        int vci = 0;
        float[] rp = ModelRenderer.MODEL_CUBE.attribute(0).data;
        float[] rn = ModelRenderer.MODEL_CUBE.attribute(2).data;
        for (int i = 0;i < FACE_FNUM;i++) {
            positions.add((rp[faceIdx*FACE_FNUM+i]+1f)/2f + Vector3f.get(cubepos, vci));
            normals.add(rn[faceIdx*FACE_FNUM+i]);
            vci++;
            vci %= 3;
        }
        float[] tmpRawTx = ModelRenderer.MODEL_CUBE.attribute(1).data;
        for (int i = 0;i < 12;) {
            textureCoords.add(tmpRawTx[i++]*txFrag.SCALE.x + txFrag.OFFSET.x);
            textureCoords.add(tmpRawTx[i++]*txFrag.SCALE.y + txFrag.OFFSET.y);
        }
    }

}
