package com.unimelb.raftimpl.protocol;

import com.unimelb.raftimpl.entity.LogEntry;

public interface ConsensusProtocol {


    /**
        *@Description:
        *@Param: [term, leaderId, prevLogIndex, prevLogTerm, leaderCommit]
        *@return: void
        *@Author: di kan
        *@Date: 2020/4/24
     **/
    AppendResult appendEntries(int term, int leaderId,
                               long prevLogIndex, int prevLogTerm,
                               LogEntry[] entries, long leaderCommit);

    VoteResult requestVote(int term, int candidateId, long lastLogIndex, long lastLogTerm);

    
}
