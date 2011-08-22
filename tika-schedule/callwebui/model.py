'''
Created on 2011-8-21

@author: shang
'''
#from pymongo import Connection
from mongokit import * 
#db = Connection().tika
from mongokit import *  
con = Connection()


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
    
@con.register
class Source(Document):
    __collection__ = 'source'
    __database__ = 'tika'
    structure = {
                 'url':unicode,
                 'type':unicode,
                 'md5':unicode,
                 'time':unicode,
                  
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
     

  

