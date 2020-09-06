package outskirts.client.gui.inspection.rb;

import outskirts.client.gui.*;
import outskirts.client.gui.GuiExpander;
import outskirts.client.gui.inspection.num.GuiIBasis;
import outskirts.client.gui.inspection.num.GuiIScalar;
import outskirts.client.gui.inspection.num.GuiIVector3f;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.physics.dynamics.RigidBody;

import java.util.function.Consumer;

public class GuiIRigidbody extends Gui {

    public GuiIRigidbody(RigidBody theRigidbody) {

        Consumer<GuiText> titleprop = g -> {g.setWidth(100);g.setRelativeY(3);};

        addChildren(
          new GuiPadding(Insets.fromLTRB(8,0,0,0)).setContent(
            new GuiColumn().addChildren(
              new GuiExpander("Transform").setContent(
                new GuiColumn().addChildren(
                  new GuiRow().addChildren(
                    new GuiText("Origin").exec(titleprop),
                    new GuiIVector3f(theRigidbody.transform().origin)
                  ),
                  new GuiRow().addChildren(
                    new GuiText("Basis").exec(titleprop),
                    new GuiIBasis(theRigidbody.transform().basis)
                  )
                )
              ),
              new GuiExpander("CollisionShape").setContent(
                new GuiText(theRigidbody.getCollisionShape().getClass().getSimpleName())
              ),
              new GuiText("AABB").exec((GuiText g) -> {
                  g.addOnClickListener(e -> {
                      g.setText("AABB: "+theRigidbody.getAABB());
                  });
              }),
              new GuiRow().addChildren(
                new GuiText("Gravity").exec(titleprop),
                new GuiIVector3f(theRigidbody.getGravity())
              ),
              new GuiRow().addChildren(
                new GuiText("LinVel").exec(titleprop),
                new GuiIVector3f(theRigidbody.getLinearVelocity())
              ),
              new GuiRow().addChildren(
                new GuiText("AngVel").exec(titleprop),
                new GuiIVector3f(theRigidbody.getAngularVelocity())
              ),
              new GuiRow().addChildren(
                new GuiText("Mass").exec(titleprop),
                new GuiIScalar(theRigidbody::getMass, theRigidbody::setMass)
              ),
              new GuiRow().addChildren(
                new GuiText("LinDamping").exec(titleprop),
                new GuiIScalar(theRigidbody::getLinearDamping, theRigidbody::setLinearDamping)
              ),
              new GuiRow().addChildren(
                new GuiText("AngDamping").exec(titleprop),
                new GuiIScalar(theRigidbody::getAngularDamping, theRigidbody::setAngularDamping)
              ),
              new GuiRow().addChildren(
                new GuiText("Friction").exec(titleprop),
                new GuiIScalar(theRigidbody::getFriction, theRigidbody::setFriction)
              ),
              new GuiRow().addChildren(
                new GuiText("Restitution").exec(titleprop),
                new GuiIScalar(theRigidbody::getRestitution, theRigidbody::setRestitution)
              )
            )
          )
        );

//        new GuiPadding(5).addChildren(
//          new GuiColumn().addChildren(
//            new Expander("Transform").setContent(
//              new Column().addChildren(
//                new GuiRow().addChildren(
//                  new GuiSizedBox(100, 20).addChildren(new Text("Origin")),
//                  new GuiIVector3f()
//                ),
//                new GuiRow().addChildren(
//                  new GuiSizedBox(100, 20).addChildren(new Text("Basis")),
//                  new GuiIBasis()
//                )
//              )
//            ),
//            new GuiRow().addChildren(
//              new GuiSizedBox(100, 20).addChildren(new Text("Friction")),
//              new GuiIScalar()
//            ),
//            new GuiRow().addChildren(
//              new GuiSizedBox(100, 20).addChildren(new Text("LinearDamping")),
//              new GuiIScalar()
//            )
//          )
//        );




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
