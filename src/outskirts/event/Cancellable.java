package outskirts.event;

public interface Cancellable {

    default boolean isCancelled() {
        return ((Event)this).cancelled;
    }

    default void setCancelled(boolean cancel) {
        ((Event)this).cancelled = cancel;
    }

    static boolean isCancelled(Event event) {
        return event instanceof Cancellable && event.cancelled; // ((Cancellable)event).isCancelled();
    }
}
