package outskirts.client.render.chunk;

import outskirts.block.Block;
import outskirts.client.render.VertexBuffer;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.Arrays;

public class PolygoniseMCGenerator {

    public static void putPolygonise(VertexBuffer vbuf, World world, Vector3f blockpos) {

        int tableidx = MarchingCubes.tableidx((x, y, z) -> {
            Block b = world.getBlock(blockpos.x+x, blockpos.y+y, blockpos.z+z);
            return b != null && !b.isTranslucent();
        });
        if (tableidx == 0)
            return;

        int[] tri = MarchingCubes.tbTri[tableidx]; assert tri.length % 3 == 0;
        for (int edgeIdx : tri) {
            int[] edge = MarchingCubes.tbEdges[edgeIdx];

            Vector3f edgeP1 = Vector3f.set(new Vector3f(), MarchingCubes.tbVert[edge[0]]);
            Vector3f edgeP2 = Vector3f.set(new Vector3f(), MarchingCubes.tbVert[edge[1]]);

            Vector3f p = edgeP1.add(Vector3f.sub(edgeP2, edgeP1, null).scale(0.5f));
            vbuf.positions.add(Maths.mod(blockpos.x, 16)+p.x +.5f);
            vbuf.positions.add(blockpos.y                  +p.y +.5f);
            vbuf.positions.add(Maths.mod(blockpos.z, 16)+p.z +.5f);

            vbuf.normals.add(0f);
            vbuf.normals.add(1f);
            vbuf.normals.add(0f);

            vbuf.textureCoords.add(0f);
            vbuf.textureCoords.add(1f);
        }
    }

}
