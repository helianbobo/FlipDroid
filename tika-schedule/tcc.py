'''
Created on 2011-8-16

@author: shang
'''
import linksmanager
import rssmanager
import threading
import time
import config
from dbtools import ISOTIMEFORMAT

class TCC(object):
    interval = config.INTERVAL #second  
    
    LmClass = linksmanager.LinksManagerUseCoroutine
    rm = rssmanager.RssManager()
    
    def setLinksmanagerClass(self,LmClass = linksmanager.LinksManagerUseCoroutine):
        self.LmClass = LmClass
    
    def runonce(self):
        self.lm=self.LmClass()
        self.lm.start()
        self.rm = rssmanager.RssManager()
        t = threading.Thread(target=self.rm.handleLinksFromAllRss,args=(self.lm,))
        #self.rm.handleLinksFromAllRss(self.lm)
        #print "***"*3,"allnewlinksnum:",self.rm.alllinksnum
        t.start()
        t.join()
        print "##########\n"+"###fininsh onece!!!\n""##########\n" 
    
    def start(self):
        print "**********\n"+"###start tcc now!!!\n"+"**********\n"
        #self.lm=self.LmClass
        #self.lm.start()
       #time.sleep(2)
        i=0
        #self.rm = rssmanager.RssManager()
        #self.rm.handleLinksFromAllRss(self.lm)
        #self.lm.close()
        while True:
            i += 1
            print 
            print "##########\n"+"###start onece !!!"+str(i)+"\n"
             
            self.rm = rssmanager.RssManager()
            #t = threading.Thread(target=self.rm.handleLinksFromAllRss,args=(self.lm,))
            #t.start()
            #t.join()
            self.rm.handleLinksFromAllRss(self.LmClass)
            ftime=time.strftime( ISOTIMEFORMAT, time.gmtime())
            print "##########\n"+"###"+ftime+" the "+str(i)+" fininsh onece!!!\n""##########\n"
            print "##########\n"+"###to sleep %s  seconds!!!\n"%self.interval+"##########\n" 
            time.sleep(self.interval)
            
        
    
    def shutdown(self):
        try:
            self.lm.close(),self.rm.close()
            print "shutdown!!!"
        except Exception,e:
            print e
            print "erro when shuotdonw"
            
        


 

 
            
 

if __name__ == '__main__':
 
    a=TCC()
    a.start()
     
     
