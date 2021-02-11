package outskirts.client.gui.screen;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiScrollPanel;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.GuiTextBox;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.command.Command;
import outskirts.event.EventPriority;
import outskirts.util.Colors;
import outskirts.util.vector.Vector2f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static outskirts.util.logging.Log.LOGGER;

public class GuiScreenChat extends Gui {

    public static final GuiScreenChat INSTANCE = new GuiScreenChat();

    private GuiColumn msgLs;

    private GuiTextBox tbInputBox;
    private GuiColumn completeLs;

    private GuiScreenChat() {
        setWidth(INFINITY);
        setHeight(INFINITY);
        initEscClose(this);

        addChildren(
          new GuiTextBox().exec((GuiTextBox g) -> {
              tbInputBox=g;
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
              g.addOnFocusChangedListener(e -> {
                  completeLs.setVisible(g.isFocused());
              });

              g.addKeyboardListener(e -> {
                  if (g.isFocused() && e.getKeyState()) {
                      if (e.getKey()== GLFW.GLFW_KEY_ENTER) {
                          String s = g.texts();
                          if (s.isEmpty()) return;
                          g.getText().setText("");
                          dispatchLine(s);
                      } else if (e.getKey()== GLFW.GLFW_KEY_TAB) {
                          updateTabComplete();
                      }
                  }
              });
              g.addOnCursorPositionChangedListener(e -> {
                  if (tbInputBox.texts().startsWith("/")) {
                      updateTabComplete();
                  } else {
                      completeLs.removeAllGuis();
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
          }).setContent(msgLs=new GuiColumn()),
          completeLs=new GuiColumn().exec(g -> {
              g.addOnDrawListener(e -> {
                  drawRect(Colors.BLACK80, g);
              });
              g.addOnLayoutListener(e -> {
                  String line = tbInputBox.texts();
                  int curpos;
                  if (line.startsWith("/")) {
                      curpos = line.lastIndexOf(' ', tbInputBox.getCursorPosition());
                      if (curpos == -1)
                          curpos = 1;
                      else
                          curpos = curpos+1;
                  } else {
                      curpos = tbInputBox.getCursorPosition();
                  }
                  Vector2f p = tbInputBox.calculateTextPosition(curpos);
                  g.setX(p.x);
                  g.setY(tbInputBox.getY()-g.getHeight());
              });
          })
        );

    }

    private void updateTabComplete() {
        int cursorpos = tbInputBox.getCursorPosition();
        String cmdline = tbInputBox.getText().getText().substring(0, cursorpos);
        if (cmdline.startsWith("/")) {
            updateGuiCompletes(Command.dispatchTabComplete(cmdline, cursorpos));
        } else {
            updateGuiCompletes(Collections.singletonList(Outskirts.getPlayer().getName()));
        }
    }
    private void updateGuiCompletes(List<String> completes) {
        completeLs.removeAllGuis();
        for (String complete : completes) {
            completeLs.addGui(new GuiText(complete));
        }
        completeLs.onLayout();
    }

    private void dispatchLine(String line) {
        if (line.startsWith("/")) {
            Command.dispatchCommand(Outskirts.getPlayer(), line);
        } else {
            printMessage("<"+ Outskirts.getPlayer().getName() +"> "+line);
        }
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
