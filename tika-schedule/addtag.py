# -*- coding: utf-8 -*-
import dbtools,time
import codecs

import traceback  
from smallseg.smallseg import SEG 
seg = SEG()

def cut_text(content):
    wlist = seg.cut(content)
    wlist.reverse()
    for w in wlist:
        print type(w),w 
db=dbtools.DBreader()
db_url_abstract = db.getFromDBurl_abstract()
notags_links = db_url_abstract.find()
for i,item in enumerate(notags_links):
    if i == 100:
        break
    content=""
    title=""
    try:
        title = item['title']
        if title is  None:
            continue
        title = title.encode('utf-8') 
        print title,type(title)
         
        
        #cut_text( title  )
        
        content=item['content']
        if content is  None:
            continue
        content=content.encode('utf-8') 
        cut_text(content)
        
    except Exception  as e:
        print traceback.format_exc()  
        print e
        
        print "###########"*10
        
        time.sleep(10)
    print i
    
        #print item['title'].replace(u'\xa0', '')

db.close()
