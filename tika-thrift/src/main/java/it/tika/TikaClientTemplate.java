package it.tika;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/29/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class TikaClientTemplate {
    private String host;
    private int port;

    public TikaClientTemplate(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void doWithTika(DoWithTikaRequest doWithTikaRequest) {
        TTransport transport = null;
        try {
            transport = new TSocket(host, port);
            transport = new TFramedTransport(transport);
            TProtocol protocol = new TBinaryProtocol(transport);
            TikaService.Client client = new TikaService.Client(protocol);
            transport.open();
            doWithTikaRequest.doWithTikaRequest(client);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (transport != null)
                transport.close();
        }
    }
}
