package de.johni0702.minecraft.gui.utils;

public class EventRegistration<T> {
    public static <T> EventRegistration<T> create(Event<T> event, T callback) {
        return new EventRegistration<>(event, callback);
    }

    public static <T> EventRegistration<T> register(Event<T> event, T callback) {
        EventRegistration<T> registration = new EventRegistration<>(event, callback);
        registration.register();
        return registration;
    }

    private final Event<T> event;
    private final T listener;
    private boolean registered;

    private EventRegistration(Event<T> event, T listener) {
        this.event = event;
        this.listener = listener;
    }

    public void register() {
        if (registered) {
            throw new IllegalStateException();
        }

        event.register(listener);
        registered = true;
    }

    public void unregister() {
        if (!registered) {
            throw new IllegalStateException();
        }

        event.unregister(listener);
        registered = false;
    }
}
