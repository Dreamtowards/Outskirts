package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiImage;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiWrap;
import outskirts.client.render.Framebuffer;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.event.EventPriority;

import java.util.function.Consumer;

import static outskirts.client.render.renderer.gui.GuiRenderer.*;

public class GuiEntityGBufferVisual extends Gui {

    public GuiEntityGBufferVisual() {

        Framebuffer fbo = Outskirts.renderEngine.gBufferFBO;

        addChildren(new GuiColumn().addChildren(
          new GuiImage(fbo.colorTextures(0)).exec(texprop(TEXMODE_RGB1)).addChildren(new GuiText("Position")),
          new GuiImage(fbo.colorTextures(0)).exec(texprop(TEXMODE_AAA1)).addChildren(new GuiText("Depth")),
          new GuiImage(fbo.colorTextures(1)).exec(texprop(TEXMODE_RGBA)).addChildren(new GuiText("Normal")),
          new GuiImage(fbo.colorTextures(2)).exec(texprop(TEXMODE_RGB1)).addChildren(new GuiText("Albedo")),
//          new GuiImage(fbo.colorTextures(2)).exec(texprop(TEXMODE_AAA1)).addChildren(new GuiText("Specular")),
          new GuiImage(Outskirts.renderEngine.ssaoBlurFBO.colorTextures(0)).exec(texprop(TEXMODE_RGB1)).addChildren(new GuiText("SSAOBlur")),
          new GuiImage(Outskirts.renderEngine.getShadowRenderer().getDepthMapTexture()).exec(texprop(TEXMODE_RGB1)).addChildren(new GuiText("ShadowDMap"))
        ));

    }

    private Consumer<Gui> texprop(int texmode) {
        return g -> {
            g.setWidth(240);
            g.setHeight(160);
            g.addOnDrawListener(e -> {
                GuiRenderer.OP_texmode = texmode;
            }).priority(EventPriority.HIGH);
        };
    }
}
