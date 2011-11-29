package com.goal98.tika.utils;

import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.URLDBInterface;
import flipdroid.grepper.URLDBMongoDB;
import it.tika.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/29/11
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class RefetchSourceWithImage {
    public static void main(String[] args) {
        URLDBInterface urldb = URLDBMongoDB.getInstance();
        final List<URLAbstract> urlAbstracts = urldb.findByContainsImage(args[0]);

        TikaClientTemplate template = new TikaClientTemplate("127.0.0.1", 9090);
        template.doWithTika(new DoWithTikaRequest() {
            @Override
            public void doWithTikaRequest(TikaService.Client client) {
                for (URLAbstract urlAbstract : urlAbstracts) {
                    TikaRequest request = new TikaRequest();
                    request.setUrl(urlAbstract.getUrl());
                    try {
                        TikaResponse response = client.fire(request);
                    } catch (TikaException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (TException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });
    }


}
