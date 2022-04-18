package outskirts.event;

import outskirts.util.CollectionUtils;
import outskirts.util.Validate;
import outskirts.util.concurrent.Scheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventBus {

    private static final Comparator<Handler> COMP_HANDLER_PRIORITY_DESC = Collections.reverseOrder(Comparator.comparingInt(Handler::priority));

    /**
     * Java1.4+ reflection. actually got a little better effective than just ASM call.
     * espetually in first several calls. they switch to uses ASM caller after '16' VM calls,
     * then get the effective equality with original java.
     */
    private static boolean USE_ASMINVOKER = false;

    private List<Handler> handlers;

    public EventBus() {
        this(ArrayList::new);
    }

    /**
     * @param handlerListFactory:
     *  for normal, just use default ArrayList for high-speed handler read/iteration.
     *  f needs dynamics register/unregister when handler is executing, can use CopyOnIterateArrayList..
     *  not use Suppler-Function-Factory because that though more convenient for init EventBus,
     *  but that'll make EventBus some little loose - misc field/setter. but inheritment
     */
    public EventBus(Supplier<List<Handler>> handlerListFactory) {
        this.handlers = handlerListFactory.get();
    }

    public List<Handler> handlers() {
        return Collections.unmodifiableList(handlers);
    }

    private void doPriorityHandlersSort(boolean batched) {
        if (batched) throw new UnsupportedOperationException();
        else {
            //handlers.sort(Comparator.reverseOrder()); // Arrays.sort - MargeSort alloc mem
            CollectionUtils.insertionSort(handlers, COMP_HANDLER_PRIORITY_DESC); // O(n) time O(0) space maintaing ordered-list, and keeping sort-stablility
        }
    }

    /**
     * the Mainly method of EventBus.
     * register a EventHandler as unit by a Lambda-Function(interface Consumer)
     * @param eventclass the target event-class which you want to subscribe
     * @param function the handler
     */
    public <E extends Event> Handler register(Class<E> eventclass, Consumer<E> function) {
        Handler handler = new Handler(eventclass, function); handler.ptrbus = this;
        handlers.add(handler);

        //f last 2 handler had different priority
        if (handlers.size() >= 2 && handlers.get(handlers.size()-2).priority != handlers.get(handlers.size()-1).priority) {
            doPriorityHandlersSort(false);
        }
        return handler;
    }
    // unsafe when using (Event e) -> { accessing externalstackvari }.
//    public final <E extends Event> Handler register(Consumer<E> function) {
//        // crazy powerful func..
//        Class eventclass = net.jodah.typetools.TypeResolver.resolveRawArguments(Consumer.class, function.getClass())[0];
//        return register(eventclass, function);
//    }

    // for supports static class/methods, that may have some problem about unnecessary complexity
    // e.g does static-listener using non-static-method..? instanced-listener using static-method..?
    // needs filter ..? like a handlers-impl-class's some handlers for diff EventBus
    /**
     * batched register EventHandlers in owner's each methods, which passed following several condition:
     * 1.method have @EventHandler annotation
     * 2.method is non-static
     * 3.method have only one param and the param is extends Event.class class
     * that method will be register. When event happen in its EventBus, that EventHandler(method) will be call
     *
     * EventHandler(method) support not public(you can public/private/protected/friendly)
     */
    public final void register(Object owner) {
        Validate.isTrue(!(owner instanceof Class), "Class(Static-Listener) is Unsupported.");
        for (Method method : owner.getClass().getDeclaredMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Validate.isTrue(!Modifier.isStatic(method.getModifiers()), "static method is unsupported. (method: %s)", method.getName());
                Validate.isTrue(method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0]),
                        "EventHandler method requires only-one <? extends Event> param. (method: %s)", method.getName());

                // EventHandler Info
                Class eventclass = method.getParameterTypes()[0];
                int priority = annotation.priority();
                boolean ignoreCancelled = annotation.ignoreCancelled();
                Scheduler scheduler = resolveScheduler(annotation);
                Consumer function;

                if (USE_ASMINVOKER) {
                    throw new UnsupportedOperationException("ASMINVOKER unsupported. the effective is not actually better.");
//                    ASMInvoker asmi = ASMInvoker.create(method);
//                    function = event -> {
//                        asmi.invoke(owner, event);
//                    };
                } else {
                    method.setAccessible(true);
                    function = event -> {
                        try {
                            method.invoke(owner, event);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            throw new RuntimeException("Failed to invoke this Method EventHandler.", ex.getCause());
                        }
                    };
                }

                register(eventclass, function)
                        .priority(priority)
                        .ignoreCancelled(ignoreCancelled)
                        .unregisterTag(owner)
                        .scheduler(scheduler);
            }
        }
    }
    private static Scheduler resolveScheduler(EventHandler annotation) {
        if (annotation.scheduler() == EventHandler.DEF_SCHEDULER)
            return null;
        try {
            return (Scheduler) annotation.scheduler().getMethod("getScheduler").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to resolve getScheduler().", ex);
        }
    }

    // not 'semmrtry' to registerAll()...
    /**
     * @return false when nothing was been unregistered
     */
    public boolean unregister(Object funcOrTag) {
        return handlers.removeIf(h -> h.function == funcOrTag || h.unregisterTag == funcOrTag);
    }

    /**
     * perform all EventHandler(s) registered on this EventBus that typeof the Event
     * @return if true, the event has be cancelled. (only possible return true when the Event implements Cancellable)
     * // todo: reaally true==cancelled.?  may false == cancelled better.?
     */
    public boolean post(Event event) {
        Class eventclass = event.getClass();

        for (Handler handler : handlers) {
            if (handler.eventclass == eventclass || handler.eventclass.isAssignableFrom(eventclass)) {
                handler.invoke(event);
            }
        }

        return Cancellable.isCancelled(event);
    }


    public static final class Handler {

        private final Consumer function; // the handler execution function. Consumer<? extends Event>
        private final Class eventclass;
        private int priority = EventHandler.DEF_PRIORITY; // bigger number, higher priority
        private boolean ignoreCancelled = EventHandler.DEF_IGNORE_CANCELLED; // if true, the handler'll receives cancelled events.
        private EventBus ptrbus;

        private Scheduler scheduler = null; // if non-null, the handler'll be perform postpone/inside the scheduler thread.
        private Object unregisterTag = null;  // only for unregister search. you can unregister this handler by using the tag object to calls unregister() method

        private Handler(Class eventclass, Consumer function) {
            this.eventclass = Objects.requireNonNull(eventclass);
            this.function = Objects.requireNonNull(function);
        }

        private void invoke(Event event) {
            if (!ignoreCancelled && Cancellable.isCancelled(event))
                return;
            if (scheduler == null || scheduler.inSchedulerThread())
            {
                doInvoke(event);
            }
            else
            {
                scheduler.addScheduledTask(() -> doInvoke(event));
            }
        }

        private void doInvoke(Event event) {
            try
            {
                function.accept(event);
            }
            catch (Throwable t)
            {
                throw new RuntimeException("An exception occurred on EventHandler execution.", t);
            }
        }

        public int priority() {
            return priority;
        }

        public Handler priority(int priority) {
            if (this.priority != priority) {
                this.priority = priority;
                ptrbus.doPriorityHandlersSort(false);
            }
            return this;
        }
        public Handler ignoreCancelled(boolean ignoreCancelled) {
            this.ignoreCancelled = ignoreCancelled;
            return this;
        }
        public Handler unregisterTag(Object unregisterTag) {
            this.unregisterTag = unregisterTag;
            return this;
        }
        public Handler scheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }
    }
}
