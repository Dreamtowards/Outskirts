package outskirts.client.render.isoalgorithm.dc;

import outskirts.client.render.VertexBuffer;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.Raycastable;
import outskirts.physics.collision.shapes.concave.BvhTriangleMeshShape;
import outskirts.util.CollectionUtils;
import outskirts.util.Ref;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

import java.util.function.Consumer;

import static outskirts.util.CollectionUtils.range;

public class DCOctreeSampler {

    public static Octree fromSDF(Vector3f min, float size, TrifFunc f, int depthcap) {
        return sample(min, size, lf -> {
            Octree.sampleSDF(lf, f);
            lf.computefp();
        }, 0, depthcap);
    }

    public static Octree fromMESH(Vector3f min, float size, Raycastable mesh, int depthcap) {
        return sample(min, size, lf -> {
            Octree.sampleMESH(lf, mesh);
            lf.computefp();
        }, 0, depthcap);
    }

    private static Octree sample(Vector3f min, float size, Consumer<Octree.Leaf> samp, int currdep, int depthcap) {
        if (currdep == depthcap) {  // do sample.
            Octree.Leaf lf = new Octree.Leaf(min, size);
            samp.accept(lf);
            return lf;
        } else if (currdep < depthcap) {  // recurise until reach the depthcap.
            Octree.Internal intern = new Octree.Internal();
            float subsize = size/2f;
            Vector3f submin = new Vector3f();
            for (int i = 0;i < 8;i++) {
                submin.set(min).addScaled(subsize, Octree.VERT[i]);
                intern.child(i, sample(submin, subsize, samp, currdep+1, depthcap));
            }
            return intern;
        } else throw new IllegalStateException();
    }
}
