package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.audio.AudioSource;
import outskirts.client.material.Texture;
import outskirts.init.Sounds;
import outskirts.util.Colors;
import outskirts.util.ResourceLocation;
import outskirts.util.vector.Vector4f;

public class GuiButton extends Gui {

    private static AudioSource BUTTON_AUDIO = AudioSource.alfGenSource();

    public static Texture TEXTURE_BUTTON_NORMAL = Loader.loadTexture(new ResourceLocation("textures/gui/weights/button_normal.png").getInputStream());
    static Texture TEXTURE_BUTTON_HOVER = Loader.loadTexture(new ResourceLocation("textures/gui/weights/button_hover.png").getInputStream());
    static Texture TEXTURE_BUTTON_DISABLE = Loader.loadTexture(new ResourceLocation("textures/gui/weights/button_disable.png").getInputStream());

    private GuiText text = addGui(new GuiText());

    // tmp tool constructor
    public GuiButton(String text) {
        getText().setText(text);
        getText().addLayoutorAlignParentRR(0.5f, 0.5f);

        setWidth(100); //250
        setHeight(32); //40

        addOnClickListener(e -> {
            playClickSound();
        });

        addOnDrawListener(e -> {
            Vector4f color = Colors.WHITE;
            Texture tex = TEXTURE_BUTTON_NORMAL;

            if (isEnable()) {
                if (isHover()) {
                    color = Colors.YELLOW;
                    tex = TEXTURE_BUTTON_HOVER;
                }
            } else {
                color = Colors.GRAY;
                tex = TEXTURE_BUTTON_DISABLE;
            }

            getText().getTextColor().set(color);
            drawButtonTexture(tex, getX(), getY(), getWidth(), getHeight());

        });
    }

    public GuiText getText() {
        return text;
    }

    public static void playClickSound() {
        BUTTON_AUDIO.stop();
        BUTTON_AUDIO.unqueueAllBuffers();
        BUTTON_AUDIO.queueBuffers(Sounds.GUI_CLICK);
        BUTTON_AUDIO.play();
    }

    // needs a rename.?
    public static void drawButtonTexture(Texture texture, float x, float y, float width, float height) {
        float halfWidth = width/2;

        float halfTexWidth = halfWidth * texture.getHeight()/height / texture.getWidth(); // todo how? why..?

        drawTexture(texture, x, y, halfWidth, height,              0, 0, halfTexWidth, 1f);
        drawTexture(texture, x+halfWidth, y, halfWidth, height, 1f-halfTexWidth, 0, halfTexWidth, 1f);
    }
}
