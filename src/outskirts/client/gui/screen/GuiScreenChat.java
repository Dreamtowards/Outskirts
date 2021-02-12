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
import outskirts.util.CollectionUtils;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static outskirts.util.logging.Log.LOGGER;

public class GuiScreenChat extends Gui {

    public static final GuiScreenChat INSTANCE = new GuiScreenChat();

    private boolean enableAutoCommandComplete = false;

    private GuiColumn msgLs;

    private GuiTextBox tbInputBox;

    private GuiColumn completeLs;
    private int tabItemIdx;
    private int lastTabCompleteStartIdx;

    private List<String> histories = new ArrayList<>(Collections.singletonList(""));  // [curr, last, previous, ...]
    private int historyIdx;

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
                  g.texts("");
                  historyIdx=0;
              });
              g.addOnAttachListener(e -> {
                  g.setFocused(true);
              });
              g.addOnFocusChangedListener(e -> {
                  completeLs.setVisible(g.isFocused());
              });
              g.getText().addOnTextChangedListener(e -> {
                  if (historyIdx==0) {
                      histories.set(0, g.texts());  // update current text record.
                  }
                  String inputPrefix = tabCompleteInputPrefix();
                  // when text changed, let tabItemIdx = first matched-prefix item.
                  if (inputPrefix.isEmpty()) {
                      tabItemIdx = -1;
                  } else {
                      boolean matched = false;
                      for (int i = 0; i < completeLs.size(); i++) {
                          if (tabCompleteItem(i).startsWith(inputPrefix)) {
                              tabItemIdx = i; matched=true;
                              break;
                          }
                      }
                      if (!matched)
                          tabItemIdx=-1;
                  }
                  // update CompleteList item display status. selected/ same-prefix.
                  for (int i = 0;i < completeLs.size();i++) {
                      GuiText gText = completeLs.getGui(i);
                      boolean samepref = tabCompleteItem(i).startsWith(inputPrefix);
                      boolean selected = i==tabItemIdx;

                      gText.getTextColor().set(selected ? Colors.YELLOW : (samepref ? Colors.WHITE : Colors.GRAY));
                  }
              });
              g.addKeyboardListener(e -> {
                  if (g.isFocused() && e.getKeyState()) {
                      switch (e.getKey()) {
                          case GLFW.GLFW_KEY_ENTER:
                              String s = g.texts();
                              if (s.isEmpty()) return;
                              histories.add(0, "");  // required before 'clear texts'.
                              historyIdx=0;
                              g.texts("");
                              dispatchLine(s);
                              break;
                          case GLFW.GLFW_KEY_TAB:
                              if (completeLs.size() == 0) {
                                  updateTabComplete();
                                  tbInputBox.getText().performEvent(new GuiText.OnTextChangedEvent());
                              } else {
                                  String line = tbInputBox.texts();
                                  int start = tabCompleteStartIndex();
                                  if (tabItemIdx==-1 || tabCompleteInputPrefix().equals(tabCompleteItem(tabItemIdx))) {
                                      tabItemIdx++;
                                      tabItemIdx %= completeLs.size();
                                  }
                                  String comp = tabCompleteItem(tabItemIdx);
                                  tbInputBox.texts(line.substring(0, start) + comp + line.substring(tabCompleteEndIndex()));
                                  tbInputBox.setCursorPosition(start+comp.length());
                              }
                              break;
                          case GLFW.GLFW_KEY_UP:
                              switchHistory(1);
                              break;
                          case GLFW.GLFW_KEY_DOWN:
                              switchHistory(-1);
                              break;
                      }
                  }
              });
              g.addOnCursorPositionChangedListener(e -> {
                  int tabCompleteStartIdx = tabCompleteStartIndex();
                  if (lastTabCompleteStartIdx!=tabCompleteStartIdx) {
                      completeLs.removeAllGuis();
                      if (enableAutoCommandComplete) {
                          updateTabComplete();
                      }
                  }
                  lastTabCompleteStartIdx = tabCompleteStartIdx;
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
                  Vector2f p = tbInputBox.calculateTextPosition(tabCompleteStartIndex());
                  g.setX(p.x);
                  g.setY(tbInputBox.getY()-g.getHeight());
              });
          })
        );

    }

    private void updateTabComplete() {
//        LOGGER.info(" updateTabComplete");
        int cursorpos = tabCompleteStartIndex();//tbInputBox.getCursorPosition();
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

//        tabItemIdx = -1;
    }
    private int tabCompleteStartIndex() {
        String line = tbInputBox.texts();
        if (line.startsWith("/")) {
            int sp = line.lastIndexOf(' ', tbInputBox.getCursorPosition());
            return (sp==-1||sp==0)?1 : sp+1;
        } else {
            return tbInputBox.getCursorPosition();
        }
    }
    private int tabCompleteEndIndex() {
        String line = tbInputBox.texts();
        return CollectionUtils.orDefault(line.indexOf(' ', tabCompleteStartIndex()), line.length(), i -> i==-1);
    }
    private String tabCompleteInputPrefix() {
        int start = tabCompleteStartIndex();
        String prefix = tbInputBox.texts().substring(start, tabCompleteEndIndex());
        if (prefix.isEmpty())
            return "";
        if (start==1) // command
            prefix=new Identifier(prefix).toString();  // compare with Canonicals.
        return prefix;
    }
    private String tabCompleteItem(int i) {
        return ((GuiText)completeLs.getGui(i)).getText();
    }

    private void dispatchLine(String line) {
        if (line.startsWith("/")) {
            Command.dispatchCommand(Outskirts.getPlayer(), line);
        } else {
            printMessage("<"+ Outskirts.getPlayer().getName() +"> "+line);
        }
    }

    private void switchHistory(int s) {
        historyIdx = Maths.clamp(historyIdx+s, 0, histories.size()-1);
        tbInputBox.texts(histories.get(historyIdx));
        tbInputBox.setCursorPosition(tbInputBox.texts().length());
//        LOGGER.info(histories + "  "+historyIdx);
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
