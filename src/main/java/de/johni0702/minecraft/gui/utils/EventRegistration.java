package de.johni0702.minecraft.gui.utils;

import net.fabricmc.fabric.api.event.Event;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    @SuppressWarnings("unchecked")
    public void unregister() {
        if (!registered) {
            throw new IllegalStateException();
        }

        try {
            Field field = event.getClass().getDeclaredField("handlers");
            field.setAccessible(true);
            T[] handlers = (T[]) field.get(event);
            T[] copy = (T[]) Array.newInstance(handlers.getClass().getComponentType(), handlers.length - 1);
            for (int from = 0, to = 0; from < handlers.length; from++) {
                if (handlers[from] == listener) {
                    continue;
                }
                copy[to++] = handlers[from];
            }
            if (copy.length == 0) {
                copy = null;
            }
            field.set(event, copy);
            Method method = event.getClass().getDeclaredMethod("update");
            method.setAccessible(true);
            method.invoke(event);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        registered = false;
    }
}
