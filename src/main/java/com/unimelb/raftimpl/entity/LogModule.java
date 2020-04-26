package com.unimelb.raftimpl.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
    *@Description: LogModule
    *@Author: di kan
    *@Date: 2020/4/25
 **/
public class LogModule {

    private static final Logger log = LoggerFactory.getLogger(LogModule.class);

    private static final LogModule logModule = new LogModule();

    private static Map<Long,LogEntry> logEntryMap = new LinkedHashMap<>();

    private LogModule(){};

    public static LogModule getInstance(){
        return logModule;
    }

    

}
