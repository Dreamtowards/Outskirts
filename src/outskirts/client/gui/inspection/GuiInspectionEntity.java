package outskirts.client.gui.inspection;

import javafx.util.Pair;
import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiLinearLayout;
import outskirts.client.gui.GuiContainer;
import outskirts.client.gui._pending.GuiExpander;
import outskirts.client.gui.inspection.setter.GuiSetterScalars;
import outskirts.entity.Entity;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

public class GuiInspectionEntity extends Gui {

    public Entity theEntity;

    private GuiLinearLayout inspectionFields;

    public GuiInspectionEntity(Entity theEntity) {
        this.theEntity = theEntity;

        setWrapChildren(true);
        inspectionFields = addGui(new GuiLinearLayout(new Vector2f(0, 1.2f)));
        inspectionFields.setWrapChildren(true);


        GuiExpander exRigidbody = inspectionFields.addGui(new GuiContainer(new Insets(8, 4, 8, 4))).addGui(new GuiExpander()); {
            exRigidbody.getTitle().setText("Rigidbody");
            exRigidbody.getContent().addGui(new GuiIRigidbody(theEntity.getRigidBody()));
        }
        inspectionFields.addGui(new GuiButton("Material Window"));

        GuiButton btnRemoveEntity = inspectionFields.addGui(new GuiButton("Remove entity"));
        btnRemoveEntity.addOnClickListener(e -> {
            Outskirts.getWorld().removeEntity(theEntity);
        });

    }


}
