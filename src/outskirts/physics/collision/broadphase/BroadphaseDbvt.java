package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.dynamics.RigidBody;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Dynamic-Bounding-Volume-Tree.
 *
 * Maintain a dynamic Bvh struct. AABB Tree.
 *
 * https://box2d.org/files/ErinCatto_DynamicBVH_GDC2019.pdf
 */
public class BroadphaseDbvt extends Broadphase {

    private static final float LEAF_VOLUME_MARGIN = 0.1f;

    private DbvtNode rootNode;

    @Override
    public void addObject(CollisionObject body) {
        DbvtNode leaf = DbvtNode.newLeaf(body);
        insertLeaf(leaf);
        body.broadphaseAttachment = leaf;
    }

    @Override
    public void removeObject(CollisionObject body) {
        removeLeaf((DbvtNode)body.broadphaseAttachment);
        body.broadphaseAttachment = null;
        removePairsContainingBody(body);
    }

    @Override
    public void updateAABB(CollisionObject body) {
        updateLeaf((DbvtNode)body.broadphaseAttachment, body.getAABB());
    }

    @Override
    public void calculateOverlappingPairs() {
        if (rootNode==null || rootNode.isLeaf())
            return;

        // collide whole Dbvt. keep/addnew all intersecting pairs. (pairs add only)
        collideNode(rootNode, null);

        // remove non-intersecting paris. (pairs remove only).
        getOverlappingPairs().removeIf(
                mf -> !AABB.intersects(mf.bodyA().getAABB(), mf.bodyB().getAABB())
        );
    }

//    {
//        Outskirts.getIngameGUI().addGui(new Gui().addOnDrawListener(e -> {
//            drawNode(rootNode,0);
//        }));
//    }
//    private static void drawNode(DbvtNode node, int d) {
//        if (node==null) return;
//        Outskirts.renderEngine.getModelRenderer().drawOutline(node.volume, node.isLeaf()?Colors.GREEN:Colors.WHITE);
//        Gui.drawWorldpoint(node.volume.max, (x,y) -> {
//            Gui.drawString(""+d, x,y,node.isLeaf()?Colors.GREEN:Colors.WHITE);
//        });
//        if (node.isInternal()) {
//            drawNode(node.child[0],d+1);
//            drawNode(node.child[1],d+1);
//        }
//    }


    private void collideNode(DbvtNode nodeA, DbvtNode nodeB) {
        if (nodeB == null) {  // not just collides two trees, but collides each trees's its themselves children.
            if (!nodeA.isInternal()) return;
            collideNode(nodeA.child[0], null);
            collideNode(nodeA.child[1], null);
            collideNode(nodeA.child[0], nodeA.child[1]);
        } else if (AABB.intersects(nodeA.volume, nodeB.volume)) {
            if (nodeA.isInternal() && nodeB.isInternal()) {
                collideNode(nodeA.child[0], nodeB.child[0]);
                collideNode(nodeA.child[0], nodeB.child[1]);
                collideNode(nodeA.child[1], nodeB.child[0]);
                collideNode(nodeA.child[1], nodeB.child[1]);
            } else if (nodeA.isInternal() && nodeB.isLeaf()) {
                collideNode(nodeA.child[0], nodeB);
                collideNode(nodeA.child[1], nodeB);
            } else if (nodeA.isLeaf() && nodeB.isInternal()) {
                collideNode(nodeA, nodeB.child[0]);
                collideNode(nodeA, nodeB.child[1]);
            } else if (nodeA.isLeaf() && nodeB.isLeaf()) {
                // add (new) Manifold if not extsting the (persistent) manifold.
                if (CollisionManifold.indexOf(getOverlappingPairs(), nodeA.body, nodeB.body) == -1) {
                    getOverlappingPairs().add(new CollisionManifold((RigidBody)nodeA.body, (RigidBody)nodeB.body));
                }
            }
        }
    }

    private void insertLeaf(DbvtNode leaf) {
        Objects.requireNonNull(leaf);
        if (rootNode == null)  { rootNode = leaf; rootNode.parent=null; return; }
        if (rootNode.isLeaf()) { rootNode = DbvtNode.newInternal(rootNode, leaf); rootNode.wrapChildVolume(); rootNode.parent=null; return; }
        DbvtNode sibling = rootNode;
        // find out a sibling leaf, which almost most closed to.
        while (!sibling.isLeaf()) {
            sibling = AABB.centdistanf(sibling.child[0].volume, leaf.volume) < AABB.centdistanf(sibling.child[1].volume, leaf.volume) ? sibling.child[0] : sibling.child[1];
        }
        // new a parent. put sibling and self-leaf in. and let the newparent to sibling-origin-parent's child's position.
        DbvtNode oriparen = sibling.parent;// int inoriIdx = sibling.inSiblingIdx();
        DbvtNode newparen = DbvtNode.newInternal(sibling, leaf);
        oriparen.replaceChild(sibling, newparen);

        assert oriparen.child[0]==newparen || oriparen.child[1]==newparen;

        // update superiors volume.
        DbvtNode n = leaf;
        while (n.parent != null) {
            if (n.parent.volume.containsEquals(n.volume))
                break;
            n.parent.wrapChildVolume();
            n = n.parent;
        }
    }

    private void removeLeaf(DbvtNode leaf) {
        Objects.requireNonNull(leaf);
        if (rootNode == leaf) { rootNode = null; return; }
        if (leaf.parent == rootNode) { rootNode = leaf.siblingNode(); rootNode.parent=null; return; }
        DbvtNode sibling = leaf.siblingNode();
        leaf.parent.parent.replaceChild(leaf.parent, sibling);

        DbvtNode n = sibling;
        while (n.parent != null) {
            n.parent.wrapChildVolume();

            n = n.parent;
        }
    }

    /**
     * updating tree (Movement Objects).
     * the updateing-method of Rebuild-sup-nodes-volumes(without 'rotate' tree) is not ok. that'll leads to low quality trees.
     * i.e. the Tree is lost control, when a leaf's body moveing fast / far away, the leaf is not actually closed to the current (fixed) sibling anymore. broke rule.
     * we uses -- Remove/re-insert.
     * this makes updating Correctly-Perform. when movement/updating Leafs, remove it first, and then put it in "Correct" place by insertLeaf().
     */
    private void updateLeaf(DbvtNode leaf, AABB newVolume) {
        if (leaf.volume.contains(newVolume))
            return;
        removeLeaf(leaf);

        leaf.volume.set(newVolume).grow(LEAF_VOLUME_MARGIN, LEAF_VOLUME_MARGIN, LEAF_VOLUME_MARGIN);

        insertLeaf(leaf);
    }



    private final static class DbvtNode {
        private AABB volume = new AABB();
        private DbvtNode parent;
        private DbvtNode[] child;      // non-null when is Internal-node. size=2
        private CollisionObject body;  // non-null when is Leaf-node.

        public static DbvtNode newLeaf(CollisionObject body) {  // leaf
            DbvtNode n = new DbvtNode();
            n.parent = null;  // set when been putted in a Internal-Node.
            n.volume.set(body.getAABB()); // when update.?
            n.body = body;
            return n;
        }

        public static DbvtNode newInternal(DbvtNode child0, DbvtNode child1) {  // internal
            DbvtNode n = new DbvtNode();
            n.child = new DbvtNode[2];
            n.child[0] = child0;
            n.child[1] = child1;
            child0.parent = n;
            child1.parent = n;
            return n;
        }

        // no matter with body/data. when children wasn't complete, just Leaf..
        public boolean isLeaf() {
            return child==null;
        }
        public final boolean isInternal() {
            return !isLeaf();
        }

        public final void wrapChildVolume() {
            AABB.merge(child[0].volume, child[1].volume, volume);
        }
        public void replaceChild(DbvtNode from, DbvtNode to) {  // may not a good name..
            if (child[0]==from) {child[0]=to; to.parent=this;}
            else if (child[1]==from) {child[1]=to; to.parent=this;}
            else throw new NoSuchElementException();
        }
        public DbvtNode siblingNode() {
            if (parent.child[0]==this) return parent.child[1];
            if (parent.child[1]==this) return parent.child[0];
            throw new NoSuchElementException();
        }
    }

}
