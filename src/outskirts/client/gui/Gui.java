package outskirts.client.gui;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.screen.GuiScreenDebug;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.event.Cancellable;
import outskirts.event.Event;
import outskirts.event.EventBus;
import outskirts.event.client.input.*;
import outskirts.event.gui.GuiEvent;
import outskirts.util.*;
import outskirts.util.function.TriConsumer;
import outskirts.util.function.TriFunction;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.*;

import static org.lwjgl.glfw.GLFW.*;


public class Gui {

    private float x; //actually is relative-x
    private float y;
    private float width;
    private float height;

    private boolean focused = false;

    /** efforts to onClickEvent... */
    private boolean enable = true;

    /** f not VISIBLE, the gui parent will not call onDraw() automatically */
    private boolean visible = true;

    private boolean clipChildren = false;

    /** just a attachment */
    private Object tag;

    //they are not tint.(colorMultiply, opacity) cause other renderer would't supports, its high-level stuff

    private Gui parent;
    private List<Gui> children = new ArrayList<>();

    private EventBus eventBus = new EventBus().listFactory(CopyOnIterateArrayList::new);

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
            if (e.getMouseButton() == GLFW_MOUSE_BUTTON_LEFT && e.getButtonState())
                setFocused(isMouseOver());
        });
    }

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
        GuiRenderer.PARAM_colorMultiply.set(color);
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, Texture.UNIT, x, y, width, height);
    }

    public static void drawTexture(Texture texture, float x, float y, float width, float height) {
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, texture, x, y, width, height);
    }
    public static void drawTexture(Texture texture, float x, float y, float width, float height, float texOffsetX, float texOffsetY, float texScaleX, float texScaleY) {
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, texture, x, y, width, height, texOffsetX, texOffsetY, texScaleX, texScaleY);
    }

    public static void drawWorldpoint(Vector3f worldposition, BiConsumer<Float, Float> lsr) {
        Vector4f v = Maths.calculateDisplayPosition(worldposition, Outskirts.renderEngine.getProjectionMatrix(), Outskirts.renderEngine.getViewMatrix(), null);
        if (v.z > 0) {
            lsr.accept(v.x*Outskirts.getWidth(), v.y*Outskirts.getHeight());
        }
    }

    /**
     * A border inner the [x, y, width, height] size
     */
    public static void drawRectBorder(Vector4f color, float x, float y, float width, float height, float thickness) {

        drawRect(color, x, y, width, thickness); //Top
        drawRect(color, x, y + height - thickness, width, thickness); //Bottom

        drawRect(color, x, y + thickness, thickness, height - thickness - thickness); //Left
        drawRect(color, x + width - thickness, y + thickness, thickness, height - thickness - thickness); //Right
    }



    //////////////////////////// END GLOBAL DRAW ////////////////////////////


    //just some tool-type methods, calls some really methods for more-convenient, do not needs override this
    /**
     * @return the Gui which added
     */
    public final <T extends Gui> T addGui(T gui) {
        return addGui(gui, getChildCount());
    }

    public <T extends Gui> T addGui(T gui, int index) {
        gui.setParent(this);

        children.add(index, gui);

        return gui;
    }

    // todo: getGui(i) ..?
    public <T extends Gui> T getChildAt(int index) {
        return (T) children.get(index);
    }

    public final <T extends Gui> T setGui(int index, Gui gui) {
        if (index < getChildCount()) {
            removeGui(index);
        }
        addGui(gui, index);
        return (T)this;
    }

    public int getChildCount() {
        return children.size();
    }

    /**
     * @return the Gui which removed
     */
    public <T extends Gui> T removeGui(int index) {
        getChildAt(index).setParent(null);
        return (T) children.remove(index);
    }

    public final void removeAllGuis() {
        for (int i = getChildCount() - 1;i >= 0;i--) {
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






    public float getRelativeX() {
        return this.x;
    }
    public <T extends Gui> T setRelativeX(float x) {
        this.x = x;
        return  (T) this;
    }

    public float getRelativeY() {
        return this.y;
    }
    public <T extends Gui> T setRelativeY(float y) {
        this.y = y;
        return  (T) this;
    }

    /**
     * Get absolute x position.
     */
    public float getX() {
        return getParent().getX() + x;
    }
    public <T extends Gui> T setX(float x) {
        setRelativeX(x - getParent().getX());
        return (T) this;
    }

    public float getY() {
        return getParent().getY() + y;
    }
    public <T extends Gui> T setY(float y) {
        setRelativeY(y - getParent().getY());
        return  (T) this;
    }

    public float getWidth() {
        return width;
    }
    public <T extends Gui> T setWidth(float width) {
        this.width = width;
        return (T) this;
    }

    public float getHeight() {
        return height;
    }
    public <T extends Gui> T setHeight(float height) {
        this.height = height;
        return (T) this;
    }





    public boolean isFocused() {
        return focused;
    }
    public <T extends Gui> T setFocused(boolean focused) {
        this.focused = focused;
        return (T) this;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public boolean isEnable() {
        return enable;
    }

    public boolean isVisible() {
        return visible;
    }
    public <T extends Gui> T setVisible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    public boolean isClipChildren() {
        return clipChildren;
    }
    public <T extends Gui> T setClipChildren(boolean clipChildren) {
        this.clipChildren = clipChildren;
        return (T) this;
    }

    public Object getTag() {
        return tag;
    }
    public <T extends Gui> T setTag(Object tag) {
        this.tag = tag;
        return (T) this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight() + "}";
    }






    public final boolean isMouseOver() {
        return isMouseOver(getX(), getY(), getWidth(), getHeight());
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
            Gui child = parent.getChildAt(i);

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
    public final <T extends Gui> void forChildren(Consumer<T> visitor, boolean includeChildren, Predicate<Gui> selfpredicate) {
        if (!selfpredicate.test(this))
            return;
        // if iterating children use index, probably skip/repeat iteration-item when the list been edit(add/remove)
        for (int i = 0;i < getChildCount();i++) {
            T child = getChildAt(i);

            visitor.accept(child);

            if (includeChildren && child.getChildCount() > 0) {
                child.forChildren(visitor, true, selfpredicate);
            }
        }
    }
    public final <T extends Gui> void forChildren(Consumer<T> visitor, boolean includeChildren) {
        forChildren(visitor, includeChildren, g -> true);
    }
    public final <T extends Gui> void forChildren(Consumer<T> visitor) {
        forChildren(visitor, false);
    }

    //should this..?
    public static void toggleVisible(Gui gui) {
        gui.setVisible(!gui.isVisible());
    }




    public final void onDraw() {
        if (!isVisible())
            return;
        boolean isClip = isClipChildren(); // avoid field dynamic changed

        performEvent(new OnDrawEvent()); // OnDraw

        if (isClip)
            GuiRenderer.pushScissor(getX(), getY(), getWidth(), getHeight());

        forChildren(Gui::onDraw);

        if (isClip)
            GuiRenderer.popScissor();
    }






    // for getParent()(EMPTY_PARENT), draw nothing...
    public static final Gui EMPTY = new Gui() {
        @Override public float getX() { return 0; }
        @Override public float getY() { return 0; }
        @Override public float getWidth() { return 0; }
        @Override public float getHeight() { return 0; }
    };



    private static final Field _REF_FIELD_GUIEVENT_GUI = ReflectionUtils.getField(GuiEvent.class, "gui");

    public final boolean performEvent(Event event) {
        if (event instanceof GuiEvent && ((GuiEvent)event).gui() == null)
            ReflectionUtils.setFieldValue(_REF_FIELD_GUIEVENT_GUI, event, this);
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
    public final <T extends Gui> T attachTransform(float from, float to, float duration, BiConsumer<Gui, Float> applicator, Function<Float, Float> interpolator, int easeMode, float pass) {
        return addOnDrawListener(new Consumer<OnDrawEvent>() {
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


    public final <T extends Gui> T addOnClickListener(Consumer<MouseButtonEvent> listener) {
        return addMouseButtonListener(e -> {
            if (isVisible() && isEnable() && e.getButtonState() && e.getMouseButton() == GLFW_MOUSE_BUTTON_LEFT && isMouseOver()) {
                listener.accept(e);
            }
        });
    }

    // Global/ Events
    public final <T extends Gui> T addMouseButtonListener(Consumer<MouseButtonEvent> listener) {
        attachListener(MouseButtonEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addMouseMoveListener(Consumer<MouseMoveEvent> listener) {
        attachListener(MouseMoveEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addKeyboardListener(Consumer<KeyboardEvent> listener) {
        attachListener(KeyboardEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addCharInputListener(Consumer<CharInputEvent> listener) {
        attachListener(CharInputEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addMouseScrollListener(Consumer<MouseScrollEvent> listener) {
        attachListener(MouseScrollEvent.class, listener); return (T)this;
    }


    public final <T extends Gui> T addOnMouseEnteredListener(Consumer<MouseEnteredEvent> listener) {
        checkTrigger_OnMouseInOut();
        attachListener(MouseEnteredEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addOnMouseExitedListener(Consumer<MouseExitedEvent> listener) {
        checkTrigger_OnMouseInOut();
        attachListener(MouseExitedEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addOnDrawListener(Consumer<OnDrawEvent> listener) {
        attachListener(OnDrawEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addOnDrawListener(Consumer<OnDrawEvent> listener, int priority) {
        attachListener(OnDrawEvent.class, listener).priority(priority); return (T)this;
    }
    public final <T extends Gui> T addOnLayoutListener(Consumer<OnDrawEvent> listener) { // the event waiting to do related.
        attachListener(OnDrawEvent.class, listener); return (T)this;
    }

    public final <T extends Gui> T addLayoutorPercent(float xt, float yt) {
        return addOnLayoutListener(e -> {
            setX((getParent().getWidth()-getWidth())*xt);
            setY((getParent().getHeight()-getHeight())*yt);
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
    public final <T extends Gui> T addOnDraggingListener(Consumer<OnDraggingEvent> listener) {
        checkTrigger_OnDragging();
        attachListener(OnDraggingEvent.class, listener); return (T)this;
    }
    public final <T extends Gui> T addOnDraggingStateChangedListener(Consumer<OnDraggingStateChangedEvent> lsr) {
        checkTrigger_OnDragging();
        attachListener(OnDraggingStateChangedEvent.class, lsr); return (T)this;
    }

    protected static class MouseExitedEvent extends GuiEvent { }

    protected static class MouseEnteredEvent extends GuiEvent { }

    public static class OnDrawEvent extends GuiEvent { }

    protected static class OnDraggingEvent extends GuiEvent {
        private float dx;
        private float dy;
        public OnDraggingEvent(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }
        public float getDX() {
            return dx;
        }
        public float getDY() {
            return dy;
        }
    }

    public static class OnDraggingStateChangedEvent extends GuiEvent {
        private boolean dragging;
        public OnDraggingStateChangedEvent(boolean dragging) {
            this.dragging = dragging;
        }
        public boolean isDragging() {
            return dragging;
        }
    }


    private List<Class<? extends GuiEvent>> initializedTriggers = new ArrayList<>();

    private boolean isTriggerInitialized(Class t) {
        if (initializedTriggers.contains(t)) {
            return true;
        } else {
            initializedTriggers.add(t);
            return false;
        }
    }

    private void checkTrigger_OnDragging() {
        if (isTriggerInitialized(OnDraggingEvent.class))
            return;
        boolean[] dragging = {false};
        addMouseButtonListener(e -> {
            if (e.getMouseButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (e.getButtonState() && isMouseOver()) {
                    dragging[0] = true;
                    performEvent(new OnDraggingStateChangedEvent(true));
                }
                if (!e.getButtonState()) {
                    dragging[0] = false;
                    performEvent(new OnDraggingStateChangedEvent(false));
                }
            }
        });
        addMouseMoveListener(e -> {
            if (dragging[0]) {
                performEvent(new OnDraggingEvent(Outskirts.getMouseDX(), Outskirts.getMouseDY())); // todo had not ext.test yet
            }
        });
    }

    private void checkTrigger_OnMouseInOut() {
        if (isTriggerInitialized(MouseEnteredEvent.class))
            return;
        if (isTriggerInitialized(MouseExitedEvent.class))
            return;
        boolean[] isPrevMouseOver = {false};
        addOnDrawListener(e -> {
            boolean isMouseOver = isMouseOver();
            if (isMouseOver && !isPrevMouseOver[0]) {
                performEvent(new MouseEnteredEvent());
            } else if (!isMouseOver && isPrevMouseOver[0]) {
                performEvent(new MouseExitedEvent());
            }
            isPrevMouseOver[0] = isMouseOver;
        });
    }
}
