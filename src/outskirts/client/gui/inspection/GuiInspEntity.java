package outskirts.client.gui.inspection;

import javafx.util.Pair;
import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiCollapse;
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

    {
        GuiCollapse collapseGui = addGui(new GuiCollapse());

        collapseGui.getTitleGui().addGui(new GuiText("˅ RigidBody"));

        {
            GuiSetterScalars rbOrigin = GuiSetterScalars.forVector3f("Origin", () -> currentEntity.getRigidBody().transform().origin);
            GuiSetterScalars rbGravity = GuiSetterScalars.forVector3f("Gravity", () -> currentEntity.getRigidBody().getGravity());
            GuiSetterScalars rbMass = new GuiSetterScalars("Mass", new Pair<>(() -> currentEntity.getRigidBody().getMass(), f -> currentEntity.getRigidBody().setMass(f)));
            GuiSetterScalars rbRestitution = new GuiSetterScalars("Restitution", new Pair<>(() -> currentEntity.getRigidBody().getRestitution(), f -> currentEntity.getRigidBody().setRestitution(f)));
            GuiSetterScalars rbFriction = new GuiSetterScalars("Friction", new Pair<>(() -> currentEntity.getRigidBody().getFriction(), f -> currentEntity.getRigidBody().setFriction(f)));


            collapseGui.getBodyGui().addLayoutorLayoutLinear(new Vector2f(0, 1.2f));
            collapseGui.getBodyGui().addLayoutorWrapChildren(4,4,4,4);
            collapseGui.getBodyGui().addGui(rbOrigin);
            collapseGui.getBodyGui().addGui(rbGravity);
            collapseGui.getBodyGui().addGui(rbMass);
            collapseGui.getBodyGui().addGui(rbRestitution);
            collapseGui.getBodyGui().addGui(rbFriction);
        }

    }
//    private GuiText entityinfo = addGui(new GuiText().setHeight(16));
    {
        addLayoutorLayoutLinear(new Vector2f(0, 1.2f));
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

//            entityinfo.setText(String.format("currentEntity: [%s] %s", currentEntity.getRegistryID(), currentEntity.getClass().getSimpleName()));
        });
    }

}
