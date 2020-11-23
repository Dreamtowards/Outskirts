package outskirts.event;

import outskirts.util.CopyOnIterateArrayList;

public final class Events {

    public static final EventBus EVENT_BUS = new EventBus(CopyOnIterateArrayList::new);

}
