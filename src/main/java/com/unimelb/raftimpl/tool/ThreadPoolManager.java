package com.unimelb.raftimpl.tool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private static final ThreadPoolManager manager = new ThreadPoolManager();
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_SIZE = 100;
    private static final int CORE_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_TIME = 1000*60;
    private ThreadPoolExecutor executor;
    private ThreadPoolManager(){
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(QUEUE_SIZE));
    }

    public static ThreadPoolExecutor getInstance(){
        return manager.executor;
    }
}
