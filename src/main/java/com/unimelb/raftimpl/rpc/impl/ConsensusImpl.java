package com.unimelb.raftimpl.rpc.impl;

import com.unimelb.raftimpl.entity.LogModule;
import com.unimelb.raftimpl.entity.impl.Node;
import com.unimelb.raftimpl.entity.impl.Peer;
import com.unimelb.raftimpl.enumerate.NodeStatus;
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
    public AppendResult handleAppendEntries(int term, String leaderId, long prevLogIndex, int prevLogTerm, List<LogEntry> entries, long leaderCommit) throws TException {
        AppendResult result = new AppendResult();
        List<LogEntry> curLogEntries = LogModule.logEntryList;
        LogModule logModule = LogModule.getInstance();
        if (checkValidMsg(term, prevLogIndex, prevLogTerm, curLogEntries)) {
            String[] leaderInfo = leaderId.split(":");
            String leaderHost = leaderInfo[0].trim();
            int leaderPort = Integer.getInteger(leaderInfo[1].trim());
            Node.leader = new Peer(leaderHost, leaderPort);
            if (entries == null) {
                //todo: entries 设为 null，一起测了
                Node.startTime = System.currentTimeMillis();
                result.success = true;
                result.term = term;
                Node.nodeStatus = NodeStatus.FOLLOWER;
            } else {
                //todo: redirect client request to leader IP

                LogEntry firstAppendEntry = entries.get(0);
                long delIndex = firstAppendEntry.getIdex();
                logModule.delete(delIndex);

                //todo: addTransaction一下
                for(LogEntry entry: entries){
                    logModule.write(entry);
                }
                if(leaderCommit > Node.commitIndex){
                    Node.commitIndex = Math.min(leaderCommit, logModule.getLastLogEntry().getIdex());
                }
                if(Node.commitIndex > Node.lastApplied){
                    curLogEntries = LogModule.logEntryList;
                    try{
                        for(int index = (int)Node.lastApplied + 1; index <= Node.commitIndex; index++){
                            Node.stateMachine.apply(curLogEntries.get(index));
                            Node.lastApplied++;
                        }
                        Node.lastApplied = Node.commitIndex;
                    } catch (Exception e) {
                        log.info(e.toString());
                    }
                }
                //todo: 结束transaction
            }
        } else {
            result.success = false;
            result.term = Math.max(term, Node.currentTerm);
        }
        return result;
    }

    @Override
    public VoteResult handleRequestVote(int term, String candidateId, long lastLogIndex, int lastLogTerm) throws TException {
        VoteResult voteResult = new VoteResult();
        voteResult.setTerm(Node.currentTerm);
        voteResult.setVoteGranted(false);
        if (term > Node.currentTerm) {
            if (lastLogIndex >= Node.commitIndex) {
                LogEntry temp = LogModule.getLastLogEntry();
                int tempTerm;
                if(temp != null){
                    tempTerm = temp.getTerm();
                }else{
                    tempTerm = 0;
                }
                if (lastLogTerm >= tempTerm) {
                    Node.currentTerm = Node.currentTerm + 1;
                    voteResult.setTerm(Node.currentTerm);
                    voteResult.setVoteGranted(true);
                    Node.votedFor = candidateId;
                }
            }
        }
        return voteResult;
    }

    private boolean checkValidMsg(int leaderTerm, long prevLogIndex, int prevLogTerm, List<LogEntry> curLogEntries) {
        if(leaderTerm < Node.currentTerm){
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
