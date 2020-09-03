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

public class GuiExpander extends Gui implements Gui.Contentable {

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

    private Gui content = addGui(new Gui()); { // contentWrapper
        content.setRelativeXY(0, 20);
        content.setWrapChildren(true);
        setContent(new Gui());
    }

    public GuiExpander(String title) {
        setWrapChildren(true);
        setExpanded(false);

        header.addChildren(
          new Gui(20, 2, 0, 0).addChildren(
            new GuiText(title)
          )
        );

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK10, this);
            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), header.getHeight());

            drawTexture(isExpanded() ? TEX_ARROW_EXPANDED : TEX_ARROW_UNEXPANDED, getX()+5, getY()+5, 10, 10);
        });

    }

    @Override
    public Gui setContent(Gui g) {
        if (content.size() > 0) {
            content.removeGui(0);
            assert content.size()==0;
        }
        content.addGui(g);
        return this;
    }

    @Override
    public Gui getContent() {
        return content.getGui(0);
    }

    public boolean isExpanded() {
        return expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        content.setVisible(isExpanded());
    }
}
