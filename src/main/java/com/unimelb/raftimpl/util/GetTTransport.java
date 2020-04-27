package com.unimelb.raftimpl.util;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class GetTTransport {

    public static TTransport getTTransport(String host, int port, int timeout){
        try {
            TSocket tsocket = new TSocket(host,port,timeout);
            TTransport tTransport = new TFramedTransport(tsocket);
            if(!tTransport.isOpen()){
                tTransport.open();
            }
            return tTransport;
        } catch (TTransportException e) {
            e.printStackTrace();
            return null;
        }
    }
}
