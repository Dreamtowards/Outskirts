package outskirts.client.gui.debug;

import outskirts.block.Block;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.render.chunk.MarchingCubes;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.SystemUtils;
import outskirts.util.vector.Vector3f;

public class GuiDebugTxInfos extends Gui {

    public static GuiDebugTxInfos INSTANCE = new GuiDebugTxInfos();

    private GuiDebugTxInfos() { }

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

            sb.append(String.format("RP_Pointer: %s\n", Outskirts.getRayPicker().getCurrentPoint()));

            Vector3f bpos = Outskirts.getRayPicker().getCurrentBlockPos();
            if (bpos != null) {
                Block b = Outskirts.getRayPicker().getCurrentBlock();
                sb.append(String.format("BlockPos: (%s/ %s) (%s) %s v:\n", b.v, MarchingCubes.cubeidx(0, (x, y, z) -> {
                    Block bl = Outskirts.getWorld().getBlock(bpos.x + x, bpos.y + y, bpos.z + z);
                    return bl != null && !bl.isTranslucent() ? bl.v : -0.5f;
                }), b, bpos));
            }

            drawString(sb.toString(), getX(), getY()+32, Colors.WHITE);
        });
    }

}
