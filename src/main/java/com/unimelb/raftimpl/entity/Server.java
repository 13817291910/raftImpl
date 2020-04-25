package com.unimelb.raftimpl.entity;

import com.unimelb.raftimpl.rpc.Consensus;
import com.unimelb.raftimpl.rpc.impl.ConsensusImpl;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.stereotype.Component;

@Component
public class Server {

    private final static int DEFAULT_PORT = 100001;
    private static TServer server = null;

    public void start(){
        try {
            TNonblockingServerSocket socket = new TNonblockingServerSocket(DEFAULT_PORT);
            Consensus.Processor processor = new Consensus.Processor(new ConsensusImpl());
            TNonblockingServer.Args args = new TNonblockingServer.Args(socket);
            args.protocolFactory(new TBinaryProtocol.Factory());
            args.transportFactory(new TFramedTransport.Factory());
            args.processorFactory(new TProcessorFactory(processor));
            server = new TNonblockingServer(args);
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
