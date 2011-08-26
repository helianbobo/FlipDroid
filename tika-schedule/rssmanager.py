# -*- coding: utf-8 -*-  
'''
Created on 2011-8-10

@author: shang
'''
import traceback
import feedparser
import dbtools  
import testtools
from linkitem import *
import hashlib
import urllib2
import threading
import config

class RssManager(object):
    linkitems=[]
    db=dbtools.DBreader()
    TESTREPEAT=1
    isruned =0
    timeout=config.RSSTIMEOUT
    semaphore = threading.Semaphore(5)
    def __init__(self):
        self.links_db_target = self.getLinksFromDB()
    
 
    def getLinksItems(self,rssitem,linksmanager,threadnum):   
        def getLinksWeiboItems(rssitem):
            pass
    
        def getLinksRssItems(rssitem):
            try:
                 
                print "#"*10
                print "###start feedparser",rssitem[KEYURL]
                print "#"*10
                urlopen = urllib2.urlopen(rssitem[KEYURL],timeout = self.timeout)
                content = urlopen.read()
                #rssmd5=md5.new(content).hexdigest()
                rssmd5=hashlib.md5(content).hexdigest()
                print rssmd5,rssitem["md5"]
                if rssmd5 is rssitem["md5"]:
                    #self.db.close()
                    return True
                
                self.db.updateSourceRssMD5(rssitem["_id"],rssmd5)
                
                rss = feedparser.parse(content)
                #rss = feedparser.parse(rssitem[KEYURL])   
                links = [(item.link,KEYTYPE_RSS) for item in rss.entries]
                urlopen.close()
                #self.db.close()
                return links
            
            except:
                print "####error rss url: "+rssitem[KEYURL]
                traceback.print_exc()
                return False

            
        try:
            if self.semaphore.acquire():
                self.isruned += 1
                print "@@@@@isruned",self.isruned,
                if rssitem[KEYTYPE]==KEYTYPE_RSS:
                #return getLinksRssItems(rssitem)
                 
                    links_now = getLinksRssItems(rssitem)
                    
                
                    if links_now == False:
                        print "False"
                        if self.isruned == self.rsscount:
                            print "###set####"
                            self.event.set()
                        #self.event.clear()
                         
                        return links_now
            
                    links_new = []
                #print "len links in url_abstract ",self.links_db_target.find().count()
                    links_old = [item[KEYURL] for item in self.links_db_target.find()]
                    self.db.close()
                
                    links_new = [item for item in links_now if item[0] not in links_old]
                    self.alllinksnum+=len(links_new)	
                    partnewlinkitems=[LinkItem(url=item[0],type=item[1]) for item in links_new]
                    self.linkitems+=partnewlinkitems
                    r = self.callLinksManager(partnewlinkitems,linksmanager)
                
                    print "********rss handel:"+rssitem[KEYURL]+':' +str(r),threadnum
                
                    if self.isruned == self.rsscount:
                        print "###set####"
                        self.event.set()
                    #self.event.clear()
                    #pass
                    self.semaphore.release()
                    return links_now    

        except Exception , e:
            print e
            if self.isruned == self.rsscount:
                    print "###set####"
                    self.event.set()
            self.semaphore.release()
            traceback.print_exc()
            self.db.close()
            return False
            

    
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
        self.linksmanager=linksmanager()
        self.linksmanager.start()
        ts=[]
        for i,rssitem in enumerate(self.yieldLinkFromAllRss()):
            #links_now = self.getLinksItems(rssitem)
            t=threading.Thread(target=self.getLinksItems,args=(rssitem,self.linksmanager,i))
            ts.append(t)
            t.start()
            #t.join()
          
        for t in ts:
            t.join()
        #self.linksmanager.close()
        self.linksmanager.join()
        self.db.close()
        self.event.wait()
        self.close()
       
        #self.linksmanager.close()
        #self.db.close()
        print "rssmanger once send over!!!!\n"
        

    def callLinksManager(self,partnewlinkitems,linksmanager=None):
        return linksmanager.addTasks(partnewlinkitems)


    def close(self):
        print "rm close!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        self.linksmanager.close()
        



    
    
    
    
        
