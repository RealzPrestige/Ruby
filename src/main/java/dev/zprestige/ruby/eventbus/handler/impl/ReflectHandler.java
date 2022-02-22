package dev.zprestige.ruby.eventbus.handler.impl;


import dev.zprestige.ruby.eventbus.event.Event;
import dev.zprestige.ruby.eventbus.handler.Handler;

import java.lang.reflect.Method;

public class ReflectHandler extends Handler {

    protected final Method listener;

    public ReflectHandler(Method listener, Object subscriber) {
        super(listener, subscriber);
        this.listener = listener;
    }

    @Override
    public void invoke(Event event) {
        try {
            listener.invoke(subscriber, event);
        } catch (Exception ignored) {
        }
    }
}
