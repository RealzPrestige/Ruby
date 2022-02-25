package dev.zprestige.ruby.util;

import java.util.LinkedList;
import java.util.Queue;

public class GraphUtil {
    public Queue<Long> crystals;

    public GraphUtil() {
        this.crystals = new LinkedList<>();
    }

    public int getCount() {
        final long currentTimeMillis = System.currentTimeMillis();
        try {
            while (!this.crystals.isEmpty() && this.crystals.peek() < currentTimeMillis) {
                this.crystals.remove();
            }
        } catch (Exception ignored) {
        }
        return this.crystals.size();
    }

    public void addItem() {
        this.crystals.add(System.currentTimeMillis() + 1000L);
    }
}