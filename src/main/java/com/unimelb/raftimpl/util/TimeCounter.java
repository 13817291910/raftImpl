package com.unimelb.raftimpl.util;

public class TimeCounter {

    public static boolean checkTimeout(long startTime, long timeout) {
        long currentTime = System.currentTimeMillis();
        long elapse = currentTime - startTime;
        return elapse >= timeout;
    }

}
