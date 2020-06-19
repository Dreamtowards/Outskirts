package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.screen.ingame.GuiChatMessages;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Colors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class GuiScreenInGame extends GuiScreen {

    private GuiScreenDebug screenDebug = addGui(new GuiScreenDebug()).setVisible(false);
    private GuiChatMessages guiChatMessages = addGui(new GuiChatMessages());

    public GuiScreenInGame() {

        addOnDrawListener(e -> {

            int POINTER_SIZE = 4;

            drawRect(Colors.WHITE, Outskirts.getWidth()/2f-POINTER_SIZE/2f, Outskirts.getHeight()/2f-POINTER_SIZE/2f, POINTER_SIZE, POINTER_SIZE);

            // block selection bound
//        Octree octree = Outskirts.getRayPicker().getCurrentOctree();
//        if (octree != null) {
//            glEnable(GL_DEPTH_TEST);
//
//            AABB octreeAABB = octree.getAABB(new AABB());
//            Outskirts.renderEngine.getModelRenderer().drawOutline(octreeAABB, Colors.WHITE);
//
//            glDisable(GL_DEPTH_TEST);
//        }
        });
    }

    public GuiScreenDebug getGuiScreenDebug() {
        return screenDebug;
    }

    public GuiChatMessages getGuiChatMessages() {
        return guiChatMessages;
    }
}
