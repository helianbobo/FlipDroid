# -*- coding: utf-8 -*-  
'''
Created on 2011-8-10

@author: shang
'''

import pymongo
import linkitem
import config
username=config.DBUSERNAME
password=config.DBPASSWORD
HOST=config.DBHOST
POST=config.DBPOST

import time
ISOTIMEFORMAT='%Y-%m-%d %X'

def cleardb():
    mydb,con=con_db()
    muser = mydb
    enter = raw_input("remove db yes/no")
    if enter == "yes":
        #pass
        muser.source.remove()
        muser.url_abstract.remove()
    #con.disconnect()
    
    
    
def test_save_to_mdb():
    mydb,con=con_db()
    muser = mydb
   
    
    

    if muser.source.find().count() <> 0:
         
        con.disconnect()
        return None
    
    muser.source.save({linkitem.KEYTIME:time.strftime( ISOTIMEFORMAT, time.gmtime()),
                       linkitem.KEYURL:"http://www.36kr.com/feed/",linkitem.KEYTYPE:linkitem.KEYTYPE_RSS,'md5':'1'})
    muser.source.save({linkitem.KEYTIME:time.strftime( ISOTIMEFORMAT, time.gmtime()),
                        linkitem.KEYURL:"http://www.ifanr.com/feed",linkitem.KEYTYPE:linkitem.KEYTYPE_RSS,'md5':'1'})

    muser.source.save({linkitem.KEYTIME:time.strftime( ISOTIMEFORMAT, time.gmtime()),
                       linkitem.KEYURL:"http://android.guao.hk/feed",linkitem.KEYTYPE:linkitem.KEYTYPE_RSS,'md5':'1'})
    con.disconnect()
    



     
def con_db():
    con = pymongo.Connection(HOST, POST) 
    mydb = con.tika
    #mydb.authenticate(username, password)
    
    return mydb,con    
      
class DBreader(object):
    def __init__(self):
        self.con = pymongo.Connection(HOST, POST) 
        self.mydb = self.con
        #self.mydb.authenticate(username, password)
        
    def getFromDBsource(self,):
        return self.mydb.tika.source
    
    def getFromDBurl_abstract(self):
        return self.mydb.tika.url_abstract
    
    def updateTarget(self,items):
#        muser=self.mydb.tika
#       if not isinstance(items,list):
#            items=[items]
        
#        for item in items:
#            muser.url_abstract.save({"time":time.strftime( ISOTIMEFORMAT, time.gmtime()),linkitem.KEYURL:item.url,linkitem.KEYTYPE:item.type,
#                                 linkitem.KEYSTATE:item.state})
#            muser.url_abstract.save({"time":time.strftime( ISOTIMEFORMAT, time.gmtime()),linkitem.KEYURL:item.url,linkitem.KEYTYPE:item.type,linkitem.KEYSTATE:item.state,
#                                    linkitem.KEYCONTENT:item.content,linkitem.KEYTITLE:item.title,linkitem.KEYIMAGES:item.images})
        return True

    def updateSourceRssMD5(self,theid,md5):
        source = self.mydb.tika.source
        source.update({"_id":theid}, {"$set" : {"md5": md5}}, False)

    
    def close(self):
        print "close con"
        self.con.disconnect()
    

    
if __name__=="__main__":
    
    test_save_to_mdb()
    db=DBreader()
    results=db.getFromDBsource()
    for item in results.find() :
        print item
        print dir(item)
        
    db.close()
