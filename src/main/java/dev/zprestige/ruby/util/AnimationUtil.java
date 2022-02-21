package dev.zprestige.ruby.util;

public class AnimationUtil {

    public static Integer increaseNumber(int input, int target, int delta) {
        if (input < target)
            return input + delta;
        return target;
    }

    public static Float increaseNumber(float input, float target, float delta) {
        if (input < target)
            return input + delta;
        return target;
    }

    public static Double increaseNumber(double input, double target, double delta) {
        if (input < target)
            return input + delta;
        return target;
    }

    public static Integer decreaseNumber(int input, int target, int delta) {
        if (input > target)
            return input - delta;
        return target;
    }

    public static Float decreaseNumber(float input, float target, float delta) {
        if (input > target)
            return input - delta;
        return target;
    }
}
