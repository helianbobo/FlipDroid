import sys
sys.path.append('./gen-py')

import threading,time
from it.tika import TikaService 
from it.tika.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
import config
HOST = config.TIKAHOST
POST = config.TIKAPOST
from checktimeout import timeout,Timeout

class Tika(object):
    timeout = config.THRIFTTIMEOUT
    def __init__(self,host=HOST,post=POST):
        self.transport = TSocket.TSocket(host, post)
        self.transport = TTransport.TFramedTransport(self.transport)
        self.protocol = TBinaryProtocol.TBinaryProtocol(self.transport,self.timeout)
        self.client = TikaService.Client(self.protocol)
        self.transport.open()
    
    
    def handleUrl(self,url):
        @timeout(20)
        def _handleUrl(self,url):
            r =False
            try:
                tr = TikaRequest(url)
                print url,"###befor fire"
                aa = self.client.fire(tr)
                print url,"###after fire"
                if aa.success:
                    print "success",tr.url
                    r = True
            except Exception,e:
                print "!!!!!errorurl!!!!:"+url
                print e
                r = False
            finally:
                return r
            
        try:
            r = _handleUrl(self,url)
        except Timeout, e:
            print e,"TIMEOUT handleUrl:",url
            r = False
        finally:
            return r
    
    def close(self):
        self.transport.close()

def test(url):  
    transport = TSocket.TSocket(HOST, POST)
    transport = TTransport.TFramedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport,config.THRIFTTIMEOUT)
    client = TikaService.Client(protocol)
    transport.open()
    r = False
    try:
        tr = TikaRequest(url)
        #tr.url=url
        #print tr.url
        aa=TikaResponse()
        aa=client.fire(tr)
        
        if aa.success:
            print "success",tr.url
            r = True

    except TikaException,e:
        import traceback
        traceback.print_exc()
        print e
    finally:
        transport.close()
        return r

def test2(url):
    tr = TikaRequest(url)
    print "success",tr.url        
    return True
   
    
def trun(url):
    t1=threading.Thread()
    t=threading.Thread(target=test,args=(url,))
    t.start()
    t.join()
     

if __name__ == "__main__":
    url=str("http://www.21jn.net/html/72/n-272.html")
    #tika=Tika()
    #tresponse=tika.handleUrl(url)
    #print tresponse.success
    test(url)
#thrift.transport.TTransport.TTransportException: TSocket read 0 bytes

    
