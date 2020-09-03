package outskirts.client.gui;

import outskirts.event.EventHandler;
import outskirts.util.Validate;
import outskirts.util.logging.Log;

// WrapChild-Padding.
// GuiWrapper
/**
 * GuiPadding always should had only one child.
 * GuiPadding moves the child position, and self size(width/height) to fit the padding.
 * if needs multi children, or child needs align parent, should have a Gui to wrap them.
 */
public class GuiPadding extends Gui implements Gui.Contentable {

    private Insets padding = new Insets();

    public GuiPadding(Insets paddingIn) {
        getPadding().set(paddingIn);
        setContent(new Gui());

        addOnLayoutListener(this::onLayout0);
    }

    @EventHandler
    private void onLayout0(OnLayoutEvent event) {
        if (size() != 1)
            Log.LOGGER.info("GuiPadding children: " + size());
        Validate.isTrue(!isWrapChildren(), "WrapChildren is not allowed on GuiPadding. its leads padding size wrong.");

        Gui content = getContent();
        content.setRelativeXY(padding.left, padding.top);
        setWidth(padding.left + content.getWidth() + padding.right);
        setHeight(padding.top + content.getHeight() + padding.bottom);
    }

    public Insets getPadding() {
        return padding;
    }

    @Override
    public Gui setContent(Gui content) {
        removeAllGuis();
        addGui(content);
        return this;
    }

    @Override
    public Gui getContent() {
        return getGui(0);
    }
}
