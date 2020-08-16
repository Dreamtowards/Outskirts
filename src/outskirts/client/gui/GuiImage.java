package outskirts.client.gui;

import outskirts.client.material.Texture;

public class GuiImage extends Gui {

    private Texture texture = Texture.UNIT;

    {
        addOnDrawListener(e -> {
            drawTexture(texture, getX(), getY(), getWidth(), getHeight());
        });
    }

    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
