package outskirts.client.render.isoalgorithm.dc.qefsv;

import outskirts.util.mx.VertexUtil;
import outskirts.util.vector.Vector3f;

import java.util.List;

public class QEFSolvBFAVG {


    public static Vector3f doAvg(List<Vector3f> vs, List<Vector3f> ns) {
        return VertexUtil.centeravg(vs, null);
    }

}
