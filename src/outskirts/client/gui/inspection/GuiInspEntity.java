package outskirts.client.gui.inspection;

import javafx.util.Pair;
import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.inspection.setter.GuiSetterScalars;
import outskirts.entity.Entity;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

public class GuiInspEntity extends Gui {

    public static Entity NULL_ENTITY = new Entity() { };

    public static GuiInspEntity INSTANCE = new GuiInspEntity();

    public Entity currentEntity = NULL_ENTITY;

    private GuiText entityinfo = addGui(new GuiText().setHeight(16));
    private GuiText rbTitle = addGui(new GuiText(" > RB >>").setHeight(16));
    private GuiSetterScalars rbOrigin = addGui(GuiSetterScalars.forVector3f("Origin", () -> currentEntity.getRigidBody().transform().origin));
    private GuiSetterScalars rbGravity = addGui(GuiSetterScalars.forVector3f("Gravity", () -> currentEntity.getRigidBody().getGravity()));
    private GuiSetterScalars rbMass = addGui(new GuiSetterScalars("Mass", new Pair<>(() -> currentEntity.getRigidBody().getMass(), f -> currentEntity.getRigidBody().setMass(f))));
    private GuiSetterScalars rbRestitution = addGui(new GuiSetterScalars("Restitution", new Pair<>(() -> currentEntity.getRigidBody().getRestitution(), f -> currentEntity.getRigidBody().setRestitution(f))));
    private GuiSetterScalars rbFriction = addGui(new GuiSetterScalars("Friction", new Pair<>(() -> currentEntity.getRigidBody().getFriction(), f -> currentEntity.getRigidBody().setFriction(f))));

    {
        addLayoutorLayoutLinear(Vector2f.UNIT_Y);
        addLayoutorWrapChildren(8, 8, 8, 8);

        addOnDrawListener(e -> {
            if (Outskirts.isKeyDown(GLFW.GLFW_KEY_P)) {  // picking
                Vector3f ray = Maths.calculateWorldRay(Outskirts.getMouseX(), Outskirts.getMouseY(), Outskirts.getWidth(), Outskirts.getHeight(), Outskirts.renderEngine.getProjectionMatrix(), Outskirts.renderEngine.getViewMatrix());

                for (Entity entity : Outskirts.getWorld().getEntities()) {
                    if (Maths.intersectRayAabb(Outskirts.getCamera().getPosition(), ray, entity.getRigidBody().getAABB(), new Vector2f())) {
                        currentEntity = entity;
                        break;
                    }
                }
            }

            // outline.
            Outskirts.renderEngine.getModelRenderer().drawOutline(currentEntity.getRigidBody().getAABB(), Colors.DARK_GREEN);

            entityinfo.setText(String.format("currentEntity: [%s] %s", currentEntity.getRegistryID(), currentEntity.getClass()));
        });
    }

}
