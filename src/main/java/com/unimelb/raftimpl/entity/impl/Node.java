package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.entity.LogModule;
import com.unimelb.raftimpl.entity.Server;
import com.unimelb.raftimpl.entity.StateMachine;
import com.unimelb.raftimpl.enumerate.NodeStatus;
import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.LogEntry;
import com.unimelb.raftimpl.tool.ThreadPoolManager;
import lombok.Getter;
import lombok.Setter;
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

    private volatile Integer votedFor;

    //todo: log module

    private volatile long commitIndex;

    private volatile long lastApplied;

    private Map<Peer,Long> nextIndexes;

    private Map<Peer,Long> matchIndexes;

    private NodeStatus nodeStatus;

    private Peer leader;

    private Peer self;

    @Autowired
    public Server server;

    private Set<Peer> peerSet;

    private StateMachine stateMachine;

    @Autowired
    private LogModule logModule;

    /**
        *@Description: The master server use this method to transfer the front end dialogues
         *              to logEntry, and replicate them to the slave servers.
        *@Param: [logEntry, client]
        *@return: void
        *@Author: di kan
        *@Date: 2020/4/27
     */
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
        }else{
            //TODO: 失败后是重试还是直接返回错误待讨论
        }

    }

    private Future<Boolean> replicateToSlave(Peer slave,LogEntry logEntry){
        return ThreadPoolManager.getInstance().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //Compute all the parameters that rpc method handleAppendEntries needs
                int term = currentTerm;
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

                LogEntry prevLogEntry = logModule.getPrev(logEntry);
                //TODO: 初始化的时候prev是空，这里的逻辑还没做，以及接收rpc的结果
                long prevLogIndex = prevLogEntry.getIdex();
                int prevLogTerm = prevLogEntry.getTerm();

                return true;
            }
        });
    }

}
