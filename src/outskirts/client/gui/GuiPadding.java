package outskirts.client.gui;

import outskirts.util.Validate;

/**
 * GuiPadding always should had only one child.
 * GuiPadding moves the child position, and self size(width/height) to fit the padding.
 * if needs multi children, or child needs align parent, should have a Gui to wrap them.
 */
public class GuiPadding extends Gui {

    private Insets padding = new Insets(); // LTRB

    public GuiPadding() {
        this(Insets.ZERO);
    }

    public GuiPadding(Insets paddingIn) {
        getPadding().set(paddingIn);

        addOnLayoutListener(e -> {
            Validate.isTrue(getChildCount() == 1, "Require one child Gui");
            Validate.isTrue(!isWrapChildren(), "WrapChildren is not allowed on GuiPadding. its leads padding size wrong.");

            Gui child = getGui(0);
            child.setRelativeXY(padding.left, padding.top);
            setWidth(padding.left + child.getWidth() + padding.right);
            setHeight(padding.top + child.getHeight() + padding.bottom);
        });
    }

    public Insets getPadding() {
        return padding;
    }
}
