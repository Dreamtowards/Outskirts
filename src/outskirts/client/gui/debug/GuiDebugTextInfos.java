package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.SystemUtils;

public class GuiDebugTextInfos extends Gui {

    private float deltaSumUntilOne = 0;
    private int currSecFrames = 0;
    private int prevSecFrames = 0;

    {
        addOnDrawListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("\nU/T: %s / %s | JVM_Max: %s\n", FileUtils.toDisplaySize(SystemUtils.MEM_USED), FileUtils.toDisplaySize(SystemUtils.MEM_TOTAL), FileUtils.toDisplaySize(SystemUtils.MEM_MAXIMUM)));

            currSecFrames++;
            deltaSumUntilOne += Outskirts.getDelta();
            if (deltaSumUntilOne >= 1f) {
                prevSecFrames = currSecFrames;
                deltaSumUntilOne = 0;
                currSecFrames = 0;
            }
            sb.append(String.format("P: avgT: %sms, s: %s\n", 1000f/prevSecFrames, prevSecFrames));

            sb.append(String.format("CameraPos: %s\n", Outskirts.getCamera().getPosition()));

            drawString(sb.toString(), getX(), getY(), Colors.WHITE);
        });
    }

}
