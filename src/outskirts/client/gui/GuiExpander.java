package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.util.Colors;
import outskirts.util.Identifier;

public class GuiExpander extends Gui implements Gui.Contentable {

    private static final Texture TEX_ARROW_UNEXPANDED = Loader.loadTexture(new Identifier("textures/gui/expander/arrow_unexpanded.png").getInputStream());
    private static final Texture TEX_ARROW_EXPANDED = Loader.loadTexture(new Identifier("textures/gui/expander/arrow_expanded.png").getInputStream());

    private boolean expanded;

    private Gui contentw = addGui(new Gui()); { // contentWrapper
        contentw.setRelativeXY(0, 20);
        setContent(new Gui());
    }

    private Gui header = addGui(new Gui()); {
        header.addOnLayoutListener(e -> {
            header.setWidth(Math.max(180, contentw.getWidth()));
        });
        header.setHeight(20);
        header.addOnClickListener(e -> {
            setExpanded(!isExpanded());
        });
    }

    public GuiExpander(String title) {
        setExpanded(false);

        header.addChildren(
          new Gui(20, 2, 0, 0).addChildren(
            new GuiText(title)
          )
        );

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK10, this);
            drawRect(Colors.BLACK40, header);
            if (header.isHover())
                drawRect(Colors.WHITE10, header);

            drawTexture(isExpanded() ? TEX_ARROW_EXPANDED : TEX_ARROW_UNEXPANDED, getX()+5, getY()+5, 10, 10);
        });

    }

    @Override
    public Gui setContent(Gui g) {
        if (contentw.size() > 0) {
            contentw.removeGui(0);
            assert contentw.size()==0;
        }
        contentw.addGui(g);
        return this;
    }

    @Override
    public Gui getContent() {
        return contentw.getGui(0);
    }

    public boolean isExpanded() {
        return expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        contentw.setVisible(isExpanded());
    }
}
