# -*- coding: utf-8 -*-  
from django.conf.urls.defaults import patterns, include, url
import views
 

#from django.http import HttpResponse
from django.http import HttpResponse
def print_num(request,page):
    """ test  """
    return HttpResponse(page) 
urlpatterns = patterns('',
    ('^console/source/$', views.showSource),
    (r'^console/source/(?P<page>\d+)', views.showSource),
    ('^console/source/updatesource', views.updateSource),
    
    ('^console/urls/$', views.showUrls),
    (r'^console/urls/(?P<page>\d+)', views.showUrls),
    ('^console/urls/updateurls', views.updateUrls),
    ('^console/urls/search',views.searchUrls),
    ('^console/recommendsource/$',views.showRecommendSource),
    (r'^console/recommendsource/(?P<page>\d+)',views.showRecommendSource),
    ('^console/recommendsource/updatesource', views.updateRecommendSourceSource),
    ('^console/test/', views.test),
    ('^console/login/$', views.login),
     
    ('^console/$', views.loginPage),
     

    
 
)
 