package com.unimelb;

import com.unimelb.raftimpl.util.TimeCounter;

public class Test {
    public static void main(String[] args) {
        int timeout = 5000;
        long current = System.currentTimeMillis();
        while (true) {
            if (TimeCounter.checkTimeout(current, timeout)) {
                System.out.println("timeout");
                current = System.currentTimeMillis();
            }

        }

    }
}
