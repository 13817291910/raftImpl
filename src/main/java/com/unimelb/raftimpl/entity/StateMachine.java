package com.unimelb.raftimpl.entity;

import com.unimelb.raftimpl.rpc.LogEntry;

import java.util.List;

public interface StateMachine {
    void apply(LogEntry logEntry);
    List<Log> read();
}
