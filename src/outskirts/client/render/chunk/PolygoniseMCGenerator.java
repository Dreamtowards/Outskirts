package outskirts.client.render.chunk;

import outskirts.block.Block;
import outskirts.client.material.TextureAtlas;
import outskirts.client.render.VertexBuffer;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.mx.VertexUtil;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.Arrays;
import java.util.Objects;

public class PolygoniseMCGenerator {

    public static void putPolygonise(VertexBuffer vbuf, World world, Vector3f blockpos) {

//        if (true ) return;
        Block blk = world.getBlock(blockpos);
        if (blk == null) return;

        MarchingCubes.marching(0.0f, (x, y, z) -> {
            Block b = world.getBlock(blockpos.x + x, blockpos.y + y, blockpos.z + z);
            return b != null && !b.isTranslucent() ? b.v : -0.5f;
        }, (v, prm) -> {
            // later to MOD pos.xz with 16. WorldPos for gen the UV.
            vbuf.positions.add(Maths.mod(blockpos.x, 16)+v.x +.5f);
            vbuf.positions.add(          blockpos.y        +v.y +.5f);
            vbuf.positions.add(Maths.mod(blockpos.z, 16)+v.z +.5f);

            vbuf.normals.add(0f);
            vbuf.normals.add(1f);
            vbuf.normals.add(0f);

            float[] dv = MarchingCubes.tbVert[prm.dvertidx];
            int nID = Block.REGISTRY.indexOf(world.getBlock(blockpos.x + dv[0], blockpos.y + dv[1], blockpos.z + dv[2]).getRegistryID());

            vbuf.textureCoords.add((float)nID);
            vbuf.textureCoords.add(0f);
        });


//        int tableidx = MarchingCubes.cubeidx(0.5f, (x, y, z) -> {
//            Block b = world.getBlock(blockpos.x+x, blockpos.y+y, blockpos.z+z);
//            return b != null && !b.isTranslucent() ? b.v : 1;
//        });
//        if (tableidx == 0) return;
//
//        int[] tri = MarchingCubes.tbTri[tableidx];
//        for (int edgeIdx : tri) {
//            int[] edge = MarchingCubes.tbEdges[edgeIdx];
//
//            Vector3f edgeP1 = Vector3f.set(new Vector3f(), MarchingCubes.tbVert[edge[0]]);
//            Vector3f edgeP2 = Vector3f.set(new Vector3f(), MarchingCubes.tbVert[edge[1]]);
//
//            Vector3f p = edgeP1.add(Vector3f.sub(edgeP2, edgeP1, null).scale(0.5f));
//            vbuf.positions.add(Maths.mod(blockpos.x, 16)+p.x +.5f);
//            vbuf.positions.add(blockpos.y                  +p.y +.5f);
//            vbuf.positions.add(Maths.mod(blockpos.z, 16)+p.z +.5f);
//
//            vbuf.normals.add(0f);
//            vbuf.normals.add(1f);
//            vbuf.normals.add(0f);
//
//            vbuf.textureCoords.add(0f);
//            vbuf.textureCoords.add(1f);
//        }
    }

}
