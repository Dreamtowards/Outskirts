package outskirts.client.gui.screen.hawks;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.util.Colors;

public final class GuiHawksWindow extends Gui {

    private String title = "Title";

    private Gui positionDrag = addGui(new Gui() {
        @Override public float getX() { return GuiHawksWindow.this.getX(); }
        @Override public float getY() { return GuiHawksWindow.this.getY(); }
        @Override public float getWidth() { return GuiHawksWindow.this.getWidth(); }
    }).setHeight(32).addOnDraggingListener((dx, dy) -> {
        if (Gui.isMouseOver(0, 0, Outskirts.getWidth(), Outskirts.getHeight())) {
            GuiHawksWindow.this.setRelativeX(GuiHawksWindow.this.getRelativeX() + dx);
            GuiHawksWindow.this.setRelativeY(GuiHawksWindow.this.getRelativeY() + dy);
        }
    });
    private Gui sizeDrag = addGui(new Gui() {
        @Override public float getX() { return GuiHawksWindow.this.getX() + GuiHawksWindow.this.getWidth() - getWidth()/2; }
        @Override public float getY() { return GuiHawksWindow.this.getY() + GuiHawksWindow.this.getHeight() - getHeight()/2; }
        @Override public float getWidth() { return 8; }
        @Override public float getHeight() { return 8; }
    }).addOnDrawListener(e -> {
        if (isMouseOver()) {
            drawRectBorder(Colors.BLACK, getX(), getY(), getWidth(), getHeight(), 2);
        }
    }).addOnDraggingListener((dx, dy) -> {
        GuiHawksWindow.this.setWidth(GuiHawksWindow.this.getWidth() + dx);
        GuiHawksWindow.this.setHeight(GuiHawksWindow.this.getHeight() + dy);
    });

    private Gui guiMain = addGui(new Gui() {
        @Override public float getX() { return GuiHawksWindow.this.getX(); }
        @Override public float getY() { return GuiHawksWindow.this.getY() + positionDrag.getHeight(); }
        @Override public float getWidth() { return GuiHawksWindow.this.getWidth(); }
        @Override public float getHeight() { return GuiHawksWindow.this.getHeight() - positionDrag.getHeight(); }
    });

    public GuiHawksWindow() {

        addOnDrawListener(e -> {
            drawRect(Colors.WHITE, getX(), getY(), getWidth(), getHeight());
            drawRectBorder(Colors.BLACK, getX(), getY(), getWidth(), getHeight(), 1);

            drawString(title, getX()+getWidth()/2, getY()+8, Colors.BLACK, 16, true, false);

            drawRect(Colors.BLACK40, guiMain.getX(), guiMain.getY(), guiMain.getWidth(), guiMain.getHeight());
        });
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Gui getGuiMain() {
        return guiMain;
    }
}
