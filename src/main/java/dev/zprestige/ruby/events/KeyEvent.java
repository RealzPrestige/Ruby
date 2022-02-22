package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;

public class KeyEvent extends Event {
    public boolean info;
    public boolean pressed;

    public KeyEvent(boolean info, boolean pressed) {
        this.info = info;
        this.pressed = pressed;
    }
}


