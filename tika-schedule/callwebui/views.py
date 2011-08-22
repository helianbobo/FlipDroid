from django.http import HttpResponse,HttpResponseRedirect
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.shortcuts import render_to_response
import pymongo
import model
import time
ISOTIMEFORMAT='%Y-%m-%d %X'

def test(request):
    return HttpResponse('showtest')

def paging(request,item_list,per_page=10):
    paginator = Paginator(item_list,per_page)
    p = request.GET.get('page','')
    try:
        items = paginator.page(p)
    except PageNotAnInteger:
        items = paginator.page(1)
    except EmptyPage:
        items = paginator.page(paginator.num_pages)
    return items




def showSource(request):
    item_list = [item for item in model.con.Source.find()]
    items = paging(request,item_list) 
    return render_to_response('source.html', {'items': items})

def updateSource(request):
    if request.GET['update']=='add':
        x = model.con.Source() 
        x["url"]=request.GET['url']
        x["type"]=request.GET['type']
        x["md5"] = u"1"
        x["time"]=unicode(time.strftime(ISOTIMEFORMAT, time.gmtime()))
         
        x.save()
        return HttpResponseRedirect("/source/")
        
    type = request.GET['type'] 
    url = request.GET['url']
    theid = request.GET['_id']
  
    if request.GET['update']=='update':
        model.con.tika.source.update({'_id':pymongo.objectid.ObjectId(theid)}, 
                                  {'$set':{"url": url,
                                            "time": time.strftime( ISOTIMEFORMAT, time.gmtime())                              
                                }},False) 
        return HttpResponseRedirect("/source/")

    if request.GET['update']=='delete':
        model.con.tika.source.remove({'_id':pymongo.objectid.ObjectId(theid)})
        return HttpResponseRedirect("/source/")
    
 

def showUrls(request):
    item_list = [item for item in model.con.Url_abstract.find()]
    #itemcontent_list = [(item["title"],item["content"]) for item in item_list]
    items=paging(request,item_list) 
    return render_to_response('urls.html', { 'items': items})


def updateUrls(request):        
   
    theid = request.GET['_id']
  
    if request.GET['update']=='show':
        item = model.con.tika.url_abstract.find({'_id':pymongo.objectid.ObjectId(theid)})
        html = item[0]['content']
        return HttpResponse(html)

    if request.GET['update']=='delete':
        model.con.tika.url_abstract.remove({'_id':pymongo.objectid.ObjectId(theid)})
        return HttpResponseRedirect("/urls/")
        
 
    