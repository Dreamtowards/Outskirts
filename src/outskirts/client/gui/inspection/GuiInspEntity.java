package outskirts.client.gui.inspection;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiText;
import outskirts.entity.Entity;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

public class GuiInspEntity extends Gui {

    public static GuiInspEntity INSTANCE = new GuiInspEntity();

    public Entity currentEntity;



    {
        addOnDrawListener(e -> {
            if (Outskirts.isKeyDown(GLFW.GLFW_KEY_P)) {  // picking
                Vector3f ray = Maths.calculateWorldRay(Outskirts.getMouseX(), Outskirts.getMouseY(), Outskirts.getWidth(), Outskirts.getHeight(), Outskirts.renderEngine.getProjectionMatrix(), Outskirts.renderEngine.getViewMatrix());

                for (Entity entity : Outskirts.getWorld().getEntities()) {
//                    if (entity.getRigidBody().getCollisionShape() instanceof ConcaveShape) continue;
                    if (Maths.intersectRayAabb(Outskirts.getCamera().getPosition(), ray, entity.getRigidBody().getAABB(), new Vector2f())) {
                        currentEntity = entity;
                        break;
                    }
                }
            }
            if (currentEntity == null)
                return;
            // outline.
            Outskirts.renderEngine.getModelRenderer().drawOutline(currentEntity.getRigidBody().getAABB(), Colors.DARK_GREEN);

            drawString(String.format(
                    "currentEntity: \n" +
                    "Class: %s. \n" +
                    "registryID: %s\n" +
                    "Position: %s\n",
                    currentEntity.getClass(), currentEntity.getRegistryID(), currentEntity.getPosition()), getX(), getY(), Colors.WHITE);
        });
    }

}
