package outskirts.client.gui.inspection.setter;

import javafx.util.Pair;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiTextFieldNumerical;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiSetterScalars extends Gui {

    public GuiSetterScalars(Pair<Supplier<Float>, Consumer<Float>>... fgsetters) {
        float startX = 0;
        for (Pair<Supplier<Float>, Consumer<Float>> pair : fgsetters) {
            GuiTextFieldNumerical field = addGui(new GuiTextFieldNumerical());
            field.addOnDrawListener(e -> {
                if (pair.getKey().get() != field.getValue())
                    field.setText(String.valueOf(pair.getKey().get()));
            });
            field.addOnTextChangedListener(e -> {
                pair.getValue().accept(field.getValue());
            });
            field.setRelativeX(startX);
            startX += 8 + field.getWidth();
        }
    }

    public static GuiSetterScalars forVector3f(Vector3f vec) {
        return new GuiSetterScalars(
                new Pair<>(() -> vec.x, f -> vec.x=f),
                new Pair<>(() -> vec.y, f -> vec.y=f),
                new Pair<>(() -> vec.z, f -> vec.z=f)
        );
    }

    public static GuiSetterScalars forVector4f(Vector4f vec) {
        return new GuiSetterScalars(
                new Pair<>(() -> vec.x, f -> vec.x=f),
                new Pair<>(() -> vec.y, f -> vec.y=f),
                new Pair<>(() -> vec.z, f -> vec.z=f),
                new Pair<>(() -> vec.w, f -> vec.w=f)
        );
    }

}
