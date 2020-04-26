package com.unimelb.raftimpl.rpc.impl;

import com.unimelb.raftimpl.rpc.AppendResult;
import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.LogEntry;
import com.unimelb.raftimpl.rpc.VoteResult;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConsensusImpl implements Consensus.Iface {

    private static final Logger log = LoggerFactory.getLogger(ConsensusImpl.class);

    @Override
    public AppendResult handleAppendEntries(int term, int leaderId, long prevLogIndex, int prevLogTerm, List<LogEntry> entries, long leaderCommit) throws TException {
        return null;
    }

    @Override
    public VoteResult handleRequestVote(int term, int candidateId, long lastLogIndex, int lastLogTerm) throws TException {
        log.info("method handleRequestVote succeed in invoking");
        return new VoteResult(term,true);
    }


}
