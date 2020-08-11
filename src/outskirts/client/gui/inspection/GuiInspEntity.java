package outskirts.client.gui.inspection;

import javafx.util.Pair;
import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiLayoutLinear;
import outskirts.client.gui.GuiPadding;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.inspection.setter.GuiSetterScalars;
import outskirts.entity.Entity;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

public class GuiInspEntity extends Gui {

    public static Entity NULL_ENTITY = new Entity() { };
    static {
        NULL_ENTITY.getRigidBody().setCollisionShape(new SphereShape(0));
    }

    public static GuiInspEntity INSTANCE = new GuiInspEntity();




    public Entity currentEntity = NULL_ENTITY;

    private GuiLayoutLinear terms;

    {
        terms = addGui(new GuiPadding(new Insets(8, 8, 8, 8)))
                .addGui(new GuiLayoutLinear(new Vector2f(0, 1.2f)));
        terms.setWrapChildren(true);

        addOnDrawListener(e -> {
            if (Outskirts.isKeyDown(GLFW.GLFW_KEY_P)) {  // picking
                Vector3f ray = Maths.calculateWorldRay(Outskirts.getMouseX(), Outskirts.getMouseY(), Outskirts.getWidth(), Outskirts.getHeight(), Outskirts.renderEngine.getProjectionMatrix(), Outskirts.renderEngine.getViewMatrix());
                float mndist = Float.MAX_VALUE;
                Vector2f rg = new Vector2f();
                for (Entity entity : Outskirts.getWorld().getEntities()) {
                    if (Maths.intersectRayAabb(Outskirts.getCamera().getPosition(), ray, entity.getRigidBody().getAABB(), rg)) {
                        if (rg.x < mndist) {
                            currentEntity = entity;
                            mndist = rg.x;
                        }
                    }
                }
            }

            // outline.
            Outskirts.renderEngine.getModelRenderer().drawOutline(currentEntity.getRigidBody().getAABB(), Colors.DARK_GREEN);

//            entityinfo.setText(String.format("currentEntity: [%s] %s", currentEntity.getRegistryID(), currentEntity.getClass().getSimpleName()));
        });

        {
            Gui inspRigidbody = terms.addGui(new GuiPadding(new Insets(4, 4, 4, 4)))
                    .addGui(new GuiLayoutLinear(new Vector2f(0, 1.2f)));
            inspRigidbody.setWrapChildren(true);

            GuiSetterScalars rbOrigin = GuiSetterScalars.forVector3f("Origin", () -> currentEntity.getRigidBody().transform().origin);
            GuiSetterScalars rbGravity = GuiSetterScalars.forVector3f("Gravity", () -> currentEntity.getRigidBody().getGravity());
            GuiSetterScalars rbMass = new GuiSetterScalars("Mass", new Pair<>(() -> currentEntity.getRigidBody().getMass(), f -> currentEntity.getRigidBody().setMass(f)));
            GuiSetterScalars rbRestitution = new GuiSetterScalars("Restitution", new Pair<>(() -> currentEntity.getRigidBody().getRestitution(), f -> currentEntity.getRigidBody().setRestitution(f)));
            GuiSetterScalars rbFriction = new GuiSetterScalars("Friction", new Pair<>(() -> currentEntity.getRigidBody().getFriction(), f -> currentEntity.getRigidBody().setFriction(f)));

            inspRigidbody.addGui(rbOrigin);
            inspRigidbody.addGui(rbGravity);
            inspRigidbody.addGui(rbMass);
            inspRigidbody.addGui(rbRestitution);
            inspRigidbody.addGui(rbFriction);

        }

    }

}
