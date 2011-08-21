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
    def __init__(self):
        self.links_db_target = self.getLinksFromDB()
    
    @testtools.testtime
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
                return links
            
            except:
                traceback.print_exc()
                return False

            
        try:
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
                        pass
                    return links_now
            
                links_new = []
                print "len links in url_abstract ",self.links_db_target.find().count()
                links_old = [item[KEYURL] for item in self.links_db_target.find()]
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
                return links_now    

        except Exception , e:
            traceback.print_exc()
            return False
            
        
    
    def getLinksFromAllRss(self):
        def getRssAndWeiboItems():
            #self.db=dbtools.DBreader()
            results=self.db.getFromDBsource().find()
            return [(item[KEYURL],item[KEYTYPE] )for item in results.find()]
       
        items=getRssAndWeiboItems()
        allitems=[]
        for item in items: 
            partitems=self.getLinksItems(item)
            allitems+=partitems
        return allitems
    
    def getLinksFromDB(self):
        #tip:only return TYPE rss
        results=self.db.getFromDBurl_abstract()
        return  results
         
    
    def getWeiboUrl(self):
        #todo:
        return None

    #dont use
    def getNewLinkItems(self):
        links_now = self.getLinksFromAllRss()
        links_db_target = self.links_db_target
        links_new=[x for x in links_now if x[0] not in [item[0] for item in links_db_target]]           
        #links_new=list(set(links_now)-set(links_db_target))      
        self.linkitems=[LinkItem(url=item[0],TYPE=item[1]) for item in links_new]
        #self.db.updateTarget(self.linkitems) 
        self.db.close() 
        return self.linkitems

    def yieldLinkFromAllRss(self):
        def getRssAndWeiboItems():
            results=self.db.getFromDBsource().find()
            return results 
            #return [(item[KEYURL],item[KEYTYPE] )for item in results]
       
        items=getRssAndWeiboItems()
        self.rsscount= items.count()
        print self.rsscount
        #allitem=[]
        #for item in items :  
            #allitem+=item
            #yield item
        return items
            
    alllinksnum=0
    linksmanager=None
    event=threading.Event()
    def handleLinksFromAllRss(self,linksmanager=None):
        self.linksmanager=linksmanager
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
        self.event.wait()
        
       
        #self.linksmanager.close()
        self.db.close()
        print "rssmanger once send over!!!!\n"
        

    def callLinksManager(self,partnewlinkitems,linksmanager=None):
        return linksmanager.addTasks(partnewlinkitems)


    def close(self):
        print "rm close!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        self.linksmanager.close()
 



    
    
    
    
        
