package outskirts.client.gui;

import outskirts.event.EventHandler;
import outskirts.util.Validate;

import static java.lang.Float.isNaN;

public class GuiAlign extends Gui implements Gui.Contentable {

    // Ratios.
    private float rx = NaN;
    private float ry = NaN;
    private float rwidth = NaN;
    private float rheight = NaN;

    // LTRB higher priority than RRs.
    private float left = NaN;
    private float top = NaN;
    private float right = NaN;
    private float bottom = NaN;

    public GuiAlign() {
        super(0, 0, INF, INF);
        addOnLayoutListener(this::onLayout0);
    }
    public GuiAlign(float _rx, float _ry) {
        this();
        this.rx = _rx;
        this.ry = _ry;
    }
    public GuiAlign(float _rx, float _ry, float rw, float rh) {
        this();
        this.rx = _rx;
        this.ry = _ry;
        this.rwidth = rw;
        this.rheight = rh;
    }

    public GuiAlign useLTRB(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        return this;
    }

    @EventHandler
    private void onLayout0(OnLayoutEvent e) {
        Validate.isTrue(count() == 1);
        Gui g = getContent();
        if (!isNaN(rx)) g.setRelativeX((getWidth() -g.getWidth()) *rx);
        if (!isNaN(ry)) g.setRelativeY((getHeight()-g.getHeight())*ry);
        if (!isNaN(rwidth))  g.setWidth(getWidth()   * rwidth);
        if (!isNaN(rheight)) g.setHeight(getHeight() * rheight);

        if (!isNaN(left)) g.setRelativeX(left);
        if (!isNaN(top))  g.setRelativeY(top);
        if (!isNaN(right)) {
            if (!isNaN(left) || !isNaN(rx)) g.setWidth(getWidth()-right-g.getRelativeX());
            else g.setRelativeX(getWidth()-right-g.getWidth());
        }
        if (!isNaN(bottom)) {
            if (!isNaN(top) || !isNaN(ry)) g.setHeight(getHeight()-bottom-g.getRelativeY());
            else g.setRelativeY(getHeight()-bottom-g.getHeight());
        }
    }

    @Override
    public Gui setContent(Gui g) {
        removeAllGuis();
        addGui(g);
        return this;
    }

    @Override
    public Gui getContent() {
        return getGui(0);
    }
}
