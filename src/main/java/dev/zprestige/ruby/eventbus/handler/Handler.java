package dev.zprestige.ruby.eventbus.handler;

import dev.zprestige.ruby.eventbus.annotation.Priority;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.eventbus.event.Event;

import java.lang.reflect.Method;

public abstract class Handler {

    protected final Object subscriber;
    protected final Priority priority;

    public Handler(Method listener, Object subscriber) {
        listener.setAccessible(true);
        final RegisterListener annotation = listener.getAnnotation(RegisterListener.class);
        this.priority = annotation.priority();
        this.subscriber = subscriber;
    }

    public abstract void invoke(Event event);

    public Priority getPriority() {
        return this.priority;
    }

    public boolean isSubscriber(Object object) {
        return this.subscriber.equals(object);
    }
}
