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
    
    
    def handleUrl(self,url,referencedFrom):
        @timeout(15)
        def _handleUrl(self,url,referencedFrom):
            r =False
            try:
                tr = TikaRequest(url,referencedFrom)
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
            r = _handleUrl(self,url,referencedFrom)
        except Timeout, e:
            print e,"TIMEOUT handleUrl:",url
            r = False
        finally:
            return r
    
    def close(self):
        self.transport.close()

def test(url,referencedFrom):  
    transport = TSocket.TSocket(HOST, POST)
    transport = TTransport.TFramedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport,config.THRIFTTIMEOUT)
    client = TikaService.Client(protocol)
    transport.open()
    r = False
    try:
        tr = TikaRequest(url,referencedFrom)
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


