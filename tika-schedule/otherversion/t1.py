# -*- coding: utf-8 -*-  
'''
Created on 2011-8-10

@author: shang
'''
import traceback
import feedparser
import dbtools  

from linkitem import *


import threading



class RssManagerTest(object):
    linkitems=[]
    db=dbtools.DBreader()
    TESTREPEAT=1
    isruned =0
    
    semaphore = threading.Semaphore(2)
    def __init__(self):
        self.links_db_target = self.getLinksFromDB()
    
 
    def getLinksItems(self,rssitem,threadnum):   
        def getLinksWeiboItems(rssitem):
            pass
    
        def getLinksRssItems(rssitem):
            try:
                print "#"*10
                print "###start feedparser",rssitem[KEYURL]
                print "#"*10
                rssid = str(rssitem["_id"])
                rss = feedparser.parse(rssitem[KEYURL])   
                links = [(item.link,KEYTYPE_RSS,rssid) for item in rss.entries]
 
                return links
            
            except:
                print "####error rss url: "+rssitem[KEYURL]
                traceback.print_exc()
                return False

            
        try:
            if self.semaphore.acquire():
                
                print "@@@@@isruned",self.isruned,
                if rssitem[KEYTYPE]==KEYTYPE_RSS:  
                    links_now = getLinksRssItems(rssitem)
                else:
                    links_now = False
                if links_now == False:
                    print "False"

                else:
                    links_new = []
              
                
                    #links_old = [item[KEYURL] for item in self.links_db_target.find()]
                     
                    #links_new = [item for item in links_now if item[0] not in links_old]
                    #self.alllinksnum+=len(links_new)
                    #partnewlinkitems=[LinkItem(url=item[0],type=item[1],referencedFrom=item[2]) for item in links_new]
                    #self.linkitems+=partnewlinkitems

        except Exception , e:
            print e
            traceback.print_exc()
            
        finally:
            self.isruned += 1
            if self.isruned == self.rsscount:
                print "###set####"
                self.event.set()
            self.semaphore.release()
            self.db.close()
            return links_now
            
            

    
    def getLinksFromDB(self):
        #tip:only return TYPE rss
        results=self.db.getFromDBurl_abstract()
        
        return  results
         
    
    def getWeiboUrl(self):
        #todo:
        return None



    def yieldLinkFromAllRss(self):
        def getRssAndWeiboItems():
            results=self.db.getFromDBsource().find()
            return results 
            
       
        items=getRssAndWeiboItems()
        self.rsscount= items.count()
        print self.rsscount
        return items
            
    alllinksnum=0
    linksmanager=None
    event=threading.Event()
    def handleLinksFromAllRss(self,linksmanager=None):
        
#        ts=[]
#        for i,rssitem in enumerate(self.yieldLinkFromAllRss()):
#            t=threading.Thread(target=self.getLinksItems,args=(rssitem,i))
#            t.setDaemon(True)
#            ts.append(t)
#            t.start()
#            
#        self.event.wait()  
#        for t in ts:
#            t.join()
        
        for i,rssitem in enumerate(self.yieldLinkFromAllRss()):
            self.getLinksItems(rssitem,i)
        
        print "#############\n#############\n#############\n"
        print "!!!counts:",len(self.linkitems)
        self.db.close()
        self.close()
        print "rssmanger once send over!!!!\n"
                
    def callLinksManager(self,partnewlinkitems,linksmanager=None):
        return linksmanager.addTasks(partnewlinkitems)


    def close(self):
        print "rm close!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        #self.linksmanager.close()


import time
from dbtools import ISOTIMEFORMAT 
def test2():
    stime=time.strftime( ISOTIMEFORMAT, time.gmtime())
    rm = RssManagerTest()
    t = threading.Thread(target=rm.handleLinksFromAllRss)
    t.start()
    t.join()
    ftime=time.strftime( ISOTIMEFORMAT, time.gmtime())
    print "##########\n"+stime+" ###to### "+ftime+" the "+" fininsh onece!!!\n""##########\n"
     


if __name__=='__main__':
    
    test2()
