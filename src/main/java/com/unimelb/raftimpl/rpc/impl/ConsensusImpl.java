package com.unimelb.raftimpl.rpc.impl;

import com.unimelb.raftimpl.entity.impl.Node;
import com.unimelb.raftimpl.enumerate.NodeStatus;
import com.unimelb.raftimpl.rpc.AppendResult;
import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.LogEntry;
import com.unimelb.raftimpl.rpc.VoteResult;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConsensusImpl implements Consensus.Iface {

    private static final Logger log = LoggerFactory.getLogger(ConsensusImpl.class);

    @Autowired
    private Node node;

    @Override
    public AppendResult handleAppendEntries(int term, int leaderId, long prevLogIndex, int prevLogTerm, List<LogEntry> entries, long leaderCommit) throws TException {
        AppendResult result = new AppendResult();
        if (checkValidMsg()) {
            if (entries == null) {
                node.setStartTime(System.currentTimeMillis());
                result.success = true;
                result.term = term;
                node.setNodeStatus(NodeStatus.FOLLOWER);
            } else {
                // todo
            }
        } else {
            node.setNodeStatus(NodeStatus.FOLLOWER);
            result.success = false;
            result.term = (int) Math.max(term, node.getCurrentTerm());
        }
        return result;
    }

    @Override
    public VoteResult handleRequestVote(int term, int candidateId, long lastLogIndex, int lastLogTerm) throws TException {
        log.info("method handleRequestVote succeed in invoking");
        return new VoteResult(term,true);
    }

    private boolean checkValidMsg() {
        //todo
        return true;
    }
}
