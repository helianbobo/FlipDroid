# -*- coding: utf-8 -*-  
from django.conf.urls.defaults import patterns, include, url
import views
 

from django.http import HttpResponse
 
urlpatterns = patterns('',
    ('^source/$', views.showSource),
    ('^scorce/updatesource', views.updateSource),
    ('^urls/$', views.showUrls),
    
 
)