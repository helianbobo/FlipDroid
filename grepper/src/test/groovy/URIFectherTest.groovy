import flipdroid.grepper.EncodingDetector
import flipdroid.grepper.FruitFetcher
import flipdroid.grepper.GpathFromURIFetcher
import junit.framework.TestCase

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2/26/11
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
class URIFectherTest extends TestCase {
    def testMap = [
            'http://news.ifeng.com/world/special/zhongdongbianju/content-2/detail_2011_02/27/4876634_0.shtml': '联合国安理会一致通过制裁利比亚决议',
            'http://nba.tom.com/2009-07-13/000F/85982984.html': '麦迪下季将改披3号战袍 炒掉训练师决心重新起步',
            'http://www.mittrchinese.com/single.php?p=23998': '索爱游戏手机:创新还是模仿？',
            'http://sports.163.com/11/0227/11/6TT8HIJJ00051CA1.html': '休城三人得分20+火箭获3连胜 德隆15+17难救主',
            'http://news.xinhuanet.com/politics/2011-02/27/c_121126523.htm': '温家宝：将提高个税起征点 这是今年给百姓办的第一件实事',
            'http://news.ifeng.com/mainland/detail_2011_03/01/4912402_0.shtml': '媒体：个税起征点或提至3000元 暂定三年调一次',
            'http://sports.xinmin.cn/2011/02/27/9511889.html': '图文-[CBA第29轮]福建VS上海 刘炜单手轻松上篮',
            'http://realtime.zaobao.com/2011/02/110227_02.shtml': '奥巴马呼吁卡达菲立即下台',
            'http://www.yicai.com/news/2011/02/692990.html': '官员称中国资本市场应拓展广度和深度',
            'http://post.news.tom.com/28000ADD3532.html': '煤化工行业未来前景看好',
            'http://blog.sina.com.cn/s/blog_54b3675801017s05.html?tj=1': '加利亚尼飞吻献给谁？老兵诠释米兰精神！',
            'http://club.uhoop.tom.com/viewthread.php?tid=214666': '姚明配不配再续约火箭？',//回复此帖 leads
           'http://wcba.sports.tom.com/2011-03-01/0OQ6/48405224.html': '三分王全场最高几乎导演大冷门 八一客场险负云南',//陈楠单挑泰勒 leads
           'http://news.zol.com.cn/218/2182162.html': '联通3G上网卡市场火热 1月新增35万用户',  //相关阅读 trails
            'http://laoyaoba.com/ss6/?action-viewnews-itemid-176072': 'ipad2即将上市 盘点A股苹果迷(附股)',//老杳吧本周热点帖子 trails
            'http://tech.hexun.com/2011-02-26/127582009.html': '一山容二虎 无线上网卡VS无线局域网优劣谈(图)',
    ]

    public void testFetch() {

//        Map compareMap = new ConcurrentHashMap();
//        GParsPool.withPool {
//            testMap.eachParallel {url, title ->
//                def gpathFetcher = new GpathFromURIFetcher()
//                final detector = new EncodingDetector()
//                gpathFetcher.encodingDetector = detector
//                def htmlNode = gpathFetcher.fetch(url)
//                final titleNode = new FruitFetcher().fetch(htmlNode)
////                assertNotNull(title, titleNode)
////                assertEquals(title.trim(), titleNode.titleText)
//
//                def htmlDoc = HTMLFetcher.fetch(new URL(url), detector.detect(url));
//
//                final BoilerpipeExtractor extractorChinese;
//                final BoilerpipeExtractor extractor;
//                final HTMLHighlighter hhChinese;
//                final HTMLHighlighter hh;
//
////                boolean forChinese = true;
////                if (forChinese) {
//                    extractorChinese = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
//                    hhChinese = HTMLHighlighter.newExtractingInstanceForChinese();
////                } else {
//                    extractor = CommonExtractors.ARTICLE_EXTRACTOR;
//                    hh = HTMLHighlighter.newExtractingInstance();
////                }
//                String bodyTextPipeChinese = hhChinese.process(htmlDoc, extractorChinese);
//                String bodyTextPipe = hh.process(htmlDoc, extractor);
//
//                compareMap << [(titleNode.titleText.toString()) : "Original:\n"+bodyTextPipe +"\n"+"Optimized:\n"+bodyTextPipeChinese]
//            }
//        }
//        compareMap.each {k,v->
//            println k
//
//            println v
//        }

        def url = "http://www.ifanr.com/42005"
        def gpathFetcher = new GpathFromURIFetcher()
        final detector = new EncodingDetector()
        gpathFetcher.encodingDetector = detector
        def htmlNode = gpathFetcher.fetch(url)
        final titleNode = new FruitFetcher().fetch(htmlNode)
        println titleNode.titleText
    }
}
