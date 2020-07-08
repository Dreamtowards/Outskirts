package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.screen.ingame.GuiChatMessages;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Colors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class GuiScreenInGame extends Gui {

    public static final GuiScreenInGame INSTANCE = new GuiScreenInGame();

    private GuiChatMessages guiChatMessages = addGui(new GuiChatMessages());

    public GuiScreenInGame() {

        addOnDrawListener(e -> {


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

    public GuiChatMessages getGuiChatMessages() {
        return guiChatMessages;
    }
}
