package outskirts.client.render.chunk;

import outskirts.block.Block;
import outskirts.client.material.TextureAtlas;
import outskirts.client.render.VertexBuffer;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
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

        TextureAtlas.Fragment txFrag = Block.REGISTRY.get(blk.getRegistryID()).theTxFrag;
        Objects.requireNonNull(txFrag);

        int beginvi = vbuf.positions.size() / 3;
        MarchingCubes.marching(0.0f, (x, y, z) -> {
            Block b = world.getBlock(blockpos.x + x, blockpos.y + y, blockpos.z + z);
            return b != null && !b.isTranslucent() ? b.v : -1;
        }, v -> {
            // later to MOD pos.xz with 16. WorldPos for gen the UV.
            vbuf.positions.add(blockpos.x+v.x +.5f);
            vbuf.positions.add(blockpos.y+v.y +.5f);
            vbuf.positions.add(blockpos.z+v.z +.5f);

            vbuf.normals.add(0f);
            vbuf.normals.add(1f);
            vbuf.normals.add(0f);

            vbuf.textureCoords.add(0f);
            vbuf.textureCoords.add(1f);
        });

        // make tris hard norm. (default.
        VertexUtil.hardnorm(vbuf, beginvi*3);

        // Gen the UV.
        Vector3f N = new Vector3f();
        Vector3f wpos = new Vector3f();
        for (int i = beginvi;i < vbuf.textureCoords.size() / 2;i++) {
            Vector3f.set(N, vbuf.normals::get, i*3);
            Vector3f.abs(N);
            int mxi = N.x > N.y ? (N.x > N.z ? 0 : 2) : (N.y > N.z ? 1 : 2);

            Vector3f.set(wpos, vbuf.positions::get, i*3);
            float u, v;
//            if (mxi == 0) {  // uvPlanarX
//                u = 1f - wpos.z;
//                v = wpos.y;
//            } else if (mxi == 1) {
//                u = wpos.x;
//                v = 1f - wpos.z;
//            } else {
//                u = wpos.x;
//                v = wpos.y;
//            }
            u = wpos.x;
            v = wpos.z;

            u = Maths.mod(u, 1.01f);
            v = Maths.mod(v, 1.01f);
//            u %= 1.001f;
//            v %= 1.001f;

//            u *= txFrag.SCALE.x;
//            v *= txFrag.SCALE.y;
//
//            u += txFrag.OFFSET.x;
//            v += txFrag.OFFSET.y;

            vbuf.textureCoords.set(i*2, u);
            vbuf.textureCoords.set(i*2+1, v);
        }

        // MOD the pos.xz with 16. (back).
        for (int i = beginvi*3;i < vbuf.positions.size();i+=3) {

            vbuf.positions.set(i,   vbuf.positions.get(i) - blockpos.x + Maths.mod(blockpos.x,   16));
            vbuf.positions.set(i+2, vbuf.positions.get(i+2) - blockpos.z + Maths.mod(blockpos.z, 16));
        }



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
