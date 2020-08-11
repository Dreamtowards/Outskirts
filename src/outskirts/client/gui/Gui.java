package outskirts.client.gui;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.event.Cancellable;
import outskirts.event.Event;
import outskirts.event.EventBus;
import outskirts.event.EventPriority;
import outskirts.event.client.input.*;
import outskirts.event.gui.GuiEvent;
import outskirts.util.*;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.*;
import java.util.function.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * reduce 'builder' style method, its likes convinent, but makes not clean. tends unmaintainable.
 * we reduce "return (T)this".
 */

public class Gui {

    private float x; //actually is relative-x   should use vector.?
    private float y;
    private float width;
    private float height;

    private boolean focused = false; // focusable

    private boolean hovered = false; // isHovered() setHovered()

    /** when not VISIBLE, onDraw() will be not exec., and size been zero. */
    private boolean visible = true;

    private boolean clipChildren = false;

    /** efforts to onClickEvent... */
    private boolean enable = true;

    /** just a attachment */
    private Object tag;

    /** size just fits can wrap all direct-children. */
    private boolean wrapChildren = false;

    //they are not tint.(colorMultiply, opacity) cause other renderer would't supports, its high-level stuff

    private Gui parent;
    private List<Gui> children = new ArrayList<>();

    private EventBus eventBus = new EventBus(CopyOnIterateArrayList::new);

    public Gui() {
        this(0, 0, 0, 0);
    }

    public Gui(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // checkTrigger_Focus()
        addMouseButtonListener(e -> {
            if (e.getMouseButton() == GLFW_MOUSE_BUTTON_LEFT && e.getButtonState()) {
                boolean f = isMouseOver();
                if (isFocused() != f) {
                    setFocused(f);
                }
            }
        });
        // OnClickEvent
        addMouseButtonListener(e -> {
            if (isVisible() && isEnable() && e.getButtonState() && e.getMouseButton() == GLFW_MOUSE_BUTTON_LEFT && Gui.isMouseOver(this)) {
                performEvent(new OnClickEvent());
            }
        });
    }

    /**
     * @return the Gui which added //todo: should make Create and Add in one Line .?
     */
    public <T extends Gui> T addGui(T gui, int index) {
        gui.setParent(this);
        children.add(index, gui);
        return gui;
    }
    public final <T extends Gui> T addGui(T gui) {
        return addGui(gui, getChildCount());
    }

    public <T extends Gui> T getGui(int index) {
        return (T)children.get(index);
    }

    public final void setGui(int index, Gui gui) {
        if (index < getChildCount()) {
            removeGui(index);
        }
        addGui(gui, index);
    }

    // size() .? childCount()
    public int getChildCount() {
        return children.size();
    }

    /**
     * @return the Gui which been removed
     */
    public <T extends Gui> T removeGui(int index) {
        getGui(index).setParent(null);
        return (T) children.remove(index);
    }
    public final void removeGui(Gui g) {
        removeGui(children.indexOf(g));
    }
    public final void removeAllGuis() {
        for (int i = getChildCount()-1;i >= 0;i--) {
            removeGui(i);
        }
    }

    public final <T extends Gui> T getParent() {
        if (parent == null)
            return (T)Gui.EMPTY;
        return (T)parent;
    }
    public void setParent(Gui parent) {
        this.parent = parent;
    }

    public final List<Gui> getChildren() {
        return Collections.unmodifiableList(children);
    }






    public float getRelativeX() {
        return this.x;
    }
    public void setRelativeX(float x) {
        this.x = x;
    }

    public float getRelativeY() {
        return this.y;
    }
    public void setRelativeY(float y) {
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

    public final void setXY(float x, float y) {  // TOOL METHOD
        setX(x);
        setY(y);
    }

    public float getWidth() {
        if (!isVisible()) return 0;
        return width;
    }
    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        if (!isVisible()) return 0;
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }





    public boolean isFocused() {
        return focused;
    }
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isClipChildren() {
        return clipChildren;
    }
    public void setClipChildren(boolean clipChildren) {
        this.clipChildren = clipChildren;
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
        this.hovered = hovered;
    }

    public boolean isWrapChildren() {
        return wrapChildren;
    }
    public void setWrapChildren(boolean wrapChildren) {
        this.wrapChildren = wrapChildren;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight() + "}";
    }





    // todo: deperecate instance method: always not supports for mul layers GUI, instead uses hover field.
    public final boolean isMouseOver() {
        return Gui.isMouseOver(getX(), getY(), getWidth(), getHeight());
    }

    public static boolean isMouseOver(Gui g) {
        return Gui.isMouseOver(g.getX(), g.getY(), g.getWidth(), g.getHeight());
    }
    public static boolean isMouseOver(float x, float y, float width, float height) {
        return Outskirts.getMouseX() >= x && Outskirts.getMouseX() < x + width && Outskirts.getMouseY() >= y && Outskirts.getMouseY() < y + height;
    }

    public static boolean isPointOver(int pointX, int pointY, Gui gui) {
        return pointX >= gui.getX() && pointX < gui.getX() + gui.getWidth() && pointY >= gui.getY() && pointY < gui.getY() + gui.getHeight();
    }

    public static Vector2f calculateChildrenBound(Gui parent) {
        Vector2f bound = new Vector2f();
        for (int i = 0;i < parent.getChildCount();i++) {
            Gui child = parent.getGui(i);

            bound.x = Math.max(bound.x, child.getRelativeX() + child.getWidth());
            bound.y = Math.max(bound.y, child.getRelativeY() + child.getHeight());
        }
        return bound;
    }

    /**
     * Tool method for Gui.
     * not unmodifiableList getChildren() cause sometimes Gui's children'll be dynamic remove ( but now when removed, one gui's invoke'll be jump over
     * and {for size: get} is original method.
     * cause always use and have multi params, so not use static.
     */
    public final void forChildren(Consumer<Gui> visitor, boolean includeChildren, Predicate<Gui> eachpredicate) {
        // if iterating children use index, probably skip/repeat iteration-item when the list been edit(add/remove)
        for (int i = 0;i < getChildCount();i++) {
            Gui child = getGui(i);
            if (!eachpredicate.test(child)) continue;

            visitor.accept(child);

            if (includeChildren && child.getChildCount() > 0) {
                child.forChildren(visitor, true, eachpredicate);
            }
        }
    }
    public final void forChildren(Consumer<Gui> visitor, boolean includeChildren) {
        forChildren(visitor, includeChildren, g -> true);
    }

    //should this..?
    public static void toggleVisible(Gui gui) {
        gui.setVisible(!gui.isVisible());
    }




    public final void onDraw() {
        if (!isVisible()) return;
        _checks_MouseInOut();

        boolean isClip = isClipChildren(); // avoid field dynamic changed

        performEvent(new OnDrawEvent()); // OnDraw

        if (isClip)
            GuiRenderer.pushScissor(getX(), getY(), getWidth(), getHeight());

        for (Gui child : getChildren())
            child.onDraw();

        if (isClip)
            GuiRenderer.popScissor();
    }

    private boolean _isPrevMouseOver = false;
    private void _checks_MouseInOut() {
        boolean isCurrMouseOver = Gui.isMouseOver(this);
        if (isCurrMouseOver && !_isPrevMouseOver) {
            setHover(true);
            performEvent(new OnMouseInEvent());
        } else if (!isCurrMouseOver && _isPrevMouseOver) {
            setHover(false);
            performEvent(new OnMouseOutEvent());
        }
        _isPrevMouseOver = isCurrMouseOver;
    }



    public final void onLayout() {
        if (!isVisible()) return;

        // layout children first or layout 'this' first.?
        // cuz sometimes this size dependents children, but sometimes this size dependents parent.
        performEvent(new OnLayoutEvent());

        for (Gui child : children)
            child.onLayout();

        if (isWrapChildren())
            _doSizeWrapChildren();
    }

    private void _doSizeWrapChildren() {
        float mxxs=0, mxys=0;
        for (Gui g : children) {
            mxxs = Math.max(mxxs, g.getRelativeX()+g.getWidth());
            mxys = Math.max(mxys, g.getRelativeY()+g.getHeight());
        }
        setWidth(mxxs);
        setHeight(mxys);
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
    public final boolean broadcaseEvent(Event event) { // post to all children gui
        forChildren(c -> {
            c.performEvent(event);
        }, true, Gui::isVisible); // Only Post to !isVisible() Guis.
        return Cancellable.isCancelled(event);
    }

    protected final <E extends Event> EventBus.Handler attachListener(Class<E> eventClass, Consumer<E> eventListener) {
        return eventBus.register(eventClass, eventListener);
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
    public final EventBus.Handler addMouseButtonListener(Consumer<MouseButtonEvent> listener) {
        return attachListener(MouseButtonEvent.class, listener);
    }
    public final EventBus.Handler addMouseMoveListener(Consumer<MouseMoveEvent> listener) {
        return attachListener(MouseMoveEvent.class, listener);
    }
    public final EventBus.Handler addKeyboardListener(Consumer<KeyboardEvent> listener) {
        return attachListener(KeyboardEvent.class, listener);
    }
    public final EventBus.Handler addCharInputListener(Consumer<CharInputEvent> listener) {
        return attachListener(CharInputEvent.class, listener);
    }
    public final EventBus.Handler addMouseScrollListener(Consumer<MouseScrollEvent> listener) {
        return attachListener(MouseScrollEvent.class, listener);
    }


    public final EventBus.Handler addOnMouseInListener(Consumer<OnMouseInEvent> listener) {
        return attachListener(OnMouseInEvent.class, listener);
    }
    public final EventBus.Handler addOnMouseOutListener(Consumer<OnMouseOutEvent> listener) {
        return attachListener(OnMouseOutEvent.class, listener);
    }
    public final EventBus.Handler addOnDrawListener(Consumer<OnDrawEvent> listener) {
        return attachListener(OnDrawEvent.class, listener);
    }
    public final EventBus.Handler addOnLayoutListener(Consumer<OnLayoutEvent> listener) {
        return attachListener(OnLayoutEvent.class, listener);
    }

    // AlignParentLTRB
    public final void addLayoutorAlignParentLTRB(float left, float top, float right, float bottom) { // in "pixels". param-b can be NaN. i.e. not to set.
        addOnLayoutListener(e -> {
            if (!Float.isNaN(left)) setRelativeX(left);
            if (!Float.isNaN(top)) setRelativeY(top);

            if (!Float.isNaN(right)) {
                if (!Float.isNaN(left)) setWidth(getParent().getWidth() - (right+left));
                else setRelativeX(getParent().getWidth() - (right+getWidth()));
            }
            if (!Float.isNaN(bottom)) {
                if (!Float.isNaN(top)) setHeight(getParent().getHeight() - (top+bottom));
                else setRelativeY(getParent().getHeight() - (bottom+getHeight()));
            }
        });
    }
    public final void addLayoutorAlignParentRR(float rrx, float rry) { // RestRatio. 0:left, 0.5f:mid, 1:right
        addOnLayoutListener(e -> {
            if (!Float.isNaN(rrx))
                setRelativeX((getParent().getWidth()-getWidth())*rrx);
            if (!Float.isNaN(rry))
                setRelativeY((getParent().getHeight()-getHeight())*rry);
        });
    }
    /**
     * A module tool for Mouse-Dragging
     * onDragging() when MouseOver-pressed AND moving until release pressing
     *
     * mouse dragging shouldn't use this way:
     * if (isMouseOver()) position += mouse.deltaXY;
     *
     * cause f mouse move too fast, the tricking will be lose,
     * and this way f mouse out gui border, mouse will not be tricking continue
     */
    public final void addOnDraggingListener(BiConsumer<Float, Float> ondragging, Consumer<Boolean> ondragstatechanged, Predicate<MouseButtonEvent> predictCanDrag) {
//        checkTrigger_OnDragging();
//        attachListener(OnDraggingEvent.class, listener);
        boolean[] dragging = {false};
        addMouseButtonListener(e -> {
            if (e.getMouseButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (e.getButtonState() && isHover() && (predictCanDrag==null || predictCanDrag.test(e))) {
                    dragging[0] = true;
                    if (ondragstatechanged!=null)ondragstatechanged.accept(true);
                } else if (dragging[0] && !e.getButtonState()) {
                    dragging[0] = false;
                    if (ondragstatechanged!=null)ondragstatechanged.accept(false);
                }
            }
        });
        addMouseMoveListener(e -> {
            if (dragging[0]) {
                ondragging.accept(Outskirts.getMouseDX(), Outskirts.getMouseDY());  // todo had not ext.test yet
            }
        });
    }
    public final void addOnDraggingListener(BiConsumer<Float, Float> ondragging) {
        addOnDraggingListener(ondragging, null, null);
    }

    protected static class OnMouseOutEvent extends GuiEvent { }

    protected static class OnMouseInEvent extends GuiEvent { }

    public static class OnDrawEvent extends GuiEvent { }

    public static class OnLayoutEvent extends GuiEvent { }

    public static class OnClickEvent extends GuiEvent { }













    //////////////////////////// START GLOBAL DRAW ////////////////////////////

//    public static Vector2i drawString(String texts, int x, int y, Vector4f color, int height, boolean centerHorizontal) {
//        if (centerHorizontal) {
//            int textWidth = Outskirts.renderEngine.getFontRenderer().stringWidth(texts, height);
//            x = x - (textWidth / 2);
//        }
//        return Outskirts.renderEngine.getFontRenderer().drawString(texts, x, y, height, color, true);
//    }

    public static void drawString(String text, float x, float y, Vector4f color, int height, boolean centerHorizontal, boolean drawShadow) {
        if (centerHorizontal) { // shoulddo tex_x = t * (max_width - tex_width)
            int textWidth = Outskirts.renderEngine.getFontRenderer().calculateBound(text, height).x;
            x -= textWidth/2f;
        }
        Outskirts.renderEngine.getFontRenderer().renderString(text, x, y, height, color, drawShadow);
    }
    public static void drawString(String text, float x, float y, Vector4f color, int height, boolean centerHorizontal) {
        drawString(text, x, y, color, height, centerHorizontal, true);
    }
    public static void drawString(String text, float x, float y, Vector4f color, int height) {
        drawString(text, x, y, color, height, false);
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



    }



    //////////////////////////// END GLOBAL DRAW ////////////////////////////




    public static final class Insets {

        public static final Insets ZERO = new Insets(0, 0, 0, 0);

        public float left;
        public float top;
        public  float right;
        public float bottom;

        public Insets() {}

        public Insets(float left, float top, float right, float bottom) {
            set(left, top, right, bottom);
        }

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
    }

}
