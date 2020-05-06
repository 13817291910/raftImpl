package com.unimelb.raftimpl.rpc.impl;

import com.unimelb.raftimpl.entity.LogModule;
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
import sun.rmi.runtime.Log;

import java.util.List;

public class ConsensusImpl implements Consensus.Iface {

    private static final Logger log = LoggerFactory.getLogger(ConsensusImpl.class);

    @Autowired
    private Node node;

    @Override
    public AppendResult handleAppendEntries(int term, String leaderId, long prevLogIndex, int prevLogTerm, List<LogEntry> entries, long leaderCommit) throws TException {
        AppendResult result = new AppendResult();
        node.getLeader().setHost(leaderId);
        List<LogEntry> curLogEntries = LogModule.logEntryList;
        LogModule logModule = LogModule.getInstance();
        if (checkValidMsg(term, prevLogIndex, prevLogTerm, curLogEntries)) {
            if (entries == null) {
                node.setStartTime(System.currentTimeMillis());
                result.success = true;
                result.term = term;
                node.setNodeStatus(NodeStatus.FOLLOWER);
            } else {
                //todo: redirect client request to leader IP

                //todo?: 如果已经存在的日志条目和新的产生冲突（索引值相同但是任期号不同）,删除这一条和之后所有的 ;
                // 附加日志中尚未存在的任何新条目;
                LogEntry firstAppendEntry = entries.get(0);
                long delIndex = firstAppendEntry.getIdex();
                LogModule.getInstance().delete(delIndex);

                for(LogEntry entry: entries){
                    LogModule.getInstance().write(entry);
                }
                if(leaderCommit > node.getCommitIndex())
                    node.setCommitIndex(Math.min(leaderCommit, logModule.getLastLogEntry().getIdex()));
            }
        } else {
            result.success = false;
            result.term = Math.max(term, node.getCurrentTerm());
        }
        return result;
    }

    @Override
    public VoteResult handleRequestVote(int term, String candidateId, long lastLogIndex, int lastLogTerm) throws TException {
        VoteResult voteResult = new VoteResult();
        voteResult.setTerm(node.getCurrentTerm());
        voteResult.setVoteGranted(false);
        if (term > node.getCurrentTerm()) {
            if (lastLogIndex >= node.getCommitIndex()) {
                LogEntry temp = node.getLogModule().getLastLogEntry();
                if (lastLogTerm >= temp.getTerm()) {
                    node.setCurrentTerm(node.getCurrentTerm() + 1);
                    voteResult.setTerm(node.getCurrentTerm());
                    voteResult.setVoteGranted(true);
                    node.setVotedFor(candidateId);
                }
            }
        }
        return voteResult;
    }

    private boolean checkValidMsg(int leaderTerm, long prevLogIndex, int prevLogTerm, List<LogEntry> curLogEntries) {
        if(leaderTerm < node.getCurrentTerm()){
            return false;
        }
        else if(!prevLogMatch(curLogEntries, prevLogIndex, prevLogTerm)){
            return false;
        }
        return true;
    }

    private boolean prevLogMatch(List<LogEntry> curLogEntries, long prevLogIndex, int prevLogTerm){
        for(LogEntry curLogEntry: curLogEntries) {
            if (curLogEntry.getIdex() == prevLogIndex)
                return curLogEntry.getTerm() == prevLogTerm;
        }
        return false;
    }
}
