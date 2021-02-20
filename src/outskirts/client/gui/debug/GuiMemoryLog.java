package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiText;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.Maths;
import outskirts.util.SystemUtil;
import outskirts.util.vector.Vector4f;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec4;
import static outskirts.util.logging.Log.LOGGER;

public class GuiMemoryLog extends Gui {

    private static final int LOG_SIZE = 200;
    private float fullCycleTime = 10; // in seconds
    private long[] memlog = new long[LOG_SIZE];
    private long[] dmemlog = new long[LOG_SIZE];
    private float elapsedPartial = 0;
    private int writePointer;

    {
        setWidth(600);
        setHeight(300);

        addChildren(
          new GuiText("GX").exec((GuiText g) -> {
              g.addOnDrawListener(e -> g.getTextColor().set(g.isHover() ? Colors.YELLOW : Colors.WHITE));
              g.addOnClickListener(e -> System.gc());
              g.setRelativeXY(0, 16);
          })
        );

        addOnDrawListener(e -> {
            SystemUtil.updateMemoryInfo(); // Utils supports..  // updates states
            SystemUtil.updateDirectMemoryInfo();

            float oneLogTimeLen = fullCycleTime / LOG_SIZE; // one log timeLength. in seconds.

            elapsedPartial += Outskirts.getDelta();
            if (elapsedPartial > oneLogTimeLen) {
                elapsedPartial = 0;
                writePointer++;
                writePointer %= LOG_SIZE;
                memlog[writePointer] = SystemUtil.MEM_USED;
                dmemlog[writePointer] = SystemUtil.DMEM_RESERVED;
            }

            long memMax = SystemUtil.MEM_MAXIMUM;
            long memTotal = SystemUtil.MEM_TOTAL;

            long dmemMax = SystemUtil.DMEM_MAX;

            // draw background
            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());

            // draw vm maximum mark
            drawString("VM Heap Maximum ("+ FileUtils.toDisplaySize(memMax)+")", getX(), getY(), Colors.GRAY);
            drawRect(Colors.DARK_RED, getX(), getY(), getWidth(), 2);

            float dmemSY = getY()+ (((float)dmemMax / memMax)-1f)*getHeight();
            drawString("VM DirectMemory Max ("+ FileUtils.toDisplaySize(dmemMax)+")", getX()+getWidth(), dmemSY, Colors.GRAY, 16, 1f);
            drawRect(Colors.DARK_RED, getX(), dmemSY, getWidth(), 2);


            // draw total mark
            float totRelY = getHeight() - (getHeight() * ((float)memTotal / memMax));
            drawString("Heap Allocated Total ("+FileUtils.toDisplaySize(memTotal)+")", getX(), getY() + totRelY, Colors.GRAY);
            drawRect(Colors.BLACK, getX(), getY() + totRelY, getWidth(), 2);

            // draw used
            int LOG_WIDTH = Maths.ceil(getWidth() / LOG_SIZE);
            for (int i = 0;i < LOG_SIZE;i++) {
                float logx = getX() + ((float)i / LOG_SIZE) * getWidth();
                float bottom = getY()+getHeight();
                float height = ((float)memlog[i] / memMax) * getHeight();
                float dheight = ((float)dmemlog[i] / memMax) * getHeight();

                Vector4f color = i <= writePointer ? Colors.GREEN : Colors.DARK_GREEN;
                drawRect(color, logx, bottom- height, LOG_WIDTH, height);

                Vector4f dcolor = i <= writePointer ? Colors.GRAY : Colors.DARK_GRAY;
                drawRect(vec4(dcolor).setW(.8f), logx, bottom- dheight, LOG_WIDTH, dheight);
            }

            // draw mouse-over
            if (!Outskirts.isIngame() && isHover()) {
                int logIdx = (int)(((Outskirts.getMouseX() - getX()) / getWidth()) * LOG_SIZE);
                int relX = logIdx * LOG_WIDTH;

                drawRect(Colors.WHITE20, getX() + relX, getY(), LOG_WIDTH, getHeight());
                drawString("Heap: "+FileUtils.toDisplaySize(memlog[logIdx])+" | Direct: "+FileUtils.toDisplaySize(dmemlog[logIdx])+"  ["+logIdx+"]", Outskirts.getMouseX(), Outskirts.getMouseY() - 18, Colors.WHITE);
            }
        });
    }
}
