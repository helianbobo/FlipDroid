import linksmanager
import rssmanager,dbtools
import linksmanager2
'''
def test1():
    lm = linksmanager.LinksManager()
    rm = rssmanager.RssManager()
    rm.TESTREPEAT=1
    rm.handleLinksFromAllRss(lm)
    print "***"*3,"allnewlinksnum"
    print rm.alllinksnum
    print
'''
    
def test2():
    lm = linksmanager.LinksManagerUseCoroutine()
    lm.start()
    rm = rssmanager.RssManager()
    rm.TESTREPEAT=1
    rm.handleLinksFromAllRss(lm)
    #lm.close()
    print "***"*3,"allnewlinksnum"
    print rm.alllinksnum
    print "all over!!!!!!!!!!"




if __name__=='__main__':
    
    test2()
