package com.mambo.rafiki.utils;

public class ParedViewUtils {

    public static String doubleTrans1(double num) {
        if (num % 1.0 == 0) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }
}
