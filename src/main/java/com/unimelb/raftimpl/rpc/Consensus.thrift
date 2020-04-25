namespace java com.unimelb.raftimpl.rpc

struct AppendResult {
    1: i32 term;
    2: bool success;
}

struct VoteResult {
    1: i32 term;
    2: bool voteGranted;
}

struct LogEntry{
    1: i64 idex;
    2: i32 term;
    3: string text;
}



service Consensus {

    AppendResult handleAppendEntries(1:i32 term,2:i32 leaderId,3:i64 prevLogIndex,4:i32 prevLogTerm,5:list<LogEntry> entries,6:i64 leaderCommit);

    VoteResult handleRequestVote(1:i32 term,2:i32 candidateId,3:i64 lastLogIndex,4:i32 lastLogTerm);
}