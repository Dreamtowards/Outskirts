package outskirts.physics.collision.shapes.concave;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

/**
 * BoundingVolumeHierarchy TriangleMeshShape.
 * 30k Triangles
 */
public class BvhTriangleMeshShape extends TriangleMeshShape {

    private BvhNode rootNode;

    public BvhTriangleMeshShape(int[] indices, float[] positions) {
        super(indices, positions);
    }

    @Override
    protected void processMeshModified() {

        rebuildBVH();
    }

    private void rebuildBVH() {
        rootNode = null;
        for (int i = 0;i < trianglesCount();i++) {
            insertLeaf(i);
        }
    }

    private void insertLeaf(int triangleIndex) {
        BvhNode leaf = BvhNode.newLeaf(triangleIndex, getTriangle(triangleIndex, TMP_TRIANGE));
        if (rootNode == null) { rootNode=leaf; leaf.parent=null; return; }
        if (rootNode.isLeaf()) { rootNode=BvhNode.newInternal(rootNode, leaf); rootNode.wrapChildVolume(); rootNode.parent=null; return; }

        BvhNode supnode = rootNode;
        while (supnode.isInternal()) {
            supnode = AABB.centdistanf(supnode.child[0].volume, leaf.volume) < AABB.centdistanf(supnode.child[1].volume, leaf.volume)
                      ? supnode.child[0] : supnode.child[1];
        }
        // isLeaf().
        supnode.parent.replaceChild(supnode, BvhNode.newInternal(supnode, leaf));

        // rebuild superiors volumes.
        BvhNode n = leaf;
        while (n.parent != null) {
            if (n.parent.volume.containsEquals(n.volume))
                break;
            n.parent.wrapChildVolume();
            n=n.parent;
        }
    }

    @Override
    public void collideTriangles(AABB aabb, BiConsumer<Integer, Vector3f[]> oncollide) {
        collideNode(rootNode, aabb, triangleIndex -> {
            oncollide.accept(
                    triangleIndex,
                    getTriangle(triangleIndex, getTriangle(triangleIndex, TMP_TRIANGE))
            );
        });
    }

    private void collideNode(BvhNode node, AABB aabb, IntConsumer oncollide) {
        if (!AABB.intersects(node.volume, aabb))
            return;
        if (node.isInternal()) {
            collideNode(node.child[0], aabb, oncollide);
            collideNode(node.child[1], aabb, oncollide);
        } else { // isLeaf()
            oncollide.accept(node.triangleIndex);
        }
    }

    private static final class BvhNode {

        private AABB volume = new AABB();

        private BvhNode parent;

        private int triangleIndex = -1;  // body
        private BvhNode[] child;    // child size=2

        static BvhNode newLeaf(int triangleIndex, Vector3f[] triangle) {
            BvhNode leaf = new BvhNode();
            leaf.triangleIndex=triangleIndex;
            AABB.bounding(triangle, leaf.volume);
            return leaf;
        }
        static BvhNode newInternal(BvhNode node1, BvhNode node2) {
            BvhNode internal = new BvhNode();
            internal.child = new BvhNode[2];
            internal.child[0] = node1;
            internal.child[1] = node2;
            node1.parent = internal;
            node2.parent = internal;
            return internal;
        }

        boolean isLeaf() {
            return triangleIndex != -1;
        }
        boolean isInternal() {
            return child != null;
        }

        void replaceChild(BvhNode from, BvhNode to) {
            if (child[0] == from) { child[0] = to; to.parent=this; }
            else if (child[1] == from) { child[1] = to; to.parent=this; }
            else throw new NoSuchElementException();
        }
        void wrapChildVolume() {
            AABB.merge(child[0].volume, child[1].volume, volume);
        }
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return dest.set(rootNode.volume);
    }
}
