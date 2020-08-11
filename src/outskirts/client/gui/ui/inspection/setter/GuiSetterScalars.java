package outskirts.client.gui.ui.inspection.setter;

import javafx.util.Pair;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiTextBox;
import outskirts.util.Colors;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiSetterScalars extends Gui {

    public GuiSetterScalars(String title, Pair<Supplier<Float>, Consumer<Float>>... fgsetters) {
        setHeight(16);
        float startX = 100;
        for (Pair<Supplier<Float>, Consumer<Float>> pair : fgsetters) {
            GuiTextBox g = addGui(new GuiTextBox("0"));
            g.setWidth(80);
            g.addOnDrawListener(e -> {
                if (pair.getKey().get() != Float.parseFloat(g.getText().getText()))
                    g.getText().setText(String.valueOf(pair.getKey().get()));
            });
            g.getText().addOnTextChangedListener(e -> {
                pair.getValue().accept(Float.parseFloat(g.getText().getText()));
            });
            g.setRelativeX(startX);
            startX += 8 + g.getWidth();
        }
        setWidth(startX);
        setHeight(16);
        addOnDrawListener(e -> {
            drawString(title, getX(), getY(), Colors.WHITE);
        });
    }

    public static GuiSetterScalars forVector3f(String title, Supplier<Vector3f> v) {
        return new GuiSetterScalars(title,
                new Pair<>(() -> v.get().x, f -> v.get().x=f),
                new Pair<>(() -> v.get().y, f -> v.get().y=f),
                new Pair<>(() -> v.get().z, f -> v.get().z=f)
        );
    }

    public static GuiSetterScalars forVector4f(String title, Supplier<Vector4f> v) {
        return new GuiSetterScalars(title,
                new Pair<>(() -> v.get().x, f -> v.get().x=f),
                new Pair<>(() -> v.get().y, f -> v.get().y=f),
                new Pair<>(() -> v.get().z, f -> v.get().z=f),
                new Pair<>(() -> v.get().w, f -> v.get().w=f)
        );
    }

}
