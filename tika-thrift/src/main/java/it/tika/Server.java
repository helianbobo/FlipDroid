package it.tika;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Server {
    public static void main(String[] args) {
        System.out.println("Starting the server...");
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);
            serverArgs.processor(new TikaService.Processor(new TikaServiceImpl()));
            serverArgs.transportFactory(new TFramedTransport.Factory());
            serverArgs.protocolFactory(new TBinaryProtocol.Factory(true, true));
            TServer server = new TThreadPoolServer(serverArgs);
            server.serve();
            System.out.println("Starting the server...");

        } catch (Exception x) {
            x.printStackTrace();
        }
        System.out.println("done.");
    }
}