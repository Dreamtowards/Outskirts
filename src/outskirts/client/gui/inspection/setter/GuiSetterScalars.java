package outskirts.client.gui.inspection.setter;

import javafx.util.Pair;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiTextFieldNumerical;
import outskirts.util.Colors;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiSetterScalars extends Gui {

    public GuiSetterScalars(String title, Pair<Supplier<Float>, Consumer<Float>>... fgsetters) {
        setHeight(16);
        float startX = 80;
        for (Pair<Supplier<Float>, Consumer<Float>> pair : fgsetters) {
            GuiTextFieldNumerical field = addGui(new GuiTextFieldNumerical());
            field.setWidth(80);
            field.addOnDrawListener(e -> {
                if (pair.getKey().get() != field.getValue())
                    field.setText(String.valueOf(pair.getKey().get()));
                drawString(title, getX(), getY(), Colors.WHITE);
            });
            field.addOnTextChangedListener(e -> {
                pair.getValue().accept(field.getValue());
            });
            field.setRelativeX(startX);
            startX += 8 + field.getWidth();
        }
        setWidth(startX);
        setHeight(16);
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
