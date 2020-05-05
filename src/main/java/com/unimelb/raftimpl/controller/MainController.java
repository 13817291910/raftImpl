package com.unimelb.raftimpl.controller;

import com.unimelb.raftimpl.entity.CommonMsg;
import com.unimelb.raftimpl.entity.LogModule;
import com.unimelb.raftimpl.entity.impl.Node;
import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.LogEntry;
import com.unimelb.raftimpl.rpc.VoteResult;
import com.unimelb.raftimpl.util.GetTTransport;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private Node node;

    @Autowired
    private LogModule logModule;

    @RequestMapping("/send")
    CommonMsg send(@RequestParam String text){
        log.info("the front end send the text {}",text);
        TTransport tTransport = null;
            try {
                tTransport = GetTTransport.getTTransport("192.168.0.116",8083,5000);
                TProtocol protocol = new TBinaryProtocol(tTransport);
                Consensus.Client thriftClient = new Consensus.Client(protocol);
                VoteResult voteResult = thriftClient.handleRequestVote(0,"",0,0);
                log.info("VoteResult is {} {}",voteResult.getTerm(),voteResult.isVoteGranted());
                //TODO: thriftClient.handleAppendEntries(

                LogEntry curEntry = new LogEntry();
                curEntry.setText(text);
                node.handleRequest(curEntry,thriftClient);
                LogEntry logEntry = new LogEntry(0,1,"");
            } catch (Exception e) {
                log.error("thriftClient init fails");
                e.printStackTrace();
            } finally {
                if(tTransport!=null) tTransport.close();
            }

        return null;
    }

}
