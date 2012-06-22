# -*- coding: utf-8 -*-  
'''
Created on 2012-6-20

@author: shang
'''

from django import template

from django.template import Context, Template, loader, resolve_variable
register = template.Library()
 
@register.filter(name = "mongo_id")
def mongo_id(value):
    return value['_id']