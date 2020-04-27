package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.entity.LogModule;
import com.unimelb.raftimpl.entity.Server;
import com.unimelb.raftimpl.entity.StateMachine;
import com.unimelb.raftimpl.enumerate.NodeStatus;
import com.unimelb.raftimpl.rpc.AppendResult;
import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.LogEntry;
import com.unimelb.raftimpl.rpc.VoteResult;
import com.unimelb.raftimpl.tool.ThreadPoolManager;
import com.unimelb.raftimpl.util.GetTTransport;
import com.unimelb.raftimpl.util.NumberGenerator;
import com.unimelb.raftimpl.util.TimeCounter;
import lombok.Getter;
import lombok.Setter;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Getter
@Setter
public class Node {

    private static final Logger log = LoggerFactory.getLogger(Node.class);
    /**
     * This is the persistent state on all servers
     */
    private volatile int currentTerm;

    private volatile String votedFor;

    //todo: log module

    private volatile long commitIndex;

    private volatile long lastApplied;

    private Map<Peer,Long> nextIndex;

    private Map<Peer,Long> matchIndex;

    private volatile NodeStatus nodeStatus;

    private Peer leader;

    private Peer self;

    private long heartBeat;

    @Autowired
    public Server server;

    private Set<Peer> peerSet;

    private StateMachine stateMachine;

    @Autowired
    private LogModule logModule;


    private long electiontimeout;
    private long startTime;
    private volatile int voteCount;

    public void startPeer() {
        electiontimeout = (long) NumberGenerator.generateNumber(200, 500);
        while (true) {
            if (nodeStatus == NodeStatus.FOLLOWER) {
                followerWork();
            } else if (nodeStatus == NodeStatus.CANDIDATE) {
                candidateWork();
            } else if (nodeStatus == NodeStatus.LEADER) {
                leaderWork();
            }
        }
    }

    private void followerWork() {
        startTime = System.currentTimeMillis();
        if (!TimeCounter.checkTimeout(startTime, heartBeat) && !TimeCounter.checkTimeout(startTime, electiontimeout)) {
            nodeStatus = NodeStatus.CANDIDATE;
        }
    }

    private void candidateWork() {
        voteCount = 0;
        long voteStartTime = System.currentTimeMillis();
        ConcurrentHashMap<String, Boolean> voteResultSet = new ConcurrentHashMap<>();
        currentTerm = currentTerm + 1;
        votedFor = self.getHost();
        List<LogEntry> logEntryList = LogModule.logEntryList;
        LogEntry logEntry = logEntryList.get(logEntryList.size() - 1);
        int lastLogTerm = logEntry.getTerm();
        long lastLogIndex = logEntry.getIdex();
        TTransport tTransport = null;
        for (Peer peer: peerSet) {
            try {
                new Thread(() -> {
                    int score = handleVoted(tTransport, peer, lastLogTerm, lastLogIndex);
                    voteCount += score;
                }).start();
            } catch (Exception e) {
                log.info(e.toString());
            } finally {
                tTransport.close();
            }
        }
        while (true) {
            if (TimeCounter.checkTimeout(voteStartTime, electiontimeout + 1000)) {
                if (voteCount >= (peerSet.size() / 2) + 1) {
                    nodeStatus = NodeStatus.LEADER;
                }
                break;
            }
        }
    }

    private int handleVoted(TTransport tTransport, Peer peer, int lastLogTerm, long lastLogIndex) {
        String host = peer.getHost();
        int port = peer.getPort();
        tTransport = GetTTransport.getTTransport(host, port, 1000);
        TProtocol protocol = new TBinaryProtocol(tTransport);
        Consensus.Client thriftClient = new Consensus.Client(protocol);

        VoteResult voteResult = thriftClient.handleRequestVote(currentTerm,self.getHost(),lastLogIndex,lastLogTerm);
        if (voteResult.voteGranted) {
            return 1;
        }
        return 0;
    }

    private void leaderWork() {
        long leaderTime = System.currentTimeMillis();
        while (true) {
            if (TimeCounter.checkTimeout(leaderTime, heartBeat)) {
                for (Peer peer: peerSet) {
                    new Thread(() -> {
                        TTransport tTransport = null;
                        try {
                            String host = peer.getHost();
                            int port = peer.getPort();
                            tTransport = GetTTransport.getTTransport(host, port, 1000);
                            TProtocol protocol = new TBinaryProtocol(tTransport);
                            Consensus.Client thriftClient = new Consensus.Client(protocol);
                            AppendResult appendResult = thriftClient.handleAppendEntries(currentTerm, host, , );
                        } catch (Exception e) {
                            log.info(e.toString());
                        } finally {
                            tTransport.close();
                        }
                    }).start();
                }
            }
        }
    }


    private synchronized void handleRequest(LogEntry logEntry, Consensus.Client client){
        logModule.write(logEntry);
        List<Future<Boolean>> futureList = new CopyOnWriteArrayList<>();
        List<Boolean> resultList = new CopyOnWriteArrayList<>();
        AtomicInteger successNum = new AtomicInteger(0);
        int count = 0;
        long commitIndex = 0;
        for(Peer peer:peerSet){
            //futureList.add(client.)
        }
        CountDownLatch countDownLatch = new CountDownLatch(futureList.size());
        //TOdo: 取future
        for(Future<Boolean> future:futureList){
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        resultList.add(future.get(2000, TimeUnit.MILLISECONDS));
                    } catch (InterruptedException  | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
                        resultList.add(false);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }

        try {
            countDownLatch.await(5000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for(Boolean result:resultList){
            if(result == true){
                successNum.incrementAndGet();
            }
        }

        if(successNum.get() >= (count/2)){
            commitIndex = logEntry.getIdex();
            stateMachine.apply(logEntry);
            lastApplied = commitIndex;
        }else{
            //TODO: 失败后是重试还是直接返回错误待讨论
        }
    }


    private Future<Boolean> replicateToSlave(Peer slave,LogEntry logEntry){
        return ThreadPoolManager.getInstance().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });
    }

}
