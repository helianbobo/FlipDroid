# -*- coding: utf-8 -*-  
'''
Created on 2011-8-10

@author: shang
'''

import traceback
import dbtools  
#import testtools
from linkitem import *
import threading,time
import usethrift
from Queue import Queue

   
            
class LinksManagerUseCoroutine(threading.Thread):
    threadnum=10
    alllinkitem=[]
    
    def __init__(self,*args,**kwargs):
        threading.Thread.__init__(self,*args,**kwargs)
        self.db=dbtools.DBreader()
        self.input_queue=Queue(maxsize = self.threadnum)
        self.tika=usethrift.Tika()
        
    def start(self):
        threading.Thread.start(self)

    def addTasks(self,linkitems): 
        length=len(linkitems)
        if length==0 or linkitems is None:
            return False
        for linkitem in linkitems:
            self.input_queue.put(linkitem)
        return True
            

    def run(self):
        while True:
            item = self.input_queue.get()
            if item is None:
                break
            r = self.callHandle(item)
            print item.url,"*** writed to db"+str(r)
            self.input_queue.task_done()
        self.input_queue.task_done()
        return

    
    def join(self):
        self.input_queue.join()

        
    def close(self):
        self.input_queue.put(None)
        self.input_queue.join()
        self.db.close()
        self.tika.close()
        print "lm.close!!!!!!!"
        
    def callHandle(self,linkitem,entag=None):
        def callHandleTika(_linkitem):
            #issuccessed = usethrift.test(_linkitem.url)
            #tika=usethrift.Tika()
            issuccessed=self.tika.handleUrl(_linkitem.url)
            if issuccessed:
                _linkitem.state=KEYSTATE_SUCCESS
                self.updateDB(_linkitem)       
            return True

         
        if linkitem.type == KEYTYPE_RSS:  
            print "theurl",linkitem.url
            try:
                result = callHandleTika(linkitem)
                return True
            except:
                traceback.print_exc()
                return False
                

    def updateDB(self,linkitem):
        return self.db.updateTarget(linkitem)
    
    def checkDB(self):
        return True


#dont use
class LinksManager(object):
    '''
    dont use
    '''
    threadnum=3
    #semaphore = threading.Semaphore(3)
    event = threading.Event()
    alllinkitem=[]
    
    def __init__(self,linksitems=None):
        self.linkitems=linksitems
        self.db=dbtools.DBreader()
    
    def start(self):
        pass

    def run(self): 
        #if self.semaphore.acquire():
        length=len(self.linkitems)
        if length==0 or self.linkitems is None:
            self.db.close()
            return self.checkDB
 
        for i,linkitem in enumerate(self.linkitems):
            tag=False
            if i==length-1:
                tag=True    
            t = threading.Thread(target=self.callHandle,args=(linkitem,tag))
            t.start()
            #self.semaphore.release()
        self.event.wait()
        print "all threadings fininsh"
        self.db.close()
        return self.checkDB
    
    def addTasks(self,linkitems): 
        length=len(linkitems)
        if length==0 or linkitems is None:
            return "no items"
        for i,linkitem in enumerate(linkitems):
            tag=False
            if i==length-1:
                tag=True    
            t = threading.Thread(target=self.callHandle,args=(linkitem,tag))
            t.start()
            
    
    def updateDB(self,linkitem):
        return self.db.updateTarget(linkitem)
    
    def checkDB(self):
        return True
    
    semaphore = threading.Semaphore(threadnum)
    def callHandle(self,linkitem,entag):
        
        def callHandleTika(_linkitem):
            #issuccessed=usethrift.test(_linkitem.url)
            tika=usethrift.Tika()
            issuccessed=tika.handleUrl(_linkitem.url)
            if issuccessed:
                
                _linkitem.state=KEYSTATE_SUCCESS
                wdbrs = self.updateDB(_linkitem)
                print linkitem.url,"write to db",wdbrs       
            return True
        info=(threading.currentThread().getName(),linkitem.url)
        print '%s wait... %s \n' % info
        if self.semaphore.acquire(): 
            if linkitem.type == KEYTYPE_RSS:
                print '%s todo: %s \n' % info
                #print linkitem.url
                result = callHandleTika(linkitem)
                
            #time.sleep(1)
            self.semaphore.release()
            if entag:
                self.event.set()
                #return result
            #self.event.set()
            #self.semaphore.release()
            
    def close(self):
        pass




class LinksManagerUseCoroutine2(LinksManagerUseCoroutine):

    
    def callHandle(self,linkitem,entag=None):
        def callHandleTika(_linkitem):
            #issuccessed=usethrift.test(_linkitem.url)
            tika=usethrift.Tika()
            issuccessed=tika.handleUrl(_linkitem.url)
            if issuccessed:
                _linkitem.state=KEYSTATE_SUCCESS
                t = threading.Thread(target=self.updateDB,args=(_linkitem,))
                t.start()
                #self.updateDB(_linkitem)       
            #return True

         
        if linkitem.type == KEYTYPE_RSS:  
            print "theurl",linkitem.url
            result = callHandleTika(linkitem)
            print linkitem.url,"write to db success"
                
    semaphore = threading.Semaphore(5)
    def updateDB(self,linkitem):
        
        info=(threading.currentThread().getName(),linkitem.url)
        print '%s wait... %s \n' % info
        if self.semaphore.acquire():
            print '%s todo... %s \n' % info
            self.db.updateTarget(linkitem)
            print "abcd%%%%%%%%%%%%%%%"+linkitem.url
            self.semaphore.release()
 

if __name__ == "__main__":
    a=LinkItem(url="lk1",mode="rss")
    b=LinkItem(url="lk2",mode="rss")
    c=LinkItem(url="lk3",mode="rss")
    d=LinkItem(url="lk4",mode="rss")
    e=LinkItem(url="lk5",mode="rss")
    fs=[]
    for i in range(1000):
        fs.append(a)
    a=LinksManager(fs)
    a.run()
    
    print "yy"
