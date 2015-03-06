# -*- coding: utf-8 -*-  
from django.http import HttpResponse,HttpResponseRedirect
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.shortcuts import render_to_response
import pymongo
import model
import time
import datetime
from django.template import loader,RequestContext 
from bson.objectid import ObjectId


ISOTIMEFORMAT='%Y-%m-%d %X'

PASSWD="qqwwee"
SESSIONID="user"

TYPE_SINA_WEIBO = "SINA_WEIBO"
TYPE_RSS = "RSS"
TYPE_DEFAULT = "DEFAULT"
TYPE_FEATURED = "FEATURED"

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
    return render_to_response('source.html', {'items': items,'thepage':page})


@islogin
def showRecommendSource(request,page=1):
     
    item_list =  model.con.Recommend_Source.find()  
    items = paging(request,item_list,page) 
    return render_to_response('rsource.html', {'items': items})
    #return render_to_response('rsource.html', None)

@islogin
def updateSource(request):
    
    try:
        if request.POST['update']=='adds':        
            _type=request.POST['type']
            urls=request.POST['urls'].encode("utf-8")
            _time = datetime.datetime.utcnow()
         
            for url in urls.split('\n'):
                url =  "".join(url.split())
                print url 
                x = model.con.Source() 
                x["url"]=url.decode('utf-8')
                x["type"]=_type
                x["md5"] = u"1"
                x["time"]=_time
                x["class"] = u"unknown"
                x.save()
            return HttpResponseRedirect("/console/source/")
    except Exception as e:
        print e
        
     
        
    page = request.path[request.path.rfind('/')+1:] 
    if request.GET['update']=='add':
        x = model.con.Source() 
        x["url"]=request.GET['url']
        x["type"]=request.GET['type']
        x["md5"] = u"1"
        x["class"] = u"unknown"
        x["time"]=datetime.datetime.utcnow()
         
        x.save()
        return HttpResponseRedirect("/console/source/"+page)
        
    _type = request.GET['type'] 
    url = request.GET['url']
    theid = request.GET['_id']
    theclass = request.GET['class']
    if request.GET['update']=='update':
        model.con.tika.source.update({'_id':ObjectId(theid)}, 
                                  {'$set':{"url": url,
                                            "time": datetime.datetime.utcnow() ,
                                             "class": theclass,
                                             "type": _type
                                }},False) 
        return HttpResponseRedirect("/console/source/"+page)

    if request.GET['update']=='delete':
        model.con.tika.source.remove({'_id':ObjectId(theid)})
        return HttpResponseRedirect("/console/source/"+page)
        
    
 
@islogin
def updateRecommendSourceSource(request):
    if request.POST['update']=='add':
        x = model.con.Recommend_Source() 
        x["body"]=request.POST['body']
        x["type"]=request.POST['type']
         
        #x["lastModified"]=unicode(time.strftime(ISOTIMEFORMAT, time.gmtime()))
        x["lastModified"]=datetime.datetime.utcnow()
         
        x.save()
        return HttpResponseRedirect("/console/recommendsource/")
        
    type = request.POST['type'] 
    body = request.POST['body']
    theid = request.POST['_id']
  
    if request.POST['update']=='update':
        model.con.tika.RecommendSource.update({'_id':ObjectId(theid)}, 
                                  {'$set':{"body": body,
                                           "lastModified": datetime.datetime.utcnow(),
                                            #"lastModified": time.strftime( ISOTIMEFORMAT, time.gmtime()),
                                            "type":type,                           
                                }},False) 
        return HttpResponseRedirect("/console/recommendsource/")

    if request.POST['update']=='delete':
        model.con.tika.RecommendSource.remove({'_id':ObjectId(theid)})
        return HttpResponseRedirect("/console/recommendsource/")
    
 
@islogin
def showUrls(request,page=1):
    #item_list = [item for item in model.con.Url_abstract.find()]
    #import re  
    #q      = re.compile(r'.*')
    #item_list = [item for item in model.con.Url_abstract.find( {'url' : { '$regex' : q } } )]
    
    item_list =model.con.Url_abstract.find().sort('time', pymongo.DESCENDING)
    items=paging(request,item_list,page) 
    return render_to_response('urls.html', { 'items': items,'vsss':"i am ok,haha"})





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
        
        item = model.con.tika.url_abstract.find_one({'_id':ObjectId(theid)})
         
         
        html= item['content']
         
        return HttpResponse(html)
    
    if request.GET['update']=='delete':
        model.con.tika.url_abstract.remove({'_id':ObjectId(theid)})
        return HttpResponseRedirect("/console/urls/")
    

def showUrl(request):        
   
    theid = request.GET['_id']
    print "#######:",theid
  
    if request.GET['update']=='show':
        
        item = model.con.tika.url_abstract.find_one({'_id':ObjectId(theid)}) 
        html= item['content']  
        return HttpResponse(html) 
    
    

    
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

def getlistAndtag(request,page=1): 
    keyword =  ""
    tag = ""
    
    if 'keyword' in request.GET and request.GET['keyword']: 
        keyword = request.GET['keyword']
        
        
      
    if 'tag' in request.GET and request.GET['tag']:
        
        tag = ";".join(request.GET['tag'].split("_"))
        
    if keyword is "" and tag is "":
        item_list =model.con.Url_abstract.find({"images":{"$ne":[]}}).sort('time', pymongo.DESCENDING)
    if keyword is "" and tag is not "":
        item_list =model.con.Url_abstract.find({"images":{"$ne":[]},"sogou_class":{'$regex': tag}}).sort('time', pymongo.DESCENDING)
    if keyword is not "" and tag is  "":
        re_keyword = keyword.replace(" ","|")
        item_list =model.con.Url_abstract.find({"images":{"$ne":[]},"sogou_class":{'$regex': tag}, "title": {'$regex': re_keyword}}).sort('time', pymongo.DESCENDING)
     
        #item_list =model.con.Url_abstract.find({"images":{"$ne":[]},"title": {'$regex': re_keyword}}).sort('time', pymongo.DESCENDING)
    if keyword is not "" and tag is not "":
        re_keyword = keyword.replace(" ","|")
        item_list =model.con.Url_abstract.find({"images":{"$ne":[]},"sogou_class":{'$regex': tag},"title":{'$regex': re_keyword}}).sort('time', pymongo.DESCENDING)
   
    return item_list,tag,keyword

 

def showPage(request,page=1): 
    '''
           现在对应url是/console/showPage/
    '''
    item_list,tag,keyword = getlistAndtag(request,page)
    items=paging(request,item_list,page,24) 
    
    return render_to_response('show.html', RequestContext(request,{ 'items': items,'tag':tag,'keyword':keyword}))

 

def getlistForScroll(request,page=2): 
    '''
    showpage滚动到最后，ajax加载新数据调用,调用是最早的page应该从2开始
    thumbnail_list.html 是ajax加载新数据时候先渲染这个模板然后加到show.html的每个thumbnails后面 ，
          平均加，现在4列thumbnails，一次加载24个，每个平均6个
         
    '''
    item_list,tag,keyword = getlistAndtag(request,page)
    items=paging(request,item_list,page,24) 
    startindex = 24*(int(page)-1)
    return render_to_response('thumbnail_list.html', { 'items': items,'tag':tag,'keyword':keyword,'startindex':startindex})
 
 


import simplejson

def getReadModal(request, id):
    '''
            单个文章content去渲染readmodal.html，readmodal.html就是bootstrap modal，div里面的内容
    '''
      
    # item = model.con.tika.url_abstract.find_one({'_id':ObjectId(id)})
    item = model.con.tika.url_abstract.find_one({'_id':ObjectId(id)})
  
     
    return render_to_response('readmodal.html', {'item': item})  


def getinfo(request, id):
    '''
          返回单个文章json，现在没用到
            
    '''
    
    item = model.con.tika.url_abstract.find_one({'_id':ObjectId(id)})
    content = item['content']
         
    
    j = {'title':item['title'],'id':id,'content':content}
     
    return HttpResponse(simplejson.dumps(j))

def showPageForWeibo(request,page=1): 
    print request
#    item_list,tag,keyword = getlistAndtag(request,page)
#    items=paging(request,item_list,page,24)  
#    return render_to_response('show.html', RequestContext(request,{ 'items': items,'tag':tag,'keyword':keyword}))
    return HttpResponse("2222")
  
 
    
