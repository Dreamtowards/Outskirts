package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiText;
import outskirts.client.render.renderer.gui.FontRenderer;
import outskirts.util.Colors;
import outskirts.util.StringUtils;
import outskirts.util.profiler.Profiler;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.input.Keyboard.KEY_P;

// the visual actually is not very accurate, because the profiling data is half-prev-frame, half-curr-frame.
public class GuiProfilerVisual extends Gui {

    private Profiler profiler = Outskirts.getProfiler();
    private float rootTimeNano;
    private Vector4f TMP_COLOR_TRANS = new Vector4f();
    private Profiler.Section mouseOverSection;

    public Profiler.Section displayingSection;
    public float fixedRootTimeNano = 0f*1_000_000; // -1 == Disable this FixedRoottime

    private void drawSectionNav(float x, float y, Profiler.Section section, int i, String[] secNames) {
        drawString(secNames[i], x, y, Colors.BLACK, 16, false, false);
        float nameWid = Outskirts.renderEngine.getFontRenderer().calculateBound(secNames[i], GuiText.DEFAULT_TEXT_HEIGHT).x;
        if (isMouseOver(x, y, nameWid, 16)) {
            drawRect(Colors.BLACK40, x, y, nameWid, 16);
            if (Outskirts.isMouseDown(0))
                displayingSection=section;
            float sy = y-section.subs.size()*16;
            for (Profiler.Section s : section.subs) {
                drawRect(Colors.BLACK, x, sy, 150, 16);
                float precen = FUNC_profvalue(s)/FUNC_profvalue(section)*100;
                drawString(s.name+"  "+(int)(precen)+"%", x, sy, Colors.WHITE); sy+=16;
            }
        }
        if (i+1 < secNames.length) {
            for (Profiler.Section s : section.subs) {
                if (s.name.equals(secNames[i+1])) {
                    drawString(".", x+nameWid, y, Colors.GRAY, 16, false, false);
                    drawSectionNav(x+nameWid+5, y, s, i + 1, secNames);
                }
            }
        }
    }

    {

        setWidth(300);
        setHeight(450);

        addKeyboardListener(e -> {
            if (e.getKeyState() && e.getKey() == KEY_P) {
                if (Outskirts.isAltKeyDown())
                    profiler.setEnable(!profiler.isEnable());
                else
                    Profiler.clearProfilerInfo(profiler.getRootSection());
            }
        });
        addOnClickListener(e -> {
            if (mouseOverSection!=null) {
                displayingSection=mouseOverSection;
            }
        });

        addOnDrawListener(e -> {

            Outskirts.getProfiler().push("profOnDraw");

            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());

            {   // update RootSection from its sub.
                profiler.getRootSection().totalUsedTimeNano=0;
                profiler.getRootSection().calledCounter=0;
                for (Profiler.Section sub : profiler.getRootSection().subs) {
                    profiler.getRootSection().totalUsedTimeNano += sub.totalUsedTimeNano;
                    profiler.getRootSection().calledCounter=sub.calledCounter;
                }
            }

            drawString("RootTimeMS: "+rootTimeNano/1_000_000, getX(), getY()-32, Colors.WHITE40);
            if (!profiler.isEnable()) {
                drawString("#ProfilerDisabled", getX(), getY()-16, Colors.DARK_RED);
            }

            mouseOverSection = null;
            if (displayingSection==null)displayingSection=profiler.getRootSection();
//            gDisplaySectionName.setText(displayingSection.getFullName());
//            Log.LOGGER.info(StringUtils.explode(displayingSection.getFullName(), ".").length);
            drawSection(displayingSection, 0, 0);
            drawSectionNav(getX(), getY()-16, profiler.getRootSection(), 0, StringUtils.explode(displayingSection.getFullName(), "."));
            Outskirts.getProfiler().pop("profOnDraw");

            // draw mouse-over section info
            if (!Outskirts.isIngame() && isHover() && mouseOverSection != null) {
                drawRect(Colors.BLACK10, Outskirts.getMouseX(), Outskirts.getMouseY() - 128, 200, 112);
                drawString(String.format("[%s] \n%s \navgT=%sms \nlT: %sms \ntT: %sms \nC=%s", mouseOverSection.name, mouseOverSection.getFullName(),
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
            if (fixedRootTimeNano > 0) {
                rootTimeNano = fixedRootTimeNano;
            } else {
                rootTimeNano = FUNC_profvalue(section);
            }
        }

        float height = (FUNC_profvalue(section)/rootTimeNano) * getHeight();

        Colors.fromRGBA(section.name.hashCode() | 0xFF, TMP_COLOR_TRANS);
        drawRect(TMP_COLOR_TRANS, getX() + relX, getY() + relY, getWidth() - relX, height);
        drawString(section.name, getX() + relX, getY() + relY, Colors.WHITE, 14);

        if (!Outskirts.isIngame() && isHover() && Gui.isMouseOver(getX()+relX, getY()+relY, getWidth()-relX, height)) {
            mouseOverSection = section;
            drawRect(Colors.WHITE20, getX() + relX, getY() + relY, getWidth() - relX, height);
        }

        for (Profiler.Section sec : section.subs) {
            relY += drawSection(sec, depth + 1, relY);
        }
        return height;
    }
}
