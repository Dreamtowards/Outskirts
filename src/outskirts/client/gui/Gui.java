package outskirts.client.gui;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.ex.GuiRoot;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.event.*;
import outskirts.event.client.input.*;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.CopyOnIterateArrayList;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.Collections;
import java.util.List;
import java.util.function.*;

import static java.lang.Float.isNaN;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec4;

/**
 * reduce 'builder' style method, its likes convinent, but makes not clean. tends unmaintainable.
 * we reduce "return (T)this".
 */

public class Gui {

    private float x; // actually is relative-x   should use vector.?
    private float y;
    private float width = NaN;  // should the NaN been Manuel-Set.?
    private float height = NaN;

    protected static final float NaN = Float.NaN;
    protected static final float INF = Float.POSITIVE_INFINITY;

    protected static final String EVTAG_DEFDECO = "DEF_DECO"; //

    // available only when width/height is set to WrapContent (NaN).
    private Vector2f childrenBound = new Vector2f();

    private boolean focused = false; // focusable?
    private boolean hovered = false;
    private boolean pressed = false;

    /** when not VISIBLE, onDraw() will be not exec., and size been zero. */
    private boolean visible = true;

    private boolean clipping = false;

    /** efforts to onClickEvent... */
    private boolean enable = true;

    /** just an attachment */
    private Object tag;

    // they are not tint.(colorMultiply, opacity) cause other renderer would't supports, its high-level stuff

    private Gui parent;
    private List<Gui> children = new CopyOnIterateArrayList<>(); // allow to modify child while iteration.

    private EventBus eventBus = new EventBus(CopyOnIterateArrayList::new);

    {
        // checkTrigger_Focus()
        addMouseButtonListener(e -> {
            if (e.getMouseButton() == 0 && e.getButtonState()) {
                setFocused(isHover());
            }
        });
//        // todo: OnClick should dispatch from outside, go trough to parents.
//        addOnReleasedListener(e -> {
//            if (isEnable() && isHover()) {
//                performEvent(new OnClickEvent());
//            }
//        });
        addMouseButtonListener(e -> {
            if (e.getMouseButton() == 0) {
                if (e.getButtonState() && isHover()) {
                    setPressed(true);
                } else if (isPressed() && !e.getButtonState()) {
                    setPressed(false);
                }
            }
        });
        addOnDetachListener(e -> {  // when removed from parent, clear children hover,press. recursive
            setHover(false);
            setPressed(false);
        });
    }

    public Gui() { }

    public Gui(float rx, float ry) {
        setRelativeXY(rx, ry);
    }
    public Gui(float rx, float ry, float width, float height) {
        setRelativeXY(rx, ry);
        setWidth(width);
        setHeight(height);
    }

    /**
     * @return the Gui which added //todo: should make Create and Add in one Line .?
     */
    public <T extends Gui> T addGui(T gui, int index) {
        assert gui.getParent() == Gui.EMPTY : "Failed to add gui, not detach yet. gui: "+gui+", parent: "+gui.getParent();
        ((Gui)gui).setParent(this);
        gui.broadcaseEvent(new AttachEvent());
        children.add(index, gui);

        requestLayout();
        return gui;
    }
    public final <T extends Gui> T addGui(T gui) {
        return addGui(gui, count());
    }

    public final Gui addChildren(Gui... guis) {
        for (Gui g : guis) {
            addGui(g);
        }
        return this;
    }

    public <T extends Gui> T getGui(int index) {
        return (T)children.get(index);
    }

    public final void setGui(int index, Gui gui) {
        if (index < count()) {
            removeGui(index);
        }
        addGui(gui, index);
    }

    public int count() {
        return children.size();
    }

    /**
     * @return the Gui which been removed
     */
    public Gui removeGui(int index) {
        getGui(index).setParent(null);
        Gui removed = children.remove(index);
        removed.broadcaseEvent(new DetachEvent());
        requestLayout();
        return removed;
    }
    public final boolean removeGui(Gui g) {
        int i = children.indexOf(g);
        if (i == -1) return false;
        removeGui(i);
        return true;
    }
    public final void removeAllGuis() {
        for (int i = count()-1; i >= 0; i--) {
            removeGui(i);
        }
    }

    public final <T extends Gui> T getParent() {
        if (parent == null)
            return (T)Gui.EMPTY;
        return (T)parent;
    }
    private void setParent(Gui parent) {
        this.parent = parent;
    }

    public final List<Gui> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public final <T extends Gui> T exec(Consumer<T> exec) {
        exec.accept((T)this);
        return (T)this;
    }


//    public boolean isWrapChildren() {
//        return isNaN(width) || isNaN(height);
//    }




    public float getRelativeX() {
        return this.x;
    }
    public void setRelativeX(float x) {
        if (valdif(this.x, x)) {
//            Log.LOGGER.info("X Changed: {}, {}\n{}", this.x, x, IOUtils.toString(new Throwable()));
            requestLayout();
        }
        this.x = x;
    }

    public float getRelativeY() {
        return this.y;
    }
    public void setRelativeY(float y) {
        if (valdif(this.y, y)) {
//            Log.LOGGER.info("Y Changed: {}, {}\n{}", this.y, y, IOUtils.toString(new Throwable()));
            requestLayout();
        }
        this.y = y;
    }

    public final void setRelativeXY(float x, float y) {  // TOOL METHOD
        setRelativeX(x);
        setRelativeY(y);
    }

    /**
     * Get absolute x position.
     */// todo: every time get coordinate needs a series recursolve call.? may should had a local cache
    public float getX() {
        return getParent().getX() + x;
    }
    public void setX(float x) {
        setRelativeX(x - getParent().getX());
    }

    public float getY() {
        return getParent().getY() + y;
    }
    public void setY(float y) {
        setRelativeY(y - getParent().getY());
    }

//    public final void setXY(float x, float y) {  // TOOL METHOD
//        setX(x);
//        setY(y);
//    }

    public float getWidth() {
        if (!isVisible()) return 0;
        if (isNaN(width)) return childrenBound.x;
        if (width == INF) return getParent().getWidth()-getRelativeX();
        return width;
    }
    public void setWidth(float width) {
        if (valdif(this.width, width)) requestLayout();
        this.width = width;
    }
    private static boolean valdif(float a, float b) {  // value changed
        if (isNaN(a) || isNaN(b))
            return isNaN(a) != isNaN(b);
        return a != b;
    }

    public float getHeight() {
        if (!isVisible()) return 0;
        if (isNaN(height)) return childrenBound.y;
        if (height == INF) return getParent().getHeight()-getRelativeY();
        return height;
    }
    public void setHeight(float height) {
        if (valdif(this.height, height)) requestLayout();
        this.height = height;
    }





    public boolean isFocused() {
        return focused;
    }
    public void setFocused(boolean focused) {
        boolean oldFocused = this.focused;
        this.focused = focused;
        if (oldFocused != focused) {
            performEvent(new OnFocusChangedEvent());
        }
        if (oldFocused != focused) requestDraw();
    }

    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        if (this.enable != enable) requestDraw();
        this.enable = enable;
    }

    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        boolean oldVisible = this.visible;
        this.visible = visible;
        if (oldVisible != visible) {
            performEvent(new OnVisibleChangedEvent());
        }
        if (oldVisible != visible) {
            requestLayout();
        }
    }

    public boolean isClipping() {
        return clipping;
    }
    public void setClipping(boolean clipping) {
        if (this.clipping != clipping) requestDraw();
        this.clipping = clipping;
    }

    public Object getTag() {
        return tag;
    }
    public void setTag(Object tag) {
        this.tag = tag;
    }

    public boolean isHover() { // isHovering
        return hovered;
    }
    public void setHover(boolean hovered) {
        boolean oldHovered = this.hovered;
        this.hovered = hovered;
        if (!oldHovered && hovered) {
            performEvent(new OnMouseInEvent());
        } else if (oldHovered && !hovered) {
            performEvent(new OnMouseOutEvent());
        }
        if (oldHovered != hovered) requestDraw();
    }

    public boolean isPressed() {
        return pressed;
    }
    public void setPressed(boolean pressed) {
        boolean oldPressed = this.pressed;
        this.pressed = pressed;
        if (!oldPressed && pressed) {
            performEvent(new OnPressedEvent());
        } else if (oldPressed && !pressed) {
            performEvent(new OnReleasedEvent());
        }
        if (oldPressed != pressed) requestDraw();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight() + "}";
    }


    public static void initEscClose(Gui g) {
        g.addKeyboardListener(e -> {
            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_ESCAPE && getRootGUI().getLastGui() == g) {
                g.getParent().removeGui(g);
            }
        });
    }



//    public final boolean isMouseOver() {
//        throw new UnsupportedOperationException();
////        return Gui.isMouseOver(getX(), getY(), getWidth(), getHeight());
//    }
    public static boolean isMouseOver(Gui g) {
        return Gui.isMouseOver(g.getX(), g.getY(), g.getWidth(), g.getHeight());
    }
    public static boolean isMouseOver(float x, float y, float width, float height) {
        return Outskirts.getMouseX() >= x && Outskirts.getMouseX() < x + width && Outskirts.getMouseY() >= y && Outskirts.getMouseY() < y + height;
    }
    public static boolean isPointOver(Vector2f p, Gui gui) {
        return Gui.isPointOver(p.x, p.y, gui);
    }
    public static boolean isPointOver(float x, float y, Gui gui) {
        return x >= gui.getX() && x <= gui.getX() + gui.getWidth() && y >= gui.getY() && y <= gui.getY() + gui.getHeight();
    }
    public static boolean isIntersects(Gui g1, Gui g2) {
        float amnX=g1.getX(), amnY=g1.getY(), amxX=amnX+g1.getWidth(), amxY=amnY+g1.getHeight();
        float bmnX=g2.getX(), bmnY=g2.getY(), bmxX=bmnX+g2.getWidth(), bmxY=bmnY+g2.getHeight();
        return bmxX >= amnX && bmnX < amxX &&
               bmxY >= amnY && bmnY < amxY;
    }

    /**
     * Tool method for Gui.
     * not unmodifiableList getChildren() cause sometimes Gui's children'll be dynamic remove ( but now when removed, one gui's invoke'll be jump over
     * and {for size: get} is original method.
     * cause always use and have multi params, so not use static.
     */
    public static void forChildren(Gui gfrom, Consumer<Gui> visitor) {
        visitor.accept(gfrom);

        // if iterating children use index, probably skip/repeat iteration-item when the list been edit(add/remove)
        for (Gui child : gfrom.getChildren()) {
            forChildren(child, visitor);
        }
    }

    public static void forParents(Gui gfrom, Consumer<Gui> visitor, boolean includeSelf) {
        if (includeSelf)
            visitor.accept(gfrom);

        Gui g = gfrom.getParent();
        if (g != Gui.EMPTY) {
            forParents(g, visitor, true);
        }
    }
    public static void forParents(Gui gfrom, Consumer<Gui> visitor) {
        forParents(gfrom, visitor, true);
    }

    private static int calcDepth(Gui g) {
        int i = 0;
        while ((g=g.getParent()) != Gui.EMPTY) i++;
        return i;
    }

    //should this..?
    public static void toggleVisible(Gui gui) {
        gui.setVisible(!gui.isVisible());
    }

    /**
     * for "Tooltip" Gui popup. actually dosen't think unlaw-putting is good. the Tooltip should tends just stay in themself place.
     */
    public static GuiRoot getRootGUI() {
        return Outskirts.getRootGUI();
    }


    public final void onDraw() {
        if (!isVisible()) return;
//        _checks_MouseInOut();

        boolean isClip = isClipping(); // avoid field dynamic changed
        if (isClip)
            GuiRenderer.pushScissor(getX(), getY(), getWidth(), getHeight());

        performEvent(new OnDrawEvent()); // OnDraw

        for (Gui child : getChildren()) {
            if (isClip && !Gui.isIntersects(this, child)) continue;

            child.onDraw();
        }

        if (isClip)
            GuiRenderer.popScissor();

        performEvent(new OnPostDrawEvent());
    }

    public static boolean layoutRequested = true;  // should reflow/repaint. markLayout()
    public static void requestLayout() {
        layoutRequested = true;
    }

    public static boolean drawRequested = true;
    public static void requestDraw() {
        drawRequested = true;
    }


    //    private int cachedvolumehash;
//    private boolean isVolumeChanged() {  // hasAppearanceChange()
//        int h = vec4(getX(),getY(),getWidth(),getHeight()).hashCode() + (isVisible() ? 1 : 0); // child count.?
//        if (cachedvolumehash != h) {
//            cachedvolumehash = h;
//            return true;
//        }
//        return false;
//    }
//    public static boolean hasVolumeChanged() {  // markDirty() from subs. isDirty()
//        Val v = Val.zero();
//        forChildren(getRootGUI(), g -> {
//            // Needs Full-through volume-cache-update. dont early exit. otherwise it will cause a lot of onLayout calls.
//            if (g.isVolumeChanged())
//                v.val = 1;
//        });
//        return v.val > 0;
//    }

    public final void onLayout() {
        if (!isVisible()) return;

        // layout children first or layout 'this' first.?
        // cuz sometimes this size dependents children, but sometimes this size dependents parent.
        performEvent(new OnLayoutEvent());

        for (Gui child : children)
            child.onLayout();

        _updateChildrenBound();
    }

    private void _updateChildrenBound() {
        if (!isNaN(width) && !isNaN(height))
            return;
        float mxxs=0, mxys=0;
        for (Gui g : children) {
            mxxs = Math.max(mxxs, g.getRelativeX()+g.getWidth());
            mxys = Math.max(mxys, g.getRelativeY()+g.getHeight());
        }
        if (childrenBound.x != mxxs || childrenBound.y != mxys) {
            childrenBound.set(mxxs, mxys);
//            requestLayout();  // this request is necessary, because even tho the changers have called the Request,
        }
    }




    // for getParent()(EMPTY_PARENT), draw nothing...
    public static final Gui EMPTY = new Gui() {
        @Override public float getX() { return 0; }
        @Override public float getY() { return 0; }
        @Override public float getWidth() { return 0; }
        @Override public float getHeight() { return 0; }
    };


    public final boolean performEvent(Event event) {
        if (event instanceof GuiEvent)
            ((GuiEvent)event)._gui=this;
        return eventBus.post(event);
    }
    public final boolean broadcaseEvent(Event event) { // post to all children gui{
        Gui.forChildren(this, g -> {
            // whether isVisible(), just dispatch.
            // something dependents on event, don't missed.
            // e.g. global event dependent on OnAttach/OnDetach, if Attached as Invisiable, then to Visible, it just missed the event.

            g.performEvent(event);
        });
        return Cancellable.isCancelled(event);
    }

    protected final <E extends Event> EventBus.Handler attachListener(Class<E> eventClass, Consumer<E> eventListener) {
        return eventBus.register(eventClass, eventListener);
    }

    public boolean removeListeners(Object funcOrTag) {
        return eventBus.unregister(funcOrTag);
    }

    // may split out ..? for sth needs interpolation like FOV transformation
    // APPLY / TRANS
    protected static final BiConsumer<Gui, Float> TRANS_X = (gui, value) -> gui.setX(value.intValue());
    protected static final BiConsumer<Gui, Float> TRANS_Y = (gui, value) -> gui.setY(value.intValue());

    //IG means InterpolationGenerator ha
    protected static final Function<Float, Float> IG_BACKEASE = t -> Maths.backease(t, 1);
    protected static final Function<Float, Float> IG_POWER3 = t -> Maths.powerease(t, 3);
    protected static final Function<Float, Float> IG_CIRCLEEASE = Maths::circleease;
    protected static final Function<Float, Float> IG_LINEAR = t -> t;

    protected static final int EASE_IN = 0;
    protected static final int EASE_OUT = 1;
    protected static final int EASE_IN_OUT = 2;

    private static float _APPLY_EASE(int easeMode, Function<Float, Float> ig, float t) {
        switch (easeMode) {
            case EASE_IN:
                return ig.apply(t); // default IG is EASE_IN
            case EASE_OUT:
                return 1F-ig.apply(1F-t); // flip X, Y
            case EASE_IN_OUT:
                if (t < 0.5f) { // IN
                    return ig.apply(t*2f)/2f; // X:[0.0-0.5], Y:[0.0-0.5]
                } else { // OUT
                    return (1F-ig.apply((1F-t)*2f))/2f+0.5f;
                }
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * @param from,to using float num cause sometimes not only considers pixels, and also having some like transparency value, vec4 color transform..
     * @param pass already passed transform time, less than 0 makes delay transform, bigger than 0 makes already transform
     * @param duration transform duration time, seconds
     * @param applicator the transformation interpolation value applicator
     * @param interpolator the interpolation value generator
     */
    public final void attachTransform(float from, float to, float duration, BiConsumer<Gui, Float> applicator, Function<Float, Float> interpolator, int easeMode, float pass) {
        addOnDrawListener(new Consumer<OnDrawEvent>() {
            private float passed = pass;
            @Override
            public void accept(OnDrawEvent e) {
                passed += Outskirts.getDelta();
                if (passed < 0f)
                    return;

                float t = passed / duration; // ratio of Transformation

                float v = _APPLY_EASE(easeMode, interpolator, t); // [0.0-1.0] value been interpolated AND applied-easeMode
                v = Maths.lerp(v, from, to); // [from-to] lerp-ed value

                applicator.accept(Gui.this, v); // applies to GUI

                if (t > 1f) {
                    eventBus.unregister(this);
                }
            }
        });
    }


    public final EventBus.Handler addOnClickListener(Consumer<OnClickEvent> listener) {
        return attachListener(OnClickEvent.class, listener);
    }

    // Global/ Events   //todo: is these really needs.? the global events, not GuiEvent s.
    public final void addMouseButtonListener(Consumer<MouseButtonEvent> lsr) {
        addGlobalEventListener(MouseButtonEvent.class, lsr);
    }
    public final void addMouseMoveListener(Consumer<MouseMoveEvent> lsr) {
        addGlobalEventListener(MouseMoveEvent.class, lsr);
    }
    public final void addKeyboardListener(Consumer<KeyboardEvent> lsr) {
        addGlobalEventListener(KeyboardEvent.class, lsr);
    }
    public final void addMouseWheelListener(Consumer<InputScrollEvent> lsr) {
        addGlobalEventListener(InputScrollEvent.class, lsr);
    }


    public final EventBus.Handler addOnDrawListener(Consumer<OnDrawEvent> listener) {
        return attachListener(OnDrawEvent.class, listener);
    }
    public final EventBus.Handler addOnPostDrawListener(Consumer<OnPostDrawEvent> listener) {
        return attachListener(OnPostDrawEvent.class, listener);
    }
    public final EventBus.Handler addOnLayoutListener(Consumer<OnLayoutEvent> listener) {
        return attachListener(OnLayoutEvent.class, listener);
    }
    public final EventBus.Handler addOnMouseInListener(Consumer<OnMouseInEvent> listener) {
        return attachListener(OnMouseInEvent.class, listener);
    }
    public final EventBus.Handler addOnMouseOutListener(Consumer<OnMouseOutEvent> listener) {
        return attachListener(OnMouseOutEvent.class, listener);
    }
    public final EventBus.Handler addOnPressedListener(Consumer<OnPressedEvent> lsr) {
        return attachListener(OnPressedEvent.class, lsr);
    }
    public final EventBus.Handler addOnReleasedListener(Consumer<OnReleasedEvent> lsr) {
        return attachListener(OnReleasedEvent.class, lsr);
    }
    public final EventBus.Handler addOnFocusChangedListener(Consumer<OnFocusChangedEvent> lsr) {
        return attachListener(OnFocusChangedEvent.class, lsr);
    }
    public final EventBus.Handler addOnVisibleChangedListener(Consumer<OnVisibleChangedEvent> lsr) {
        return attachListener(OnVisibleChangedEvent.class, lsr);
    }

    public final EventBus.Handler addOnAttachListener(Consumer<AttachEvent> lsr) {
        return attachListener(AttachEvent.class, lsr);
    }
    public final EventBus.Handler addOnDetachListener(Consumer<DetachEvent> lsr) {
        return attachListener(DetachEvent.class, lsr);
    }

    private boolean isAttached() {  // naming mounted.?
        return getParent() != EMPTY || this instanceof GuiRoot;
    }

    public final <E extends Event> void addGlobalEventListener(Class<E> eventclass, Consumer<E> lsr) {
        if (isAttached())  // the gui has already attached. now register immidiately.
            Events.EVENT_BUS.register(eventclass, lsr);
        addOnAttachListener(e -> {
            Events.EVENT_BUS.unregister(lsr);
            Events.EVENT_BUS.register(eventclass, lsr);
        });
        addOnDetachListener(e -> Events.EVENT_BUS.unregister(lsr));
    }


    // AlignParentLTRB
    public final void addLayoutorAlignParentLTRB(float left, float top, float right, float bottom) { // in "pixels". param-b can be NaN. i.e. not to set.
        addOnLayoutListener(e -> {
            if (!isNaN(left)) setRelativeX(left);
            if (!isNaN(top)) setRelativeY(top);

            if (!isNaN(right)) {
                if (!isNaN(left)) setWidth(getParent().getWidth() - (right+left));
                else setRelativeX(getParent().getWidth() - (right+getWidth()));
            }
            if (!isNaN(bottom)) {
                if (!isNaN(top)) setHeight(getParent().getHeight() - (top+bottom));
                else setRelativeY(getParent().getHeight() - (bottom+getHeight()));
            }
        });
    }
    public final void addLayoutorAlignParentRR(float rx, float ry, float rwidth, float rheight) { // RestRatio. 0:left, 0.5f:mid, 1:right
        addOnLayoutListener(e -> {
            if (!isNaN(rx))
                setRelativeX((getParent().getWidth()-getWidth())*rx);
            if (!isNaN(ry))
                setRelativeY((getParent().getHeight()-getHeight())*ry);
            if (!isNaN(rwidth))
                setWidth(getParent().getWidth() * rwidth);
            if (!isNaN(rheight))
                setHeight(getParent().getHeight() * rheight);
        });
    }
    public final void addLayoutorAlignParentRR(float rx, float ry) {
        addLayoutorAlignParentRR(rx, ry, NaN, NaN);
    }

    public static class OnDrawEvent extends GuiEvent { }

    public static class OnPostDrawEvent extends GuiEvent { }

    public static class OnLayoutEvent extends GuiEvent { }

    public static class OnClickEvent extends GuiEvent { }

    protected static class OnMouseInEvent extends GuiEvent { } // setHover() false -> true.

    protected static class OnMouseOutEvent extends GuiEvent { }  // setHover() true -> false.


    public static class OnPressedEvent extends GuiEvent { }  // setPressed() false -> true.

    public static class OnReleasedEvent extends GuiEvent { } /// setPressed() true -> false.

    public static class OnFocusChangedEvent extends GuiEvent { }

    public static class OnVisibleChangedEvent extends GuiEvent { }

    // the Attach might be calls multi times, with out once Detach.
    // because current the Attach event not required needs attach to the RootGUI.
    public static class AttachEvent extends GuiEvent { }

    public static class DetachEvent extends GuiEvent { }












    //////////////////////////// START GLOBAL DRAW ////////////////////////////

//    public static Vector2i drawString(String texts, int x, int y, Vector4f color, int height, boolean centerHorizontal) {
//        if (centerHorizontal) {
//            int textWidth = Outskirts.renderEngine.getFontRenderer().stringWidth(texts, height);
//            x = x - (textWidth / 2);
//        }
//        return Outskirts.renderEngine.getFontRenderer().drawString(texts, x, y, height, color, true);
//    }

    public static void drawString(String text, float x, float y, Vector4f color, float textHeight, float horizontalAlign, boolean drawShadow) {
        if (horizontalAlign != 0) { // shoulddo tex_x = t * (max_width - tex_width)
            float textWidth = Outskirts.renderEngine.getFontRenderer().calculateBound(text, textHeight).x;
            x -= textWidth*horizontalAlign;
        }
        Outskirts.renderEngine.getFontRenderer().renderString(text, x, y, textHeight, color, drawShadow);
    }
    public static void drawString(String text, float x, float y, Vector4f color, float height, float horizontalAlign) {
        drawString(text, x, y, color, height, horizontalAlign, true);
    }
    public static void drawString(String text, float x, float y, Vector4f color, float height) {
        drawString(text, x, y, color, height, 0);
    }
    public static void drawString(String text, float x, float y, Vector4f color) {
        drawString(text, x, y, color, GuiText.DEFAULT_TEXT_HEIGHT);
    }

    public static void drawRect(Vector4f color, float x, float y, float width, float height) {
        GuiRenderer.OP_colormul.set(color);
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, Texture.UNIT, x, y, width, height);
    }
    public static void drawRect(Vector4f color, Gui g) {
        drawRect(color, g.getX(), g.getY(), g.getWidth(), g.getHeight());
    }

    public static void drawTexture(Texture texture, float x, float y, float width, float height) {
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, texture, x, y, width, height);
    }
    public static void drawTexture(Texture texture, float x, float y, float width, float height, float texOffsetX, float texOffsetY, float texScaleX, float texScaleY) {
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, texture, x, y, width, height, texOffsetX, texOffsetY, texScaleX, texScaleY);
    }
    public static void drawTexture(Texture texture, Gui g) {
        drawTexture(texture, g.getX(), g.getY(), g.getWidth(), g.getHeight());
    }

    public static void drawWorldpoint(Vector3f worldposition, BiConsumer<Float, Float> lsr) {
        Vector4f v = Maths.calculateDisplayPosition(worldposition, Outskirts.renderEngine.getProjectionMatrix(), Outskirts.renderEngine.getViewMatrix(), null);
        if (v.z > 0) {
            lsr.accept(v.x*Outskirts.getWidth(), v.y*Outskirts.getHeight());
        }
    }

    /**
     * thickness > 0, inner. < 0, outer.
     */
    public static void drawRectBorder(Vector4f color, float x, float y, float width, float height, float thickness) {

        drawRect(color, x, y, width, thickness); //Top
        drawRect(color, x, y + height-thickness, width, thickness); //Bottom

        drawRect(color, x, y + thickness, thickness, height-thickness-thickness); //Left
        drawRect(color, x + width - thickness, y+thickness, thickness, height-thickness-thickness); //Right
    }
    public static void drawRectBorder(Vector4f color, Gui g, float thickness) {
        drawRectBorder(color, g.getX(), g.getY(), g.getWidth(), g.getHeight(), thickness);
    }

    /**
     * @param thickness corner size.
     */
    public static void drawCornerStretchTexture(Texture texture, float x, float y, float width, float height, float thickness) {

        Gui.drawTexture(texture, x, y, thickness, thickness, 0, 0, 0.5f, 0.5f); // Left-Top
        Gui.drawTexture(texture, x+width-thickness, y, thickness, thickness, 0.5f, 0, 0.5f, 0.5f); // Right-Top
        Gui.drawTexture(texture, x, y+height-thickness, thickness, thickness, 0, 0.5f, 0.5f, 0.5f); // Left-Bottom
        Gui.drawTexture(texture, x+width-thickness, y+height-thickness, thickness, thickness, 0.5f, 0.5f, 0.5f, 0.5f); // Left-Bottom

        Gui.drawTexture(texture, x, y+thickness, thickness, height-thickness-thickness, 0, 0.5f, 0.5f, 0f); // Left
        Gui.drawTexture(texture, x+thickness, y, width-thickness-thickness, thickness, 0.5f, 0, 0, 0.5f); // Top
        Gui.drawTexture(texture, x+width-thickness, y+thickness, thickness, height-thickness-thickness, 0.5f, 0.5f, 0.5f, 0); // Right
        Gui.drawTexture(texture, x+thickness, y+height-thickness, width-thickness-thickness, thickness, 0.5f, 0.5f, 0, 0.5f); // Bottom

        Gui.drawTexture(texture, x+thickness, y+thickness, width-thickness-thickness, height-thickness-thickness, 0.5f, 0.5f, 0, 0); // Center
    }
    public static void drawCornerStretchTexture(Texture texture, Gui g, float thickness) {
        drawCornerStretchTexture(texture, g.getX(), g.getY(), g.getWidth(), g.getHeight(), thickness);
    }



    //////////////////////////// END GLOBAL DRAW ////////////////////////////



    public interface Checkable {

        boolean isChecked();

        void setChecked(boolean checked);



        default void initCheckedSync(Supplier<Boolean> get, Consumer<Boolean> set) {
            addOnCheckedListener(e -> {
                boolean thisb = isChecked();
                if (get.get() != thisb) {
                    set.accept(thisb);
                }
            });
            ((Gui)this).addOnDrawListener(e -> {
                boolean b = get.get();
                if (b != isChecked()) {
                    setChecked(b);
                }
            }).priority(EventPriority.HIGH);
        }

        default EventBus.Handler addOnCheckedListener(Consumer<OnCheckedEvent> lsr) {
            return ((Gui)this).attachListener(OnCheckedEvent.class, lsr);
        }
        /** spec: Only perform on checked-changed. */
        class OnCheckedEvent extends GuiEvent { }
    }

    public interface Contentable {

        /**
         * @return "this."
         */
        Gui setContent(Gui g);

        Gui getContent();

    }



    public static final class Insets {

        public static final Insets ZERO = Insets.fromLTRB(0, 0, 0, 0);

        public float left;
        public float top;
        public float right;
        public float bottom;

        public Insets() {}

        public Insets set(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            return this;
        }

        public Insets set(Insets src) {
            return set(src.left, src.top, src.right, src.bottom);
        }

        public static Insets fromLTRB(float l, float t, float r, float b) {
            return new Insets().set(l, t, r, b);
        }
    }

}
