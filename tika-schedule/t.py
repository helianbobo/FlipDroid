# -*- coding: utf-8 -*-
from timeit import Timer
import dbtools

def test():
    import os
    #os.system("python t1.py")
    os.system("python tcc.py")


if __name__=='__main__':
    
    dbtools.test_save_to_mdb()
    t1=Timer("test()","from __main__ import test")
    print "*"*5,t1.timeit(1)

    
