package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.audio.AudioSource;
import outskirts.client.render.Texture;
import outskirts.init.Sounds;
import outskirts.util.Colors;
import outskirts.util.ResourceLocation;

public class GuiButton extends Gui {

    private static AudioSource BUTTON_AUDIO = AudioSource.alfGenSource();

    static Texture TEX_BUTTON_BACKGROUND = Loader.loadTexture(new ResourceLocation("textures/gui/button/background.png").getInputStream());
    static Texture TEX_BUTTON_BACKGROUND_HOVER = Loader.loadTexture(new ResourceLocation("textures/gui/button/background_hover.png").getInputStream());
    static Texture TEX_BUTTON_BACKGROUND_PRESSED = Loader.loadTexture(new ResourceLocation("textures/gui/button/background_pressed.png").getInputStream());

    private GuiText text = addGui(new GuiText());

    // tmp tool constructor
    public GuiButton(String text) {
        getText().setText(text);
        getText().addLayoutorAlignParentRR(0.5f, 0.5f);

        setWidth(100); //250
        setHeight(32); //40

        initOnMouseDownClickSound(this);

        addOnDrawListener(e -> {

            getText().getTextColor().set(isHover() || isPressed() ? Colors.YELLOW : Colors.WHITE);

            drawButtonBackground(this);
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

    public static void initOnMouseDownClickSound(Gui g) {
        g.addOnPressedListener(e -> {
            playClickSound();
        });
    }

    public static void drawButtonBackground(Gui g) {
        drawCornerStretchTexture(
                g.isPressed() ? TEX_BUTTON_BACKGROUND_PRESSED :
                        g.isHover() ? TEX_BUTTON_BACKGROUND_HOVER :
                                TEX_BUTTON_BACKGROUND, g, 6);
    }

    // needs a rename.?
    public static void drawButtonTexture(Texture texture, float x, float y, float width, float height) {
        float halfWidth = width/2;

        float halfTexWidth = halfWidth * texture.getHeight()/height / texture.getWidth(); // todo how? why..?

        drawTexture(texture, x, y, halfWidth, height,              0, 0, halfTexWidth, 1f);
        drawTexture(texture, x+halfWidth, y, halfWidth, height, 1f-halfTexWidth, 0, halfTexWidth, 1f);
    }
}
