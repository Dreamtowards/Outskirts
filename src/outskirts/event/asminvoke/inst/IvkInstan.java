package outskirts.event.asminvoke.inst;

import outskirts.event.asminvoke.ASMInvoker;
import outskirts.event.asminvoke.examp.AnExampEHandlerClass;
import outskirts.event.gui.GuiEvent;

public class IvkInstan extends ASMInvoker {

    @Override
    public void invoke(Object owner, Object param) {
        ((AnExampEHandlerClass)owner).anHander((GuiEvent)param);
    }

}
