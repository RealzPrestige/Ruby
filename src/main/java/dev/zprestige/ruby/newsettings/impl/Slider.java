package dev.zprestige.ruby.newsettings.impl;

import dev.zprestige.ruby.newsettings.Setting;

public class Slider extends Setting {
    protected float value, min, max;

    public Slider(String name, int min, int max){
        this.name = name;
        this.value = min;
        this.min = min;
        this.max = max;
    }

    public Slider(String name, float min, float max){
        this.name = name;
        this.value =  min;
        this.min = min;
        this.max = max;
    }

    public Slider(String name, double min, double max){
        this.name = name;
        this.value = (float)  min;
        this.min = (float) min;
        this.max = (float) max;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public long GetSlider() {
        return value;
    }

    public Slider parent(Parent parent){
        setHasParent(true);
        setParent(parent);
        return this;
    }
}