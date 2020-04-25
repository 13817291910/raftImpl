package com.unimelb.raftimpl.protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteResult {

    int term;

    boolean voteGranted;


}
