'''
Created on 2011-8-21

@author: shang
'''
#from pymongo import Connection
from mongokit import * 
#db = Connection().tika
DBUSERNAME="tika"
DBPASSWORD="Pass1234."
DBHOST='mongoc2.grandcloud.cn'
DBPORT=10005

#con = Connection(DBHOST,DBPORT,slave_okay=True)
#con.tika.authenticate(DBUSERNAME, DBPASSWORD)


DB_HOST='hemal1'
DB_PORT=27017
con = Connection(DB_HOST,DB_PORT)


#class Tika(object):
#    db = db
#    @classmethod
#    def objects(cls):
#        return cls.db[cls.name].find()
# 
#    
#    
#
#class Source(Tika):
#    name="source"
#    db = Connection().tika
import datetime   
@con.register
class Source(Document):
    __collection__ = 'source'
    __database__ = 'tika'
    structure = {
                 'url':unicode,
                 'type':unicode,
                 'md5':unicode,
                 'time':datetime.datetime,
                 'class':unicode, 
                 }
    required_fields = ['url' ]
    
@con.register
class Url_abstract(Document):
    __collection__ = 'url_abstract'
    __database__ = 'tika'
    structure = {
                 'url':unicode,
                 'type':unicode,
                 'time':unicode,
                 } 
    required_fields = ['url' ]
    indexes = [{'fields':[('time',INDEX_DESCENDING)] },]
    
    
@con.register
class Recommend_Source(Document):
    __collection__ = 'RecommendSource'
    __database__ = 'tika'
    structure = {
                 'body':unicode,
                  'lastModified':datetime.datetime,
                 'type':unicode,
                 } 
    # = ['body' ]
    #indexes = [{'fields':[('lastModified',INDEX_DESCENDING)] },]
    
     
     

  

