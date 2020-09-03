package outskirts.client.gui.inspection;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiPadding;
import outskirts.client.gui._pending.GuiExpander;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.entity.Entity;
import outskirts.util.Colors;

public class GuiInspectionEntity extends Gui {

    public Entity theEntity;

    public GuiInspectionEntity(Entity theEntity) {
        this.theEntity = theEntity;
        setWrapChildren(true);

        addChildren(new GuiColumn().addChildren(
          new GuiPadding(Insets.fromLTRB(8,8,8,4)).setContent(
            new GuiExpander("GIRigidbody").setContent(
              new GuiIRigidbody(theEntity.getRigidBody())
            )
          ),
          new GuiPadding(Insets.fromLTRB(8,4,8,4)).setContent(
            new GuiExpander("GIMaterial").setContent(
              new GuiIMaterial(theEntity.getMaterial())
            )
          ),
          new GuiPadding(Insets.fromLTRB(8,4,8,8)).setContent(
            new GuiButton("Remove").exec(g -> {
              g.addOnClickListener(e -> {
                  Outskirts.getWorld().removeEntity(theEntity);
              });
            })
          )
        ));

    }


}
