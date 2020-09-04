package outskirts.client.gui.inspection;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiPadding;
import outskirts.client.gui.GuiTextBox;
import outskirts.client.gui.GuiExpander;
import outskirts.client.gui.inspection.material.GuiIMaterial;
import outskirts.client.gui.layout.GuiColumn;
import outskirts.client.gui.layout.GuiRow;
import outskirts.entity.Entity;
import outskirts.entity.EntityStaticMesh;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GuiIEntity extends Gui {

    //todo: Window -TaskBar

    private static final class InspField<T extends Entity> {
        Class<T> basetype;
        String title;
        Function<T, Gui> provider;
    }

    private static List<InspField> INSPECTION_FIELDS = new ArrayList<>();

    public static <T extends Entity> void registerInspectionField(Class<T> basetype, String title, Function<T, Gui> provider) {
        InspField f = new InspField();
        f.basetype=basetype;
        f.title=title;
        f.provider=provider;
        INSPECTION_FIELDS.add(f);
    }

    static {
        {
            registerInspectionField(Entity.class, "GIRigidbody", entity ->
                    new GuiIRigidbody(entity.getRigidBody())
            );

            registerInspectionField(Entity.class, "GIMaterial", entity ->
                    new GuiIMaterial(entity.getMaterial())
            );

            registerInspectionField(Entity.class, "commextra", entity ->
                    new GuiButton("Remove").exec(g -> {
                        g.addOnClickListener(e -> {
                            Outskirts.getWorld().removeEntity(entity);
                        });
                    })
            );

            registerInspectionField(EntityStaticMesh.class, "+Insp Model", estaticmesh -> {
                GuiTextBox path;
                return new GuiRow().addChildren(
                  path=new GuiTextBox(),
                  new GuiButton("LoadOBJ").exec(g -> {
                      g.addOnClickListener(e -> {
                          try {
                              estaticmesh.setModel(Loader.loadOBJ(new FileInputStream(path.getText().getText())));
                              path.getText().setText("");
                          } catch (Exception ex) {
                              ex.printStackTrace();
                          }
                      });
                  })
                );
            });
        }
    }

    public GuiIEntity(Entity theEntity) {

        addChildren(new GuiColumn().exec(g -> {
            for (InspField inspField : INSPECTION_FIELDS) {
                if (inspField.basetype.isAssignableFrom(theEntity.getClass())) {
                    g.addChildren(new GuiPadding(Insets.fromLTRB(8, 8, 8, 4)).setContent(
                      new GuiExpander(inspField.title).setContent(
                        (Gui)inspField.provider.apply(theEntity)
                      )
                    ));
                }
            }
        }));
    }


}
