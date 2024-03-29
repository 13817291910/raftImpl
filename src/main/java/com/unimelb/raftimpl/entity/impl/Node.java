package com.unimelb.raftimpl.entity.impl;

import com.unimelb.raftimpl.config.PeerConfig;
import com.unimelb.raftimpl.dao.LogDao;
import com.unimelb.raftimpl.entity.*;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Null;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Getter
@Setter
public class Node {
    public final static List<LogEntry> heartBeatMessage = null;

    private static final Logger log = LoggerFactory.getLogger(Node.class);
    /**
     * This is the persistent state on all servers
     */
    public static volatile int currentTerm;

    public static volatile String votedFor;

    //todo: log module

    public static volatile long commitIndex;

    public static volatile long lastApplied;

    private Map<Peer,Long> nextIndexes;

    private Map<Peer,Long> matchIndexes;

    public static volatile NodeStatus nodeStatus;

    public static Peer leader;

    public static Peer self;

    //@Value("${self.heartBeat}")
    public static int heartBeat;

    @Autowired
    public Server server;

    @Autowired
    private PeerConfig peerConfig;

    private static Set<Peer> peerSet;

    public static StateMachine stateMachine = new StateMachineImpl();

    @Autowired
    private LogModule logModule;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private LogDao logDao;

    public static long electiontimeout;
    public static volatile long startTime;
    public static volatile int voteCount;

    //THis is the lock that is used for synchronizing the retry of replication(decrease next index by 1)
    // when the first attempt fails
    public static final Lock lock = new ReentrantLock();

    private boolean leaderFirstInitialize;

    @PostConstruct
    public void startPeer() {
        log.info("startPeer is starting");
        self = new Peer(peerConfig.getSelfIp(),peerConfig.getSelfPort());
        electiontimeout = (long) NumberGenerator.generateNumber(6000, 9000);
        //electiontimeout = 3500;
        nodeStatus = NodeStatus.FOLLOWER;
        String[] peersIp = peerConfig.getPeersIp();
        int[] peersPort = peerConfig.getPeersPort();
        heartBeat = peerConfig.getHeartBeat();
        nextIndexes = new HashMap<>();
        matchIndexes = new HashMap<>();
        peerSet = new HashSet<>();
        // load the data in state machine to the memory
        List<Log> logs = logDao.findAllLog();
        if(!logs.isEmpty()){
            for(Log log:logs){
                LogEntry logEntry = new LogEntry();
                BeanUtils.copyProperties(log,logEntry);
                LogModule.logEntryList.add(logEntry);
            }
        }
        long lastIndex =  lastApplied = commitIndex = (logs.isEmpty()?-1:logs.get(logs.size()-1).getIdex());
        for(int i=0;i<peersIp.length;i++){
            Peer curPeer = new Peer(peersIp[i],peersPort[i]);
            peerSet.add(curPeer);
            matchIndexes.put(curPeer,-1L);
            nextIndexes.put(curPeer,lastIndex + 1);
        }
        log.info("the connected peers are {}",peerSet.toString());
        currentTerm = 0;
        new Thread(()->{
            log.info("get into loop");
            log.info("election time out is {}", electiontimeout);
            startTime = System.currentTimeMillis();
            while (true) {
                if (nodeStatus == NodeStatus.FOLLOWER) {
                    followerWork();
                } else if (nodeStatus == NodeStatus.CANDIDATE) {
                    candidateWork();
                } else if (nodeStatus == NodeStatus.LEADER) {
                    leaderWork();
                }
            }
        }).start();
    }

    private void followerWork() {
        if (TimeCounter.checkTimeout(startTime, heartBeat) && TimeCounter.checkTimeout(startTime, electiontimeout)) {
            log.info("become candidate");
            nodeStatus = NodeStatus.CANDIDATE;
        }

    }

    private void candidateWork() {
        voteCount = 0;
        currentTerm = currentTerm + 1;
        votedFor = peerConfig.getSelfIp();
        voteCount = voteCount + 1;
        List<LogEntry> logEntryList = LogModule.logEntryList;
        int lastLogTerm;
        long lastLogIndex;
        if(logEntryList.size()==0){
            lastLogTerm = 0;
            lastLogIndex = 0;
        }else{
            LogEntry logEntry = logModule.getLastLogEntry();
            lastLogTerm = logEntry.getTerm();
            lastLogIndex = logEntry.getIdex();
        }
        CountDownLatch latch = new CountDownLatch(peerSet.size());
        for (Peer peer: peerSet) {
            TTransport tTransport;
            try {
                tTransport = GetTTransport.getTTransport(peer.getHost(),peer.getPort(),1000);
                //if(tTransport!=null){
                    new Thread(() -> {
                        try {
                            int score = handleVoted(tTransport, lastLogTerm, lastLogIndex);

                            log.info("score is {}", score);
                            voteCount = voteCount + score;


                            log.info("voted count is {}", voteCount);
                            //latch.countDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                            tTransport.close();
                        }
                    }).start();
                //}
            } catch (Exception e) {
                log.info(e.toString());
            } finally {
                latch.countDown();
            }
        }
        //try {
        //    Thread.sleep(3000);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        try {
            latch.await();
            long voteStartTime = System.currentTimeMillis();

            while (true) {
                if (TimeCounter.checkTimeout(voteStartTime, electiontimeout)) {
                    log.info("status is {} {}:{} vote count is {} current term is {}",nodeStatus, peerConfig.getSelfIp(),peerConfig.getSelfPort(),voteCount, currentTerm);
                    int totalPeer = peerSet.size() + 1;
                    if (voteCount > Math.floor(totalPeer / 2.0)) {
                        if (nodeStatus == NodeStatus.CANDIDATE)
                            nodeStatus = NodeStatus.LEADER;
                        else {
                            nodeStatus = NodeStatus.FOLLOWER;
                            break;
                        }
                        leader = self;
                        log.info("{}:{} becomes leader",peerConfig.getSelfIp(),peerConfig.getSelfPort());
                    } else {
                        nodeStatus = NodeStatus.FOLLOWER;
                        startTime = System.currentTimeMillis();
                    }
                    break;
                }
            }
        } catch (InterruptedException e) {
            nodeStatus = NodeStatus.FOLLOWER;
            e.printStackTrace();
        }
    }

    private int handleVoted(TTransport tTransport, int lastLogTerm, long lastLogIndex) {
        TProtocol protocol = new TBinaryProtocol(tTransport);
        Consensus.Client thriftClient = new Consensus.Client(protocol);
        VoteResult voteResult;
        try {
            log.info("current term is {}", currentTerm);
            voteResult = thriftClient.handleRequestVote(currentTerm,peerConfig.getSelfIp(),lastLogIndex,lastLogTerm);
        } catch (TException e) {
            e.printStackTrace();
            log.error("fail to send the request vote rpc request",e);
            return 0;
        }
        if (voteResult.voteGranted) {
            return 1;
        } else {
            if (voteResult.getTerm() > currentTerm) {
                currentTerm = voteResult.getTerm();
            }
        }
        return 0;
    }

    private void leaderWork() {
        long leaderTime = System.currentTimeMillis();
        if(!leaderFirstInitialize) {
            try {
                List<Log> logs = logDao.findAllLog();
//                if (logs != null) {
//                    for (Log log : logs) {
//                        LogEntry logEntry = new LogEntry();
//                        BeanUtils.copyProperties(log, logEntry);
//                        LogModule.logEntryList.add(logEntry);
//                    }
//                }
                long lastIndex = lastApplied = commitIndex = (logs.isEmpty() ? -1 : logs.get(logs.size() - 1).getIdex());
                for (Peer curPeer : peerSet) {
                    matchIndexes.put(curPeer, -1L);
                    nextIndexes.put(curPeer, lastIndex + 1);
                }
                leaderFirstInitialize = true;
            } catch (Exception e) {
                log.error("the leader fails to load statemachine");
                e.printStackTrace();
            }
        }
        log.info("the connected peers are {}",peerSet.toString());
        while (true) {
            if (TimeCounter.checkTimeout(leaderTime, heartBeat)) {

//                LogEntry lastOne = logModule.getLastLogEntry();
//                if (lastOne == null) {
//                    lastLogIndex = 0;
//                    lastTerm = 0;
//                } else {
//                    lastLogIndex = lastOne.getIdex();
//                    lastTerm = lastOne.getTerm();
//                }
                for (Peer peer: peerSet) {
                    long lastLogIndex;
                    int lastTerm;
                    lastLogIndex = 0;
                    lastTerm = 0;
                    ThreadPoolManager.getInstance().execute(()->{
                        TTransport tTransport = null;
                        try {
                            String host = peer.getHost();
                            int port = peer.getPort();
                            tTransport = GetTTransport.getTTransport(host, port, 2000);
                            TProtocol protocol = new TBinaryProtocol(tTransport);
                            Consensus.Client thriftClient = new Consensus.Client(protocol);
                            AppendResult appendResult = thriftClient.handleAppendEntries(currentTerm, leader.toString(), lastLogIndex, lastTerm, heartBeatMessage, commitIndex);
                            if (!appendResult.success) {
                                if (currentTerm < appendResult.getTerm())
                                    currentTerm = appendResult.getTerm();
                                nodeStatus = NodeStatus.FOLLOWER;
                            }
                        } catch (Exception e) {
                            log.info(e.toString());
                        } finally {
                            tTransport.close();
                        }
                    });
                }
                //leaderTime = System.currentTimeMillis();
                break;
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
    public synchronized CommonMsg handleRequest(LogEntry logEntry){
        logEntry.term = currentTerm;
        logModule.write(logEntry);
        log.info("write logModule success, logEntry info : {}, log index : {}", logEntry, logEntry.getIdex());
        List<Future<Boolean>> futureList = new CopyOnWriteArrayList<>();
        List<Boolean> resultList = new CopyOnWriteArrayList<>();
        AtomicInteger successNum = new AtomicInteger(0);
        int count = peerSet.size();
        //No log is committed yet, since log index begins at 0, thus we initialize it as -1
        //initialized in startPeer
//        commitIndex = -1;
        for(Peer peer:peerSet){
            TTransport tTransport = GetTTransport.getTTransport(peer.getHost(),peer.getPort(),3000);
            TProtocol protocol = new TBinaryProtocol(tTransport);
            Consensus.Client client = new Consensus.Client(protocol);
            futureList.add(replicateToSlave(peer,logEntry,client));
        }
        CountDownLatch countDownLatch = new CountDownLatch(futureList.size());
        for(Future<Boolean> future:futureList){
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        resultList.add(future.get(3000, TimeUnit.MILLISECONDS));
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
            log.error("the replicate step costs more than 5000 ms");
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
                String leaderId = self.toString();
                long leaderCommit = commitIndex;
                List<LogEntry> entries = getReplicateEntries(slave,logEntry);
                LogEntry prevLogEntry = logModule.getPrev(entries.get(0));
                long prevLogIndex;
                int prevLogTerm;
                prevLogIndex = getPrevLogIndex(prevLogEntry);
                prevLogTerm = getPrevLogTerm(prevLogEntry);
                log.info("handleAppendEntries parameter: term:{},leaderId:{},prevLogIndex:{},prevLogTerm:{},entries:{},leaderCommit:{}"
                        ,term,leaderId,prevLogTerm,prevLogIndex,entries,leaderCommit);
                AppendResult appendResult = client.handleAppendEntries(term,leaderId,
                                                            prevLogIndex,prevLogTerm,
                                                            entries,leaderCommit);
                long lastReplicatedIndex = entries.get(entries.size()-1).idex;
                if (appendResult.isSuccess()){
                    nextIndexes.put(slave,lastReplicatedIndex + 1);
                    matchIndexes.put(slave,lastReplicatedIndex);
                    return true;
                }else{
                    //if the master fail to replicate to the slave server, it decrease the corresponding
                    // next index by 1, and retry again and again until it success.
                    lock.lock();
                    try {
                        AppendResult appendResultAgain = new AppendResult();
                        appendResultAgain.setSuccess(false);
                        while(!appendResultAgain.isSuccess()){
                            nextIndexes.put(slave,nextIndexes.get(slave) - 1);
                            entries = getReplicateEntries(slave,logEntry);
                            prevLogEntry = LogModule.getPrev(entries.get(0));
                            prevLogIndex = getPrevLogIndex(prevLogEntry);
                            prevLogTerm = getPrevLogTerm(prevLogEntry);
                            appendResultAgain = client.handleAppendEntries(term,leaderId,prevLogIndex,
                                    prevLogTerm,entries,leaderCommit);
                        }
                    } finally {
                        lock.unlock();
                    }
                    return false;
                }
            }
        });
    }

    private List<LogEntry> getReplicateEntries(Peer slave,LogEntry logEntry){
        long nextIndex = nextIndexes.get(slave);
        List<LogEntry> entries = new ArrayList<>();
        if(logEntry.getIdex() >= nextIndex){
            for(long i = nextIndex; i <= logEntry.getIdex();i++){
                LogEntry curEntry = logModule.read(i);
                if(curEntry!=null) entries.add(curEntry);
            }
        }else{
            entries.add(logEntry);
        }
        return entries;
    }

    private int getPrevLogTerm(LogEntry prevLogEntry){
        int prevLogTerm;
        if(prevLogEntry==null){
            prevLogTerm = 0;
        }else{
            prevLogTerm = prevLogEntry.getTerm();
        }
        return prevLogTerm;
    }

    private long getPrevLogIndex(LogEntry prevLogEntry){
        long prevLogIndex;
        if(prevLogEntry==null){
            prevLogIndex = 0;
        }else{
            prevLogIndex = prevLogEntry.getIdex();
        }
        return prevLogIndex;
    }
}
