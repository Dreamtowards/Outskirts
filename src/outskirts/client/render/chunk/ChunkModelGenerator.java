package outskirts.client.render.chunk;

import outskirts.block.Block;
import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.client.material.TextureAtlas;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.mx.VertexUtil;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.World;

import java.util.ArrayList;
import java.util.List;

public class ChunkModelGenerator {

    public Model buildModel(ChunkPos chunkpos, World world) {

        VertexBuffer vbuf = new VertexBuffer();

        for (int x = 0;x < 16;x++) {
            for (int y = 0;y < 30;y++) {
                for (int z = 0;z < 16;z++) {
                    Block b = world.getBlock(chunkpos.x+x, y,chunkpos.z+z);
                    if (b != null) {
                        Vector3f blockpos = new Vector3f(chunkpos.x+x,y,chunkpos.z+z);

                        b.getVertexData(world, blockpos, vbuf);
                    }
                    {
                        Vector3f blockpos = new Vector3f(chunkpos.x+x,y,chunkpos.z+z);
                        PolygoniseMCGenerator.putPolygonise(vbuf, world, blockpos);
                    }
                }
            }
        }

//        VertexUtil.hardnorm(vbuf);
//        VertexUtil.smoothnorm(vbuf);
        return Loader.loadModelT(vbuf);
    }

    private static Vector3f[] CUBE_FACES_DIRS = new Vector3f[] {
            new Vector3f( 1,0,0),
            new Vector3f(-1,0,0),
            new Vector3f(0, 1,0),
            new Vector3f(0,-1,0),
            new Vector3f(0,0, 1),
            new Vector3f(0,0,-1),
    };

    public static void putCubeVtx(VertexBuffer vbuf, Vector3f blockpos, World world, TextureAtlas.Fragment txFrag) {
        for (int i = 0;i < CUBE_FACES_DIRS.length;i++) {
            Vector3f faceDir = CUBE_FACES_DIRS[i];
            Block neibgherBlock = world.getBlock((int)(blockpos.x + faceDir.x), (int)(blockpos.y+faceDir.y), (int)(blockpos.z+faceDir.z));
            if (neibgherBlock == null || neibgherBlock.isTranslucent()) {
                putCubeFace(vbuf, blockpos, i, txFrag, Matrix3f.IDENTITY, Vector3f.ZERO);
            }
        }
    }

    public static void putCubeFace(VertexBuffer vbuf, Vector3f blockpos, int iface, TextureAtlas.Fragment txFrag, Matrix3f rot, Vector3f preposoff) {
        final int FACE_FN = 6*3;  // numFloat components of a QuadFace(2triangles)  i.e. 2*3*vec3 = 6*vec3 floats.
        float[] rawpos = ModelRenderer.MODEL_CUBE.attribute(0).data;
        float[] rawtx = ModelRenderer.MODEL_CUBE.attribute(1).data;
        float[] rawnorm = ModelRenderer.MODEL_CUBE.attribute(2).data;
        for (int i = 0;i < 6;i++) {
            Vector3f pos = new Vector3f(
                    rawpos[iface*FACE_FN+i*3],
                    rawpos[iface*FACE_FN+i*3+1],
                    rawpos[iface*FACE_FN+i*3+2]);
            pos.add(preposoff);
            Matrix3f.transform(rot, pos);
            pos.add(1,1,1).scale(1/2f);  // to [0, 1]
            pos.add(Maths.mod(blockpos.x, 16), blockpos.y, Maths.mod(blockpos.z, 16));
            vbuf.positions.add(pos.x); vbuf.positions.add(pos.y); vbuf.positions.add(pos.z);

            Vector3f norm = new Vector3f(rawnorm[iface*FACE_FN+i*3], rawnorm[iface*FACE_FN+i*3+1], rawnorm[iface*FACE_FN+i*3+2]);
            vbuf.normals.add(norm.x);vbuf.normals.add(norm.y);vbuf.normals.add(norm.z);

            vbuf.textureCoords.add(rawtx[i*2]*txFrag.SCALE.x + txFrag.OFFSET.x);
            vbuf.textureCoords.add(rawtx[i*2+1]*txFrag.SCALE.y + txFrag.OFFSET.y);
        }
    }



    public static void putTallgrassCrossFaces(VertexBuffer vbuf, Vector3f blockpos, TextureAtlas.Fragment txFrag) {

        Matrix3f rot = new Matrix3f();

        Matrix3f.rotate(Maths.PI/4, Vector3f.UNIT_Y, rot);
        putCubeFace(vbuf, blockpos, 0, txFrag, rot, new Vector3f(-1,0,0));

        Matrix3f.rotate(-Maths.PI/4, Vector3f.UNIT_Y, rot);
        putCubeFace(vbuf, blockpos, 0, txFrag, rot, new Vector3f(-1,0,0));

    }
}
