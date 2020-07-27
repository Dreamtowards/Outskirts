package outskirts.event.asminvoke.examp;

import outskirts.event.gui.GuiEvent;

public class AnExampEHandlerClass {

    public void anHander(GuiEvent event) {

        System.out.println("anHandler: "+event.getClass());
    }

}
