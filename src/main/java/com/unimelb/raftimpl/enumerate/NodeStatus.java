package com.unimelb.raftimpl.enumerate;

public enum NodeStatus {

    FOLLOWER(0),CANDIDATE(1),LEADER(2);

    private int code;

    NodeStatus(int code){
        this.code = code;
    }


}
