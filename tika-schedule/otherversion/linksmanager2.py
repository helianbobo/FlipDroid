# -*- coding: utf-8 -*-  
'''
Created on 2011-8-10

@author: shang
'''


import dbtools  
from linkitem import *
import threading
import usethrift
from Queue import Queue

class LinksManager(threading.Thread):
    threadnum=5
    event = threading.Event()
    alllinkitem=[]
    
    def __init__(self,*args,**kwargs):
        threading.Thread.__init__(self,*args,**kwargs)
        self.db=dbtools.DBreader()
        self.input_queue=Queue(self.threadnum)

    def addTasks(self,linkitems): 
        length=len(linkitems)
        if length==0 or linkitems is None:
            return "no items"
        for linkitem in linkitems:
            self.input_queue.put(linkitem)
            

    def run(self):
        while True:
            item = self.input_queue.get()
            if item is None:
                break
            self.callHandle(item)
            self.input_queue.task_done()
        self.input_queue.task_done()
        return 

    def close(self):
        self.input_queue.put(None)
        self.input_queue.join()
        self.db.close()
        print "$$$$$$$$$$$"
    def callHandle(self,linkitem,entag=None):
        def callHandleTika(_linkitem):
            issuccessed=usethrift.test(_linkitem.url)
            #tika=usethrift.Tika()
            #issuccessed=tika.handleUrl(_linkitem.url)
            if issuccessed:
                _linkitem.state=KEYSTATE_SUCCESS
                self.updateDB(_linkitem)       
            return True

        if True: 
            if linkitem.type == KEYTYPE_RSS:  
                print "theurl",linkitem.url
                result = callHandleTika(linkitem)
            #if entag:
                #self.event.set()
                #return result
                print linkitem.url,"write to db success"

    def updateDB(self,linkitem):
        self.db.updateTarget(linkitem)
    
    def checkDB(self):
        return True
 



