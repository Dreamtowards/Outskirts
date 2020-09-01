package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.Maths;
import outskirts.util.SystemUtils;
import outskirts.util.vector.Vector4f;

public class GuiMemoryLog extends Gui {

    private float cycleTime = 10; // in seconds
    private long[] memlog = new long[200];
    private float elapsedPartial = 0;
    private int writePointer;

    {
        setWidth(600);
        setHeight(300);

        addOnDrawListener(e -> {
            SystemUtils.updateMemoryInfo(); // Utils supports..  // updates states

            float UNIT_TIME_LEN = cycleTime / memlog.length; // timeLength of a log-unit. in seconds.

            elapsedPartial += Outskirts.getDelta();
            if (elapsedPartial > UNIT_TIME_LEN) {
                elapsedPartial = 0;
                memlog[writePointer = (++writePointer % memlog.length)] = SystemUtils.MEM_USED;
            }

            long memMax = SystemUtils.MEM_MAXIMUM;
            long memTotal = SystemUtils.MEM_TOTAL;

            // draw background
            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());

            // draw vm maximum mark
            drawString("VM Maximum ("+ FileUtils.toDisplaySize(memMax)+")", getX(), getY(), Colors.GRAY);
            drawRect(Colors.DARK_RED, getX(), getY(), getWidth(), 2);

            // draw total mark
            float totRelY = getHeight() - (getHeight() * ((float)memTotal / memMax));
            drawString("Allocated Total ("+FileUtils.toDisplaySize(memTotal)+")", getX(), getY() + totRelY, Colors.GRAY);
            drawRect(Colors.BLACK, getX(), getY() + totRelY, getWidth(), 2);

            // draw used
            int UNIT_WIDTH = Maths.ceil((float)getWidth() / memlog.length);
            for (int i = 0;i < memlog.length;i++) {
                float relX = ((float)i / memlog.length) * getWidth();
                float height = ((float)memlog[i] / memMax) * getHeight();

                Vector4f color = Colors.GREEN;
                if (i > writePointer)
                    color = Colors.DARK_GREEN;

                drawRect(color, getX()+relX, getY() + getHeight() - height, UNIT_WIDTH, height);
            }

            // draw mouse-over
            if (!Outskirts.isIngame() && isHover()) {
                int memIndex = (int)(((Outskirts.getMouseX() - getX()) / getWidth()) * memlog.length);
                int relX = (int)(((float)memIndex / memlog.length) * getWidth());

                drawRect(Colors.WHITE20, getX() + relX, getY(), UNIT_WIDTH, getHeight());
                drawString(FileUtils.toDisplaySize(memlog[memIndex]) + " ["+memIndex+"]", Outskirts.getMouseX(), Outskirts.getMouseY() - 18, Colors.WHITE);
            }
        });
    }
}
