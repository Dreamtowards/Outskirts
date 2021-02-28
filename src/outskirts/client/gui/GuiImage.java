package outskirts.client.gui;

import outskirts.client.render.Texture;

public class GuiImage extends Gui {

    private Texture texture;

    public GuiImage() {
        this(Texture.UNIT);
    }

    public GuiImage(Texture textureIn) {
        setTexture(textureIn);

        addOnDrawListener(e -> {
            drawTexture(texture, getX(), getY(), getWidth(), getHeight());
        });
    }

    public final Texture getTexture() {
        return texture;
    }
    public final void setTexture(Texture texture) {
        this.texture = texture;
    }
}
