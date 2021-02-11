package outskirts.client.gui.screen;

import org.lwjgl.glfw.GLFW;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiScrollPanel;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.GuiTextBox;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.event.EventPriority;
import outskirts.util.Colors;

import static outskirts.util.logging.Log.LOGGER;

public class GuiScreenChat extends Gui {

    public static final GuiScreenChat INSTANCE = new GuiScreenChat();

    private GuiColumn msgLs;

    private GuiScreenChat() {
        setWidth(INFINITY);
        setHeight(INFINITY);
        initEscClose(this);

        addChildren(
          new GuiTextBox().exec((GuiTextBox g) -> {
              g.setMaxLines(1);
              g.addLayoutorAlignParentLTRB(2, NaN, 80, 2);
              g.setHeight(20);
              g.getText().setRelativeXY(4, 2);
              g.removeListeners(EVTAG_DEFDECO);
              g.addOnDrawListener(e -> {
                  drawRect(g.isFocused() ? Colors.BLACK80 : Colors.BLACK40, g);
              }).priority(EventPriority.HIGH);
              g.addOnDetachListener(e -> {
                  g.getText().setText("");
              });
              g.addOnAttachListener(e -> {
                  g.setFocused(true);
              });

              g.addKeyboardListener(e -> {
                  if (e.getKeyState() && e.getKey()== GLFW.GLFW_KEY_ENTER) {
                      String s = g.getText().getText();
                      if (s.isEmpty()) return;
                      printMessage(s);
                      g.getText().setText("");
                  }
              });
          }),
          new GuiScrollPanel().exec((GuiScrollPanel g) -> {
              g.addLayoutorAlignParentLTRB(2, 80, 80, 24);
              g.addOnDrawListener(e -> {
                  drawRect(Colors.BLACK40, g.getX(), Math.max(g.getY(), g.getContent().getY()), g.getWidth(), Math.min(g.getHeight(), g.getContent().getHeight()));
              });
              g.addOnLayoutListener(e -> {
                  Gui gContent = g.getContent();
                  if (gContent.getHeight() < g.getHeight()) {
                      gContent.setRelativeY(g.getHeight() - gContent.getHeight());
                  }
              });
          }).setContent(msgLs=new GuiColumn())
        );

    }

    public void printMessage(String s) {
        GuiText gText = new GuiText(s);
        msgLs.addGui(gText);
        if (msgLs.getHeight() + msgLs.getRelativeY() == msgLs.getParent().getParent().getHeight()) { // when scoll is on bottom, keep bottom.
            msgLs.setRelativeY(msgLs.getRelativeY()-gText.getHeight());
            msgLs.onLayout();  // update height, prevents scroll clamp.
        }
    }

}
