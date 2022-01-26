package com.mambo.rafiki.utils;

public class NumberUtils {

    public static int getAverage(int total, int count) {

        if (count == 0)
            count = 1;

        return total / count;
    }

}
