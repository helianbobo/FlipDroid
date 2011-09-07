# -*- coding: utf-8 -*- 
'''
Created on 2011-8-16

@author: shang
'''
from tcc import *

import time,signal,os  
import sys  
import config
import dbtools


class Watcher:  
    def __init__(self):  
        self.child = os.fork()  
        if self.child == 0:  
            return  
        else:  
            self.watch()  
    def watch(self):  
        try:  
            os.wait()  
        except KeyboardInterrupt:  
            print 'KeyBoardInterrupt'  
            self.kill()  
        sys.exit()  
  
    def kill(self):  
        try:  
            os.kill(self.child, signal.SIGKILL)  
        except OSError:
            pass  
def runTcc():
    a=TCC()
    a.start()

import platform
if __name__ == '__main__':
    #dbtools.test_save_to_mdb()
    import platform
    print platform.system()
    #if not platform.system() is "Windows":
        #Watcher()	
    runTcc()
