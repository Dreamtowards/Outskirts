package outskirts.client.gui.inspection;

import javafx.util.Pair;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiContainer;
import outskirts.client.gui.GuiLinearLayout;
import outskirts.client.gui.inspection.setter.GuiSetterScalars;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.vector.Vector2f;

public class GuiIRigidbody extends Gui {

    private RigidBody theRigidbody;

    public GuiIRigidbody(RigidBody theRigidbody) {
        this.theRigidbody = theRigidbody;


//        Gui inspRigidbody = inspectionFields.addGui(new GuiContainer(new Insets(4, 4, 4, 4)))
//                .addGui(new GuiLinearLayout(new Vector2f(0, 1.2f)));
//        inspRigidbody.setWrapChildren(true);
//        {   // Rigidbody
//            GuiSetterScalars rbOrigin = GuiSetterScalars.forVector3f("Origin", () -> currentEntity.getRigidBody().transform().origin);
//            GuiSetterScalars rbGravity = GuiSetterScalars.forVector3f("Gravity", () -> currentEntity.getRigidBody().getGravity());
//            GuiSetterScalars rbMass = new GuiSetterScalars("Mass", new Pair<>(() -> currentEntity.getRigidBody().getMass(), f -> currentEntity.getRigidBody().setMass(f)));
//            GuiSetterScalars rbRestitution = new GuiSetterScalars("Restitution", new Pair<>(() -> currentEntity.getRigidBody().getRestitution(), f -> currentEntity.getRigidBody().setRestitution(f)));
//            GuiSetterScalars rbFriction = new GuiSetterScalars("Friction", new Pair<>(() -> currentEntity.getRigidBody().getFriction(), f -> currentEntity.getRigidBody().setFriction(f)));
//
//            inspRigidbody.addGui(rbOrigin);
//            inspRigidbody.addGui(rbGravity);
//            inspRigidbody.addGui(rbMass);
//            inspRigidbody.addGui(rbRestitution);
//            inspRigidbody.addGui(rbFriction);
//        }
    }
}
