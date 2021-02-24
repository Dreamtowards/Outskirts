package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.CollectionUtils;
import outskirts.util.Ref;
import outskirts.util.mx.VertexUtil;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.List;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

public class ChunkMeshGen {

    public static Vector3f rsp = new Vector3f();

    public static VertexBuffer buildModel(RenderSection rs, Ref<float[]> vertm) {
        rsp.set(rs.position());

        World world = Outskirts.getWorld();
        if (world==null) return null;
        Octree node = Outskirts.getWorld().getOctree(rs.position());
        if (node==null) return null;

        VertexBuffer vbuf = DualContouring.contouring(node);

//        if (false) {
        // Stitching. FACEs.
        Octree[] fp = new Octree[2];
        for (int i = 0;i < 3;i++) {
            // we choose stitching towards min-er. cuz miner are always exists. chunk-load is from min to max in each axis.
            Octree neib = world.getOctree(vec3(rs.position()).addv(i, 16));
            if (neib != null) {
                int beginfi = vbuf.positions.size();
                fp[0] = node;
                fp[1] = neib;
                DualContouring.doFaceContour(fp, i, vbuf);

                // vertex-axis-pos correct offset. because Octrees are all rel-pos.
                for (int fi = beginfi;fi < vbuf.positions.size();fi+=3) {
                    float f = vbuf.positions.get(fi+i);
                    if (f < 8) {
                        vbuf.positions.set(fi+i, 16+f);
                    }
                }
            }
        }

        // Stitching ADJACENTs.
        Octree[] ea = new Octree[4];
        for (int i = 0;i < 3;i++) {
            boolean comple = true;
            int[] adja = DualContouring.EDGE_ADJACENT[i*2];
            for (int j = 0;j < 4;j++) {
                Octree neib = world.getOctree(vec3(rs.position()).addScaled(16, Octree.VERT[adja[j]]));
                if (neib == null) {
                    comple = false;
                    break;
                }
                ea[j] = neib;
            }

            if (comple) {
                int beginfi = vbuf.positions.size();
                DualContouring.doEdgeContour(ea, i, vbuf);

                for (int fi = beginfi;fi < vbuf.positions.size();fi+=3) {
                    for (int j = 0;j < 3;j++) {
                        if (j == i) continue;
                        float f = vbuf.positions.get(fi+j);
                        if (f < 8) {
                            vbuf.positions.set(fi+j, 16+f);
                        }
                    }
                }
            }
        }


//        }

        // 问题还是出现在 adaptive contour dc 的实现问题。和LOD无关 因为非LOD的不同分辨率也可能过渡出问题。
        // 通过 contour-validTriangle 定位问题

        // 测试：section内 不同分辨率: 仍然有问题 看上去一样的问题。

        // 还原问题，定位问题

        // single-leaf-parent merge test


        vbuf.inituvnorm(false);

        VertexUtil.smoothnorm(vbuf);

        vertm.value = CollectionUtils.toArrayf((List)vbuf.verttags);
        assert vertm.value.length == vbuf.positions.size()/3;

        return vbuf;
    }

}
