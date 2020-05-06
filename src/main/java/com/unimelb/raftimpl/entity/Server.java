package com.unimelb.raftimpl.entity;

import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.impl.ConsensusImpl;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    @Value("${self.port}")
    private int DEFAULT_PORT;

    private static TServer server = null;

    @PostConstruct
    public void start(){
        try {
            TNonblockingServerSocket socket = new TNonblockingServerSocket(DEFAULT_PORT);
            Consensus.Processor processor = new Consensus.Processor(new ConsensusImpl());
            THsHaServer.Args args = new THsHaServer.Args(socket);
            args.protocolFactory(new TBinaryProtocol.Factory());
            args.transportFactory(new TFramedTransport.Factory());
            args.processorFactory(new TProcessorFactory(processor));
            server = new THsHaServer(args);
            log.info("server starts at port {}",DEFAULT_PORT);
            new Thread(()->server.serve()).start();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
