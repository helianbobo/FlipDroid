from django.http import HttpResponse,HttpResponseRedirect
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.shortcuts import render_to_response
import pymongo
import model
import time
ISOTIMEFORMAT='%Y-%m-%d %X'

PASSWD="qqwwee"
SESSIONID="user"

def islogin(func):
    def fun(request):
        #if args[].session['userid'] <> "user":
        if request.session.get('userid')=="user":
            return func(request)
        return HttpResponseRedirect('/')
    return fun


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



@islogin
def showSource(request):
    item_list = [item for item in model.con.Source.find()]
    items = paging(request,item_list) 
    return render_to_response('source.html', {'items': items})

@islogin
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
    
 
@islogin
def showUrls(request):
    #item_list = [item for item in model.con.Url_abstract.find()]
    #import re  
    #q      = re.compile(r'.*')
    #item_list = [item for item in model.con.Url_abstract.find( {'url' : { '$regex' : q } } )]
    
    item_list =model.con.Url_abstract.find().sort('time', pymongo.DESCENDING)
    items=paging(request,item_list) 
    return render_to_response('urls.html', { 'items': items})

@islogin
def updateUrls(request):        
   
    theid = request.GET['_id']
  
    if request.GET['update']=='show':
        item = model.con.tika.url_abstract.find({'_id':pymongo.objectid.ObjectId(theid)})
        html = item[0]['content']
        return HttpResponse(html)

    if request.GET['update']=='delete':
        model.con.tika.url_abstract.remove({'_id':pymongo.objectid.ObjectId(theid)})
        return HttpResponseRedirect("/urls/")
    
    

    
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
            return HttpResponseRedirect('/source/')
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

 
    