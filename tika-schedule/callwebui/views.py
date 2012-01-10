from django.http import HttpResponse,HttpResponseRedirect
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.shortcuts import render_to_response
import pymongo
import model
import time
import datetime
ISOTIMEFORMAT='%Y-%m-%d %X'

PASSWD="qqwwee"
SESSIONID="user"

TYPE_SINA_WEIBO = "SINA_WEIBO"
TYPE_RSS = "RSS"
TYPE_DEFAULT = "DEFAULT"

def islogin(func):
    def fun(request,**argw):
        #if args[].session['userid'] <> "user":
        if request.session.get('userid')=="user":
            return func(request,**argw)
        return HttpResponseRedirect('/')
    return fun


def test(request):
    return HttpResponse('showtest')

def paging(request,item_list,page=1,per_page=10):
    paginator = Paginator(item_list,per_page)
    #p = request.GET.get('page','')
    p=str(page)
    try:
        items = paginator.page(p)
    except PageNotAnInteger:
        items = paginator.page(1)
    except EmptyPage:
        items = paginator.page(paginator.num_pages)
    return items

def pagingSearch(request,item_list,per_page=10):
    paginator = Paginator(item_list,per_page)
    p = request.GET.get('page','')
    url=request.GET.get('url','')
    try:
        items = paginator.page(p)
    except PageNotAnInteger:
        items = paginator.page(1)
    except EmptyPage:
        items = paginator.page(paginator.num_pages)
    return items

@islogin
def showSource(request,page=1):
     
     
    #item_list = [item for item in model.con.Source.find()]
    item_list =  model.con.Source.find() 
    items = paging(request, item_list,page) 
    return render_to_response('source.html', {'items': items})


@islogin
def showRecommendSource(request,page=1):
     
    item_list =  model.con.Recommend_Source.find()  
    items = paging(request,item_list,page) 
    return render_to_response('rsource.html', {'items': items})
    #return render_to_response('rsource.html', None)

@islogin
def updateSource(request):
    
    if request.GET['update']=='add':
        x = model.con.Source() 
        x["url"]=request.GET['url']
        x["type"]=request.GET['type']
        x["md5"] = u"1"
        x["time"]=datetime.datetime.utcnow()
         
        x.save()
        return HttpResponseRedirect("/console/source/")
        
    type = request.GET['type'] 
    url = request.GET['url']
    theid = request.GET['_id']
  
    if request.GET['update']=='update':
        model.con.tika.source.update({'_id':pymongo.objectid.ObjectId(theid)}, 
                                  {'$set':{"url": url,
                                            "time": datetime.datetime.utcnow()                              
                                }},False) 
        return HttpResponseRedirect("/console/source/")

    if request.GET['update']=='delete':
        model.con.tika.source.remove({'_id':pymongo.objectid.ObjectId(theid)})
        return HttpResponseRedirect("/console/source/")
    
 
@islogin
def updateRecommendSourceSource(request):
    if request.GET['update']=='add':
        x = model.con.Recommend_Source() 
        x["body"]=request.GET['body']
        x["type"]=request.GET['type']
         
        #x["lastModified"]=unicode(time.strftime(ISOTIMEFORMAT, time.gmtime()))
        x["lastModified"]=datetime.datetime.utcnow()
         
        x.save()
        return HttpResponseRedirect("/console/recommendsource/")
        
    type = request.GET['type'] 
    body = request.GET['body']
    theid = request.GET['_id']
  
    if request.GET['update']=='update':
        model.con.tika.RecommendSource.update({'_id':pymongo.objectid.ObjectId(theid)}, 
                                  {'$set':{"body": body,
                                           "lastModified": datetime.datetime.utcnow(),
                                            #"lastModified": time.strftime( ISOTIMEFORMAT, time.gmtime()),
                                            "type":type,                           
                                }},False) 
        return HttpResponseRedirect("/console/recommendsource/")

    if request.GET['update']=='delete':
        model.con.tika.RecommendSource.remove({'_id':pymongo.objectid.ObjectId(theid)})
        return HttpResponseRedirect("/console/recommendsource/")
    
 
@islogin
def showUrls(request,page=1):
    #item_list = [item for item in model.con.Url_abstract.find()]
    #import re  
    #q      = re.compile(r'.*')
    #item_list = [item for item in model.con.Url_abstract.find( {'url' : { '$regex' : q } } )]
    
    item_list =model.con.Url_abstract.find().sort('time', pymongo.DESCENDING)
    items=paging(request,item_list,page) 
    return render_to_response('urls.html', { 'items': items})





import re
@islogin
def searchUrls(request):
    url = request.GET['url']
    page = request.GET.get('page','')
     
    item_list =  model.con.Url_abstract.find({"url":{'$regex': re.escape(url)}}).sort('time', pymongo.DESCENDING) 
    items=paging(request,item_list,page) 
    return render_to_response('urlssearch.html', {'items': items,'url':url})

@islogin
def updateUrls(request):        
   
    theid = request.GET['_id']
    print "#######:",theid
  
    if request.GET['update']=='show':
        
        item = model.con.tika.url_abstract.find_one({'_id':pymongo.objectid.ObjectId(theid)})
         
         
        html= item['content']
         
        return HttpResponse(html)
    
    if request.GET['update']=='delete':
        model.con.tika.url_abstract.remove({'_id':pymongo.objectid.ObjectId(theid)})
        return HttpResponseRedirect("/console/urls/")
    
    

    
def loginPage(request):
    return render_to_response('login.html') 

def login(request):
     
    if request.method != 'POST':
        from django.http import Http404
        raise Http404('Only POSTs are allowed')
    try:
        pwd = request.REQUEST['password']
        if pwd == PASSWD:
            request.session['userid'] = SESSIONID
            return HttpResponseRedirect('/console/source/')
        return HttpResponse("password error")
    except:
        return HttpResponse("password error")

def sessiontest(request):
    if request.method == 'POST':
        request.session.set_test_cookie()
        if request.session.test_cookie_worked():
            request.session.delete_test_cookie()
            return login(request)
        else:
            return HttpResponse("Please enable cookies and try again.")
    return sessiontest(request) 



 
    