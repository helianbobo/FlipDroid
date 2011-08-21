'''
Created on 2011-8-10

@author: shang
'''
KEYURL="url"
KEYTYPE="type"
KEYSTATE="state"
KEYTIME ="time"
KEYTYPE_RSS="rss"
KEYTYPE_WEIBO="weibo"
KEYSTATE_NOREAD="noread"
KEYSTATE_SUCCESS="success"
KEYSTATE_FAIL="fail"
KEYCONTENT="content"
KEYTITLE ="title"
KEYIMAGES="images"


class LinkItem():
    def __init__(self,url,content,title,images,type=KEYTYPE_RSS,state=KEYSTATE_NOREAD):
        self.url=url
        self.type=type
        self.state=state
        self.content=content
        self.images=images
        self.title=title

    def __repr__(self):
        return  self.url


            
   
        
    
