package outskirts.client.gui.screen.tools.debug;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.util.Colors;
import outskirts.util.profiler.Profiler;
import outskirts.util.vector.Vector4f;

// the visual actually is not very accurate, because the profiling data is half-prev-frame, half-curr-frame.
public class GuiProfilerVisual extends Gui {

    private Profiler profiler = Outskirts.getProfiler();
    private float rootTimeNano;
    private Vector4f TMP_COLOR_TRANS = new Vector4f();
    private Profiler.Section mouseOverSection;

    private static final float OP_FIXED_ROOTTIME_NANO = 2*1_000_000; // -1 == Disable this FixedRoottime

    public GuiProfilerVisual() {

        addKeyboardListener(e -> {
            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_P) {
                if (Outskirts.isAltKeyDown())
                    profiler.setEnable(!profiler.isEnable());
                else
                    profiler.clearProfilerInfo(profiler.getRootSection());
            }
        });

        addOnDrawListener(e -> {

            Outskirts.getProfiler().push("profOnDraw");

            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());

            mouseOverSection = null;
            drawSection(profiler.getRootSection(), 0, 0);
            Outskirts.getProfiler().pop("profOnDraw");

            if (!profiler.isEnable()) {
                drawString("#ProfilerDisabled", getX(), getY()-16, Colors.DARK_RED);
            }
            drawString("RootTimeMS: "+rootTimeNano/1_000_000, getX(), getY()-32, Colors.WHITE40);

            // draw mouse-over section info
            if (Outskirts.currentScreen() != Gui.EMPTY && mouseOverSection != null) {
                StringBuilder sectionID = new StringBuilder(mouseOverSection.name);
                Profiler.Section sec = mouseOverSection;
                while (sec.parent != null) {
                    sec = sec.parent;
                    sectionID.insert(0, ".").insert(0, sec.name);
                }

                drawString(String.format("[%s] \n%s \navgT=%sms \nlT: %sms \ntT: %sms \nC=%s", mouseOverSection.name, sectionID.toString(),
                        mouseOverSection.totalUsedTimeNano/1_000_000f/mouseOverSection.calledCounter,
                        mouseOverSection.lastUsedTimeNano/1_000_000f,
                        mouseOverSection.totalUsedTimeNano/1_000_000f,
                        mouseOverSection.calledCounter),
                        Outskirts.getMouseX(), Outskirts.getMouseY() - 128, Colors.WHITE);
            }

        });
    }

    private float FUNC_profvalue(Profiler.Section section) {
        return (float)section.totalUsedTimeNano/Math.max(section.calledCounter, 1);
    }

    private float drawSection(Profiler.Section section, int depth, int relY) {
        int LEVEL_GAP_X = 15;
        int relX = depth * LEVEL_GAP_X;
        if (depth == 0) {
            if (OP_FIXED_ROOTTIME_NANO != -1) {
                rootTimeNano = OP_FIXED_ROOTTIME_NANO;
            } else {
                rootTimeNano = 0;
                for (Profiler.Section sub : section)
                    rootTimeNano += FUNC_profvalue(sub); // total..?! or last, avg ..?
            }
        }

        float height = (FUNC_profvalue(section)/rootTimeNano) * getHeight();

        Colors.fromRGBA(section.name.hashCode() | 0xFF, TMP_COLOR_TRANS);
        drawRect(TMP_COLOR_TRANS, getX() + relX, getY() + relY, getWidth() - relX, height);
        drawString(section.name, getX() + relX, getY() + relY, Colors.WHITE, 14);

        if (Outskirts.currentScreen() != Gui.EMPTY && Gui.isMouseOver(getX()+relX, getY()+relY, getWidth()-relX, height)) {
            mouseOverSection = section;
            drawRect(Colors.WHITE20, getX() + relX, getY() + relY, getWidth() - relX, height);
        }

        for (Profiler.Section sec : section) {
            relY += drawSection(sec, depth + 1, relY);
        }
        return height;
    }
}
