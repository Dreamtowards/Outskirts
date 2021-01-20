package outskirts.client.render.isoalgorithm.dc;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

public class DCOctreeGen {

    public static Octree fromSDF(Vector3f min, float size, TrifFunc f, int currdep, int depthcap) {
        assert currdep <= depthcap;
        if (currdep == depthcap) {
            Octree.Leaf leaf = new Octree.Leaf(min, size);
            DualContouring.sampleSDF(leaf, f);
            leaf.computefp();
            return leaf;
        } else {
            Octree.Internal intern = new Octree.Internal();
            float subsize = size / 2f;
            Vector3f submin = new Vector3f();
            for (int i = 0; i < 8; i++) {
                submin.set(min).addScaled(subsize, Octree.VERT[i]);
                intern.child(i, fromSDF(submin, subsize, f, currdep + 1, depthcap));
            }
            return intern;
        }
    }

}
