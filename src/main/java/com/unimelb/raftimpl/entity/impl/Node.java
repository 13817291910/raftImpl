package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.entity.CommonMsg;
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
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
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

    private Map<Peer,Long> nextIndexes;

    private Map<Peer,Long> matchIndexes;

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
    private volatile long startTime;
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

        VoteResult voteResult = null;
        try {
            voteResult = thriftClient.handleRequestVote(currentTerm,self.getHost(),lastLogIndex,lastLogTerm);
        } catch (TException e) {
            e.printStackTrace();
        }
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
                            //TOdo:  reconsider the appendEntries parameter
                            //  AppendResult appendResult = thriftClient.handleAppendEntries(currentTerm, host, , );
                            //if (!appendResult.success) {
                            //    nodeStatus = NodeStatus.FOLLOWER;
                            //}
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

    /**
        *@Description: take the latest log and replicate this log and logs after
         * the next index together to the slave server
        *@Param: [logEntry, client]
        *@return: com.unimelb.raftimpl.entity.CommonMsg
        *@Author: di kan
        *@Date: 2020/4/30
     */
    public synchronized CommonMsg handleRequest(LogEntry logEntry, Consensus.Client client){
        logEntry.term = currentTerm;
        logModule.write(logEntry);
        log.info("write logModule success, logEntry info : {}, log index : {}", logEntry, logEntry.getIdex());
        List<Future<Boolean>> futureList = new CopyOnWriteArrayList<>();
        List<Boolean> resultList = new CopyOnWriteArrayList<>();
        AtomicInteger successNum = new AtomicInteger(0);
        int count = 0;
        commitIndex = 0;
        for(Peer peer:peerSet){
            futureList.add(replicateToSlave(peer,logEntry,client));
        }
        CountDownLatch countDownLatch = new CountDownLatch(futureList.size());
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

        /*
            *  Do as the paper said,If there exists an N such that N > commitIndex,
            *  a majority of matchIndex[i] ≥ N, and log[N].term == currentTerm:
            *  set commitIndex = N
        * */
        List<Long> matchedIndexList = new ArrayList<>(matchIndexes.values());
        long median = 0;
        int medianIndex = 0;
        if(matchedIndexList.size() >= 2){
            Collections.sort(matchedIndexList);
            medianIndex = matchedIndexList.size() / 2;
        }
        median = matchedIndexList.get(medianIndex);
        if(median > commitIndex){
            LogEntry entry = logModule.read(median);
            if(entry != null && entry.getTerm() == currentTerm){
                commitIndex = median;
            }
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
            log.info("success apply local state machine,  logEntry info : {}", logEntry);
            return CommonMsg.builder().code(200)
                    .msg("success apply to the state machine")
                    .success(true)
                    .build();
        }else{
            //if it fails to replicate to half or more server,return this failure to the client side
            logModule.delete(logEntry.getIdex());
            return CommonMsg.builder().code(100)
                    .msg("more than half of severs fails to replicate this log")
                    .success(false)
                    .build();
        }

    }


    private Future<Boolean> replicateToSlave(Peer slave,LogEntry logEntry,Consensus.Client client){
        return ThreadPoolManager.getInstance().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //Compute all the parameters that rpc method handleAppendEntries needs
                int term = currentTerm;
                String leaderId = self.getHost();
                long leaderCommit = commitIndex;
                long nextIndex = nextIndexes.get(slave);
                LinkedList<LogEntry> entries = new LinkedList<>();
                if(logEntry.getIdex() >= nextIndex){
                    for(long i = nextIndex; i <= logEntry.getIdex();i++){
                        LogEntry curEntry = logModule.read(i);
                        if(curEntry!=null) entries.add(curEntry);
                    }
                }else{
                    entries.add(logEntry);
                }
                LogEntry prevLogEntry = logModule.getPrev(entries.getFirst());
                //TODO: 初始化的时候prev是空，这里的逻辑还没做，以及接收rpc的结果
                long prevLogIndex = prevLogEntry.getIdex();
                int prevLogTerm = prevLogEntry.getTerm();
                AppendResult appendResult = client.handleAppendEntries(term,leaderId,
                                                            prevLogIndex,prevLogTerm,
                                                            entries,leaderCommit);
                if (appendResult.isSuccess()){
                    return true;
                }else{
                    return false;
                }
            }
        });
    }

}
