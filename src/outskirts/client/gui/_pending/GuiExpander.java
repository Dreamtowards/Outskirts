package outskirts.client.gui._pending;

import outskirts.client.Loader;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiText;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix2f;

public class GuiExpander extends Gui {

    private static final Texture TEX_ARROW_UNEXPANDED = Loader.loadTexture(new Identifier("textures/gui/expander/arrow_unexpanded.png").getInputStream());
    private static final Texture TEX_ARROW_EXPANDED = Loader.loadTexture(new Identifier("textures/gui/expander/arrow_expanded.png").getInputStream());

    private boolean expanded;

    private Gui header = addGui(new Gui()); {
        header.setWidth(180);
        header.setHeight(20);
        header.addOnClickListener(e -> {
            setExpanded(!isExpanded());
        });
    }
    private GuiText title = header.addGui(new GuiText("default title.")); {
        title.setRelativeXY(20, 2);
    }

    private Gui content = addGui(new Gui()); {
        content.setRelativeXY(0, 20);
        content.setWrapChildren(true);
    }

    public GuiExpander() {
        setWrapChildren(true);
        setExpanded(false);

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK10, this);
            drawRect(Colors.BLACK40, header);

            drawTexture(isExpanded() ? TEX_ARROW_EXPANDED : TEX_ARROW_UNEXPANDED, getX()+5, getY()+5, 10, 10);
        });

    }

    public GuiText getTitle() {
        return title;
    }

    public Gui getContent() {
        return content;
    }

    public boolean isExpanded() {
        return expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        getContent().setVisible(isExpanded());
    }
}
