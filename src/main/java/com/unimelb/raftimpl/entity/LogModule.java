package com.unimelb.raftimpl.entity;

import com.unimelb.raftimpl.rpc.LogEntry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
    *@Description: LogModule
    *@Author: di kan
    *@Date: 2020/4/25
 **/

@Component
@Getter
@Setter
public class LogModule {

    private static final Logger log = LoggerFactory.getLogger(LogModule.class);

    private static final LogModule logModule = new LogModule();

    public static List<LogEntry> logEntryList = new LinkedList<>();

    private final Lock lock = new ReentrantLock(true);

    private LogModule(){};

    public static LogModule getInstance(){
        return logModule;
    }

    /**
        *@Description: The write method is used to help the master
        *               server store new log(text) from front end
        *@Param: [logEntry]
        *@return: void
        *@Author: di kan
        *@Date: 2020/4/26
    */
    public void write(LogEntry logEntry){
        try {
            lock.lock();
            if(logEntryList.size()==0){
                logEntry.setIdex(0);
                logEntryList.add(logEntry);
            }else{
                LogEntry lastEntry = logEntryList.get(logEntryList.size()-1);
                logEntry.setIdex(lastEntry.getIdex() + 1);
                logEntryList.add(logEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static LogEntry read(long index){
        return logEntryList.get((int)index);
    }

    public static LogEntry getPrev(LogEntry logEntry){
        if(logEntry.getIdex() == 0) return null;
        LogEntry prevLogEntry = read(logEntry.getIdex()-1);
//        return prevLogEntry==null?null:prevLogEntry;
        return prevLogEntry;
    }

    public static LogEntry getLastLogEntry() {

        return logEntryList.size()==0?null:logEntryList.get(logEntryList.size() - 1);
    }

    public boolean delete(long index){
        try {
            lock.lock();
            int length = logEntryList.size();
            for(int i = 0;i<length - index;i++){
                logEntryList.remove(logEntryList.size()-1);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

}
