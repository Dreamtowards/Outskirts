package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.Colors;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

public class GuiDebugPhys extends Gui {

    public static final GuiDebugPhys INSTANCE = new GuiDebugPhys();

    public boolean showBoundingBox;
    public boolean showVelocities;
    public boolean showContactPoints;

    {
        addOnDrawListener(e -> {

            if (Outskirts.getWorld() == null)
                return;

            if (showContactPoints) {
                for (CollisionManifold manifold : Outskirts.getWorld().dynamicsWorld.getCollisionManifolds()) {
                    for (int i = 0; i < manifold.getNumContactPoints(); i++) {
                        Gui.drawWorldpoint(manifold.getContactPoint(i).pointOnB, (x, y) -> {
                            drawRect(Colors.RED, x, y, 4, 4);
                        });
                    }
                }
            }

            for (CollisionObject co : Outskirts.getWorld().dynamicsWorld.getCollisionObjects()) {
                RigidBody body = (RigidBody)co;
                if (showBoundingBox) {
                    // draw AABB
                    boolean inBroadphase = false, inNarrowphase = false;
                    for (CollisionManifold manifold : Outskirts.getWorld().dynamicsWorld.getBroadphase().getOverlappingPairs()) {
                        if (manifold.containsBody(body)) inBroadphase = true;
                    }
                    for (CollisionManifold manifold : Outskirts.getWorld().dynamicsWorld.getCollisionManifolds()) {
                        if (manifold.containsBody(body)) inNarrowphase = true;
                    }
                    Vector4f color = Colors.GREEN;
                    if (inNarrowphase) color = Colors.YELLOW;
                    else if (inBroadphase) color = Colors.RED;
                    Outskirts.renderEngine.getModelRenderer().drawOutline(body.getAABB(), color);

                    // oriention
                    Outskirts.renderEngine.getModelRenderer().drawLine(body.transform().origin,
                            new Vector3f(body.transform().origin).add(Matrix3f.transform(body.transform().basis, new Vector3f(Vector3f.UNIT_Y))), Colors.RED);
                }

                if (showVelocities) {
                    // angular velocity
                    Outskirts.renderEngine.getModelRenderer().drawLine(body.transform().origin,
                            new Vector3f(body.transform().origin).add(body.getAngularVelocity()), Colors.DARK_RED);
                    // linear  velocity
                    Outskirts.renderEngine.getModelRenderer().drawLine(body.transform().origin,
                            new Vector3f(body.transform().origin).add(body.getLinearVelocity()), Colors.DARK_GREEN);
                }
            }
        });
    }

}
