import json
from dbtools import *
def getUrls():
    f="RSS_RECOMMAND_SOURCE_DATA.json"
    thejson = json.loads(open(f).read())
    urls=[]
    for x in thejson:
        print x["content_url"]
        urls.append( x["content_url"])
    return urls

def save_to_sdb():
    mydb,con=con_db()
    muser = mydb
    if muser.source.find().count() <> 0:
        con.disconnect()
        return None
    urls=getUrls()
    for url in urls:
        muser.source.save({linkitem.KEYTIME:time.strftime( ISOTIMEFORMAT, time.gmtime()),
                       linkitem.KEYURL:url,linkitem.KEYTYPE:linkitem.KEYTYPE_RSS,'md5':'1'})



    con.disconnect()
    
    


if __name__=="__main__":
    save_to_sdb()
