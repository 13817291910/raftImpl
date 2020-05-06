package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.dao.LogDao;
import com.unimelb.raftimpl.entity.Log;
import com.unimelb.raftimpl.entity.StateMachine;
import com.unimelb.raftimpl.rpc.LogEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

public class StateMachineImpl implements StateMachine {

    @Autowired
    private LogDao logDao;

    @Override
    public synchronized void apply(LogEntry logEntry) {
        Log log = new Log();
        BeanUtils.copyProperties(logEntry, log);
        logDao.insertLog(log.getIndex(), log.getTerm(), log.getLog());
    }

    @Override
    public synchronized List<Log> read() {
        return logDao.findAllLog();
    }
}
