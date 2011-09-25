# -*- coding: utf-8 -*-  
'''
Created on 2011-8-10

@author: shang
'''
DEBUG=True
import time
def testtime(func):
    def gettime(*arg):
        if not DEBUG:
            x=func(*arg) 
            return  x
        #import time
        print "time:"
        ss=time.clock()
        x=func(*arg)
        se=time.clock()
        print '%.10f'%(se-ss)
        return x
    return gettime


import threading
import feedparser

threadnum=10
semaphore = threading.Semaphore(threadnum)  

@testtime
def testfeedparser():
     
    url="http://www.ifanr.com/feed"
    rss = feedparser.parse(url)

  
@testtime
def testfeedparser2():
    import urllib2
    url="http://www.ifanr.com/feed"
    event = threading.Event()
    islast=False
    #urlopen = urllib2.urlopen(url)
    urls=["http://www.ifanr.com/feed","http://www.ifanr.com/feed"]*2
    
    def testfeedparser(content,islast):
        if semaphore.acquire():
            rss = feedparser.parse(content)
            print 1
            if islast:
                event.set()
            semaphore.release()
    for i,url in enumerate(urls):
        if i==len(urls)-1:
            islast=True
        t = threading.Thread(target=testfeedparser,args=(url,islast))
        t.start()
        #t.join()
    event.wait()

import urllib2
url="http://www.ifanr.com/feed"
urlopen = urllib2.urlopen(url)
a=urlopen.read()
@testtime
def testfeedparser3():
    
    url="http://www.ifanr.com/feed"
     
    islast=False
    
    urls=["http://www.ifanr.com/feed","http://www.ifanr.com/feed"]*10
    urlscontents=[a,a]*10
    def testfeedparser(content,i):
        rss = feedparser.parse(content)
        print time.ctime()

    
    
    print time.ctime()
    for i,url in enumerate(urls):
        t= threading.Thread(target=testfeedparser,args=(url,i))
        t.start()

import hashlib
def testfeedparserhash():
    import urllib2
    url="http://www.36kr.com/feed"
    urlopen = urllib2.urlopen(url)
    content=urlopen.read()
    print type(content)
    rss = feedparser.parse(content)
    print rss.feed
    rssmd5 = hashlib.md5(content).hexdigest() 
    print rssmd5,len(rssmd5)
    #s = md5.new(rss.values().__hash__).hexdigest()
    
     
    
    


    
if __name__ =="__main__":
    #testfeedparser()
    #testfeedparser2()
    #testfeedparser3()
    #t= threading.Thread(target=testfeedparser3)
    #t.start()
    testfeedparserhash()

