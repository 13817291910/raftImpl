package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.entity.StateMachine;
import com.unimelb.raftimpl.rpc.LogEntry;

import java.util.LinkedList;
import java.util.List;

public class StateMachineImpl implements StateMachine {
    @Override
    public void apply(LogEntry logEntry) {

    }

    @Override
    public List<LogEntry> read() {
        List<LogEntry> logEntries = new LinkedList<>();
        return logEntries;
    }
}
