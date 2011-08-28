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
useragent='Mozilla/5.0 (Windows NT 6.0; WOW64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.16 Safari/534.24'    
class RssManager(object):
    linkitems=[]
    db=dbtools.DBreader()
    TESTREPEAT=1
    isruned =0
    timeout=config.RSSTIMEOUT
    semaphore = threading.Semaphore(4)
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
                #request = urllib2.Request(rssitem[KEYURL])
                #request.add_header('User-Agent', useragent)
                #urlopen = urllib2.urlopen(request,timeout = self.timeout)
                
                urlopen = urllib2.urlopen(rssitem[KEYURL],timeout = self.timeout)
                content = urlopen.read()
                #rssmd5=md5.new(content).hexdigest()
                rssmd5=hashlib.md5(content).hexdigest()
                print rssmd5,rssitem["md5"]
                if rssmd5 is rssitem["md5"]:
                    #self.db.close()
                    return False
                
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
                    links_now = getLinksRssItems(rssitem)
                else:
                    links_now = False
                if links_now == False:
                    print "False"

                else:
                    links_new = []
                #print "len links in url_abstract ",self.links_db_target.find().count()
                    links_old = [item[KEYURL] for item in self.links_db_target.find()]
                    #self.db.close()
                    links_new = [item for item in links_now if item[0] not in links_old]
                    self.alllinksnum+=len(links_new)
                    partnewlinkitems=[LinkItem(url=item[0],type=item[1]) for item in links_new]
                    self.linkitems+=partnewlinkitems
                    r = self.callLinksManager(partnewlinkitems,linksmanager)
                    print "********rss handel:"+rssitem[KEYURL]+':' +str(r),threadnum


        except Exception , e:
            print e
            traceback.print_exc()
            
        finally:
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
        
    #no use threading when urlopen and feedparser rss url
    def getLinksItems2(self,rssitem,linksmanager,threadnum):   
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
                    return False
                
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
            self.isruned += 1
            print "@@@@@isruned",self.isruned,
            if rssitem[KEYTYPE]==KEYTYPE_RSS:  
                links_now = getLinksRssItems(rssitem)
            else:
                links_now = False
            if links_now == False:
                print "False"

            else:
                links_new = []
                links_old = [item[KEYURL] for item in self.links_db_target.find()]
                
                links_new = [item for item in links_now if item[0] not in links_old]
                self.alllinksnum+=len(links_new)
                partnewlinkitems=[LinkItem(url=item[0],type=item[1]) for item in links_new]
                self.linkitems+=partnewlinkitems
                r = self.callLinksManager(partnewlinkitems,linksmanager)
                print "********rss handel:"+rssitem[KEYURL]+':' +str(r),threadnum


        except Exception , e:
            print e
            traceback.print_exc()
            
        finally:
            if self.isruned == self.rsscount:
                print "###set####"
                #self.event.set()
            #self.semaphore.release()
            self.db.close()
            return links_now


    #no use threading when urlopen and feedparser rss url
    def handleLinksFromAllRss2(self,linksmanager=None):
        self.linksmanager=linksmanager()
        self.linksmanager.start()
        ts=[]
        for i,rssitem in enumerate(self.yieldLinkFromAllRss()):
            links_now = self.getLinksItems2(rssitem,self.linksmanager,i)          
        self.linksmanager.join()
        self.db.close()
        #self.event.wait()
        self.close()
       
        #self.linksmanager.close()
        #self.db.close()
        print "rssmanger once send over!!!!\n"
    
        

    def callLinksManager(self,partnewlinkitems,linksmanager=None):
        return linksmanager.addTasks(partnewlinkitems)


    def close(self):
        print "rm close!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        self.linksmanager.close()
        



    
    
    
    
        
