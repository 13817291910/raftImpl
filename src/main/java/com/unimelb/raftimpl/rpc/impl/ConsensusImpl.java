package com.unimelb.raftimpl.rpc.impl;

import com.unimelb.raftimpl.rpc.AppendResult;
import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.LogEntry;
import com.unimelb.raftimpl.rpc.VoteResult;
import org.apache.thrift.TException;

import java.util.List;

public class ConsensusImpl implements Consensus.Iface {
    @Override
    public AppendResult handleAppendEntries(int term, int leaderId, long prevLogIndex, int prevLogTerm, List<LogEntry> entries, long leaderCommit) throws TException {
        return null;
    }

    @Override
    public VoteResult handleRequestVote(int term, int candidateId, long lastLogIndex, int lastLogTerm) throws TException {
        return null;
    }
}
