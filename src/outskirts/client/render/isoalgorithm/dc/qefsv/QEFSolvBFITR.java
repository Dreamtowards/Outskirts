package outskirts.client.render.isoalgorithm.dc.qefsv;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

import java.util.List;

import static outskirts.util.logging.Log.LOGGER;

public class QEFSolvBFITR {

    public static void main(String[] args) {

        LOGGER.info(
                getLeastSqBF(QEFSolvDUCJM3.TEST_Pi, QEFSolvDUCJM3.TEST_Ni)
        );

    }

    public static Vector3f getLeastSqBF(List<Vector3f> vts, List<Vector3f> nms) {
        float N = 8;

        Vector3f mp = new Vector3f();
        float mperrsq = Float.MAX_VALUE;

        AABB aabb = AABB.bounding(vts, null);
        Vector3f size = new Vector3f(aabb.max).sub(aabb.min);

        Vector3f tmp = new Vector3f();
        Vector3f tmp2 = new Vector3f();
        for (int i=0; i<N; i++) {
            for (int j=0; j<N; j++) {
                for (int k=0; k<N; k++) {
                    tmp.set(size).scale(i/N, j/N, k/N).add(aabb.min);

                    for (int n=0; n<vts.size(); n++) {
                        float distan = Vector3f.dot(tmp2.sub(vts.get(n)), nms.get(n));
                        float sq = distan*distan;
                        if (sq < mperrsq) {
                            mp.set(tmp);
                            mperrsq = sq;
                        }
                    }
                }
            }
        }
        return mp;
    }

}
