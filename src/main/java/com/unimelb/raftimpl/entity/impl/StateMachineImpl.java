package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.dao.LogDao;
import com.unimelb.raftimpl.entity.Log;
import com.unimelb.raftimpl.entity.StateMachine;
import com.unimelb.raftimpl.rpc.LogEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Component
public class StateMachineImpl implements StateMachine {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private LogDao logDao;

    private static LogDao staticLogDao;

    @Override
    public synchronized void apply(LogEntry logEntry) {
        Log log = new Log();
        BeanUtils.copyProperties(logEntry, log);
        staticLogDao.insertLog((int)log.getIdex(), log.getTerm(), log.getText());
    }

    @Override
    public synchronized List<Log> read() {
        return staticLogDao.findAllLog();
    }

    @PostConstruct
    public void init(){
        staticLogDao = this.logDao;
    }
}
