from django.http import HttpResponse,HttpResponseRedirect
from django.shortcuts import render_to_response
import pymongo
import model
import time
ISOTIMEFORMAT='%Y-%m-%d %X'

def showSource(request):
    x = model.con.Source.find()
    return render_to_response('source.html', {'item_list': x})

def updateSource(request):
    if request.POST['update']=='add':
        x = model.con.Source()
         
        x["url"]=request.POST['url']
        x["type"]=request.POST['type']
        x["md5"] = u"1"
        x["time"]=unicode(time.strftime(ISOTIMEFORMAT, time.gmtime()))
         
        x.save()
        return HttpResponseRedirect("/source/")
        
    type = request.POST['type'] 
    url = request.POST['url']
    theid = request.POST['_id']
  
    if request.POST['update']=='update':
        model.con.tika.source.update({'_id':pymongo.objectid.ObjectId(theid)}, 
                                  {'$set':{"url": url,
                                            "time": time.strftime( ISOTIMEFORMAT, time.gmtime())                              
                                }},False) 
        return HttpResponseRedirect("/source/")

    if request.POST['update']=='delete':
        model.con.tika.source.remove({'_id':pymongo.objectid.ObjectId(theid)})
        return HttpResponseRedirect("/source/")
    
 

def showUrls(request):
    x = model.con.Url_abstract.find()
    return render_to_response('urls.html', {'item_list': x})
        
 
    