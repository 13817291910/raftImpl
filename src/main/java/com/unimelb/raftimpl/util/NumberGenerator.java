package com.unimelb.raftimpl.util;

import java.util.Random;
public class NumberGenerator {

    public static double generateNumber(int min, int max) {
        Random generator = new Random();
        int number = (generator.nextInt((max - min) + 1)) + min;
        return number;
    }
}
