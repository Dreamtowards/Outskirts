package outskirts.client.gui.inspection.mtl;

import outskirts.client.Loader;
import outskirts.client.gui.*;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.client.material.Texture;
import outskirts.event.EventPriority;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiITexture extends Gui {

    private Texture theTex;

    public GuiITexture(Supplier<Texture> getter, Consumer<Texture> setter) {

        GuiTextBox loadfilePath;
        addChildren(
          new GuiColumn().addChildren(
            new GuiImage().exec((GuiImage g) -> {
                g.setWidth(80);
                g.setHeight(80);
                attachListener(OnTexChangedEv.class, e -> {
                    g.setTexture(theTex);
                });
                g.addOnDrawListener(e -> {
                    drawRectBorder(Colors.WHITE20, g, 2);
                });
            }),
            new GuiText().exec((GuiText g) -> {
                attachListener(OnTexChangedEv.class, e -> {
                    g.setText(String.format("#%s @wh:%sx%s", theTex.textureID(), theTex.getWidth(), theTex.getHeight()));
                });
            }),
            new GuiRow().addChildren(
              loadfilePath=(GuiTextBox)new GuiTextBox().exec((GuiTextBox g) -> {
                  g.setHeight(22);
                  g.getText().setRelativeY(3);
              }),
              new GuiButton("LFile").exec(g -> {
                  g.setHeight(22);
                  g.setWidth(60);
                  g.addOnClickListener(e -> {
                      try {
                          setter.accept(Loader.loadTexture(new FileInputStream(loadfilePath.getText().getText())));
                          loadfilePath.getText().setText("");
                          Log.LOGGER.info("Loaded.");
                      } catch (FileNotFoundException ex) {
                          ex.printStackTrace();
                      }
                  });
              })
            ),
            new GuiComboBox().exec((GuiComboBox g) -> {
                g.setHeight(22);
                g.addOnPressedListener(e -> {
                    g.getOptions().clear();
                    for (File file : FileUtils.listFiles(new File("src/assets/outskirts/materials"))) {
                        if (file.isFile() && file.getName().endsWith(".png"))
                            g.getOptions().add(new GuiText(file.getName()).exec(gitem -> gitem.setTag(file)));
                    }
                }).priority(EventPriority.HIGH);
                g.addOnSelectedListener(e -> {
                    File file = (File)g.getOptions().get(g.getSelectedIndex()).getTag();
                    try {
                        setter.accept(Loader.loadTexture(new FileInputStream(file)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            })
          )
        );

        addOnDrawListener(e -> {
            // sync texture.
            Texture remoteTex = getter.get();
            if (remoteTex != theTex) {
                theTex=remoteTex;
                performEvent(new OnTexChangedEv());
            }
        }).priority(EventPriority.HIGH);
    }

    private static class OnTexChangedEv extends GuiEvent {}
}
