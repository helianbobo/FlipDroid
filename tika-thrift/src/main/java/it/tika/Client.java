package it.tika;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import javax.xml.ws.Response;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 11-8-13
 * Time: 下午10:15
 * To change this template use File | Settings | File Templates.
 */
public class Client {
    public static void main(String[] args) {
        try {
			TTransport transport = new TSocket("127.0.0.1", 9090);
            transport = new TFramedTransport(transport);
			TProtocol protocol = new TBinaryProtocol(transport);
			TikaService.Client client = new TikaService.Client(protocol);
			transport.open();
			System.out.println("Client calls .....");
            TikaRequest request = new TikaRequest();
            request.setUrl("http://sports.sina.com.cn/j/2011-08-13/21595701537.shtml") ;
            TikaResponse response = client.fire(request);
            System.out.println(response.getContent());
			transport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		} catch (TikaException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
