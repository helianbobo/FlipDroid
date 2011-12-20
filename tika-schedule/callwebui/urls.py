# -*- coding: utf-8 -*-  
from django.conf.urls.defaults import patterns, include, url
import views
 

#from django.http import HttpResponse
from django.http import HttpResponse
def print_num(request,page):
    """ test  """
    return HttpResponse(page) 
urlpatterns = patterns('',
    ('^source/$', views.showSource),
    (r'^source/(?P<page>\d+)', views.showSource),
    ('^source/updatesource', views.updateSource),
    
    ('^urls/$', views.showUrls),
    (r'^urls/(?P<page>\d+)', views.showUrls),
    ('^urls/updateurls', views.updateUrls),
    ('^urls/search',views.searchUrls),
    ('^recommendsource/$',views.showRecommendSource),
    (r'^recommendsource/(?P<page>\d+)',views.showRecommendSource),
    ('^recommendsource/updatesource', views.updateRecommendSourceSource),
    ('^test/', views.test),
    ('^login/$', views.login),
     
    ('$', views.loginPage),
     

    
 
)
 