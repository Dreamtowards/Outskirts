package outskirts.client.gui.stat;

import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;
import outskirts.util.vector.Vector2f;

public class GuiWrap extends Gui {

    private Vector2f alignment = new Vector2f(0, 0);
    private Vector2f mainDirection = new Vector2f(Vector2f.UNIT_X);
    private Vector2f wrapDirection = new Vector2f(0, 1);


    public GuiWrap() {

        addOnLayoutListener(this::onLayout0);
    }

    // todo: requires optim for more generic uses
    @EventHandler
    private void onLayout0(OnLayoutEvent event) {

        Gui fsGui = size() == 0 ? Gui.EMPTY : getGui(0);
        Vector2f curr = new Vector2f(
                (getWidth()-fsGui.getWidth())  * alignment.x,
                (getHeight()-fsGui.getHeight()) * alignment.y);
        Vector2f lineStart = new Vector2f(curr);

        for (int i = 0;i < size();i++) {
            Gui g = getGui(i);
            g.setRelativeXY(curr.x, curr.y);

            curr.x += mainDirection.x * g.getWidth();
            curr.y += mainDirection.y * g.getHeight();

            // outbound. do wrap. once.
            Gui nxGui = i+1 == size() ? Gui.EMPTY : getGui(i+1);
            if (!Gui.isPointOver(
                    getX()+curr.x +mainDirection.x*nxGui.getWidth(),
                    getY()+curr.y +mainDirection.y*nxGui.getHeight(), this)) {
                curr.set(lineStart).add(g.getWidth()*wrapDirection.x, g.getHeight()*wrapDirection.y);
                lineStart.set(curr);
            }
        }
    }

    public Vector2f getAlignment() {
        return alignment;
    }
    public Vector2f getMainDirection() {
        return mainDirection;
    }
    public Vector2f getWrapDirection() {
        return wrapDirection;
    }
}
