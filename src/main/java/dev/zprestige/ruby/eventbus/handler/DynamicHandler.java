package dev.zprestige.ruby.eventbus.handler;


import dev.zprestige.ruby.eventbus.event.Event;

public interface DynamicHandler {

    void invoke(Event event);
}
