package outskirts.client.gui.inspection;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.inspection.mtl.GuiIMaterial;
import outskirts.client.gui.inspection.rb.GuiIRigidbody;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.entity.Entity;
import outskirts.event.EventPriority;
import outskirts.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GuiIEntity extends Gui {

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
                    new GuiIMaterial(entity.getRenderPerferences())
            );

            registerInspectionField(Entity.class, "Common", entity ->
                    new GuiButton("Remove").exec(g -> {
                        g.addOnClickListener(e -> {
                            Outskirts.getWorld().removeEntity(entity);
                        });
                    })
            );

            registerInspectionField(Entity.class, "+EXInsp Model", estaticmesh -> {
                GuiTextBox path;
                return new GuiColumn().addChildren(
                  new GuiRow().addChildren(
                    path=new GuiTextBox(),
                    new GuiButton("LoadOBJ").exec(g -> {
                        path.setHeight(25);
                        path.getText().setRelativeY(4.5f);
                        g.setHeight(25);
                        g.addOnClickListener(e -> {
                            try {
                                estaticmesh.setModel(Loader.loadOBJ(new FileInputStream(path.getText().getText())));
                                path.getText().setText("");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    })
                  ),
                  new GuiComboBox().exec((GuiComboBox g) -> {
                      g.setHeight(25);
                      g.addOnPressedListener(e -> {
                          g.getOptions().clear();
                          for (File file : FileUtils.listFiles(new File("src/assets/outskirts/materials"))) {
                              if (file.isFile() && file.getName().endsWith(".obj"))
                                  g.getOptions().add(new GuiText(file.getName()).exec(gitem -> gitem.setTag(file)));
                          }
                      }).priority(EventPriority.HIGH);
                      g.addOnSelectedListener(e -> {
                          File file = (File)g.getOptions().get(g.getSelectedIndex()).getTag();
                          try {
                              estaticmesh.setModel(Loader.loadOBJ(new FileInputStream(file)));
                          } catch (Throwable ex) {
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
