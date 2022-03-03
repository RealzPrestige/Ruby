package dev.zprestige.ruby.eventbus.handler;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.eventbus.event.Event;

import java.lang.reflect.Method;

public abstract class Handler {

    protected final Object subscriber;

    public Handler(Method listener, Object subscriber) {
        listener.setAccessible(true);
        this.subscriber = subscriber;
    }

    public abstract void invoke(Event event);

    public boolean isSubscriber(Object object) {
        return this.subscriber.equals(object);
    }
}
