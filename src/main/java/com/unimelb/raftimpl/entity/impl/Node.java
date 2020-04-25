package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.entity.Server;
import com.unimelb.raftimpl.enumerate.NodeStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@Getter
@Setter
public class Node {
    /**
     * This is the persistent state on all servers
     */
    private volatile long currentTerm;

    private volatile Integer votedFor;

    //todo: log module

    private volatile long commitIndex;

    private volatile long lastApplied;

    private Map<Peer,Long> nextIndex;

    private Map<Peer,Long> matchIndex;

    private NodeStatus nodeStatus;

    private Peer leader;

    private Peer self;

    @Autowired
    public Server server;

    @PostConstruct
    public void init(){
        new Thread(()->server.start());
    }



}
