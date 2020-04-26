package com.unimelb.raftimpl.entity;

import com.unimelb.raftimpl.rpc.LogEntry;

public interface StateMachine {
    void apply(LogEntry logEntry);
}
