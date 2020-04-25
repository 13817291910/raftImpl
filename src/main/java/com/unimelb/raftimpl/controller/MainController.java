package com.unimelb.raftimpl.controller;

import com.unimelb.raftimpl.entity.CommonMsg;
import com.unimelb.raftimpl.entity.impl.Node;
import com.unimelb.raftimpl.rpc.Consensus;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
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



    @RequestMapping("/send")
    CommonMsg send(@RequestParam String text){
        log.info("the front end send the text {}",text);
        TTransport tTransport = null;
            try {
                tTransport = new TSocket("localhost",11);
                TProtocol protocol = new TBinaryProtocol(tTransport);
                Consensus.Client thriftClient = new Consensus.Client(protocol);
                //TODO: thriftClient.handleAppendEntries()
                tTransport.open();
            } catch (Exception e) {
                log.error("client init failed");
                e.printStackTrace();
            }finally {
                tTransport.close();
            }


        return null;
    }


}
