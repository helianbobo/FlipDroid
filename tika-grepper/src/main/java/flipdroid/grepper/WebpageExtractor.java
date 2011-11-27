package flipdroid.grepper;

import flipdroid.grepper.extractor.Extractor;
import flipdroid.grepper.extractor.ExtractorException;
import flipdroid.grepper.extractor.pipe.*;
import it.tika.mongodb.image.ImageService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 11:45 AM
 * <p/>
 * To change this template use File | Settings | File Templates.
 */
public class WebpageExtractor {
    private List<Extractor> extractors;

    public WebpageExtractor(ImageService tikaImageService) {
        extractors = new ArrayList<Extractor>();
        GrepperTitleExtractor titleExtractor = new GrepperTitleExtractor();
        titleExtractor.setTitleExtractor(new TitleExtractorImpl());

        GrepperContentExtractor grepperContentExtractor = new GrepperContentExtractor();
        grepperContentExtractor.setExtractor(new PipeGrepperContentExtractor());

        extractors.add(titleExtractor);
        extractors.add(grepperContentExtractor);
        extractors.add(new ImageFilter(tikaImageService));

    }

    public URLAbstract extract(URLAbstract urlAbstract) {
        if (urlAbstract == null || urlAbstract.getRawContent() == null || urlAbstract.getRawContent().length == 0) {
            return urlAbstract;
        }
        if (extractors == null || extractors.isEmpty()) {
            return urlAbstract;
        }
        try {
            for (Extractor extractor : extractors) {
                extractor.extract(urlAbstract);
            }
        } catch (ExtractorException e) {
            e.printStackTrace();
            return null;
        }
        String title = urlAbstract.getTitle();
        String content = urlAbstract.getContent();

        if (title != null)
            if (content.length() > title.length() * 2 && content.indexOf(title) != -1) {
                urlAbstract.setContent(content.replaceFirst(title.replace("\\","\\\\").replace(".","\\.").replace("?","\\?").replace(")","\\)").replace("(","\\(").replace("$","\\$"), ""));
            }


        return urlAbstract;
    }

    public static final Pattern PAT_TAG_NO_TEXT = Pattern.compile("<[^/][^>]*></[^>]*>");
   public static final Pattern PAT_SUPER_TAG = Pattern.compile("^<[^>]*>(<.*?>)</[^>]*>$");
    private static final Pattern PAT_TAG_A_NO_TEXT = Pattern.compile("<a [^>]*?></a>");

    public static void main(String[] args) {
        String html = "<a href=#blkBreadcrumb></a><p><a href=http://www.sina.com.cn/></a></p><p><a href=http://news.sina.com.cn/></a></p><p><a href=http://sports.sina.com.cn/></a></p><p><a href=http://ent.sina.com.cn/></a></p><p><a href=http://finance.sina.com.cn/></a></p><p><a href=http://finance.sina.com.cn/stock/></a></p><p><a href=http://tech.sina.com.cn/></a></p><p><a href=http://blog.sina.com.cn/></a></p><p><a href=http://weibo.com/></a></p><p><a href=http://video.sina.com.cn/></a></p><p><a href=http://v.sina.com.cn/></a></p><p><a href=http://auto.sina.com.cn/></a></p><p><a href=http://house.sina.com.cn/></a></p><p><a href=http://games.sina.com.cn/></a></p><p><a href=http://eladies.sina.com.cn/></a></p><p><a href=http://book.sina.com.cn/></a></p><p><a href=http://edu.sina.com.cn/></a></p><p><a href=http://astro.sina.com.cn/></a></p><p><a href=http://weather.news.sina.com.cn/></a></p><p><a href=http://sms.sina.com.cn/></a></p><p><a href=http://mail.sina.com.cn/></a></p><p><a href=http://news.sina.com.cn/guide/></a></p><p><a href=https://login.sina.com.cn/></a></p><p><a href=https://login.sina.com.cn/sso/logout.php></a></p><a href=null></a><a href=#Main></a><h1><a href=http://sports.sina.com.cn/><img src=http://i3.sinaimg.cn/ty/main/logo/logo_home_sports_nonike.gif >hack</img></a></h1><p><a href=http://sports.sina.com.cn/></a><a href=http://sports.sina.com.cn/nba/index.shtml></a><a href=http://nba.sports.sina.com.cn/team/Lakers.shtml></a></p><a href=null></a><a href=http://sports.sina.com.cn></a><a href=http://sports.sina.com.cn></a><a href=http://weibo.com/sportschannel?zwm=sports></a><p>　　新浪体育讯　虽然临时协议达成，但对洛杉矶湖人<a href=http://weibo.com/lakersnews?zw=sports>(微博)</a>来说，他们的麻烦才刚刚开始。在洛杉矶当地媒体《洛杉矶时报》看来，湖人从阵容到教练组，都可以说是麻烦一堆。</p><p>　　33岁的科比-布莱恩特、31岁的保罗-加索尔、32岁的拉玛尔-奥多姆和24岁的安德鲁-拜纳姆<a href=http://weibo.com/andrewbynum?zw=sports>(微博)</a>，这四位是湖人最核心的家伙，但这四位的合同都将在两年内执行完毕。特别是奥多姆和拜纳姆，一个是万金油，一个是先发内线，合同都会在新赛季后就结束。对湖人来说，如果说这是个困扰，那好歹还有一年的潜伏期。</p><p><strong>相对来说，湖人现在的后场麻烦更大。德里克-费舍尔37岁了，还能指望他干嘛？史蒂夫-布雷克上赛季表现并不好，他有理由被换掉。湖人现在最希望的，大概就是骑士赶紧裁掉拜伦-戴维斯<a href=http://weibo.com/stevesnooker?zw=sports>(微博)</a>吧。此外，最好奇才还能裁掉拉沙德-刘易斯。这两人，虽然顶着高薪低能的恶名，但若能加盟湖人，都能起到立竿见影的效果。不过一位专家也暗示，这两人的年薪都已经超过千万，湖人想得到他们并不容易。</strong></p><p>　　湖人现在的薪金总额高达9000万美金，远超工资帽。在自由球员市场上，他们可用的就是中产特例了，一个大幅度缩水的签约工具。此外，湖人还有一个办法，那就是用升级版特赦令。不过，裁掉卢克-沃顿还是慈世平？这是个问题！</p><p>　　31岁的沃顿，合同上还剩2年1150万美金，32岁的慈世平还剩3年2150万美金。沃顿上赛季54场场均1.7分，慈世平数据稍好但场均8.5分也是生涯最低了。可以说，这两位都有被裁员的理由。但是，对杰里-巴斯来说，无论他裁掉哪位，该付的钱还是免不掉的。</p><p>　　在阵容之外，湖人另一大头疼的问题就是教练组。新上任的迈克-布朗铁定会抛弃在洛杉矶实行了十来年的三角进攻战术了，布朗的战术喜好从来都是以中锋唱主角的。不幸的是，尽管赛季缩水，但拜纳姆的五场停赛是逃不过的。上赛季季后赛，在和小牛的比赛中，拜纳姆因为不冷静而吃到联盟罚单。也就是说，新赛季前五场，对布朗是个残酷的考验。即便拜纳姆回归，他的不定时发作的伤病，对布朗的应变能力也是挑战，这还没算上其他球员能不能适应布朗的新战术呢。</p><p>　　所以，无论怎么看，湖人的新赛季都不会是一片坦途。对紫金军来说，想要洗刷上赛季的耻辱。从主帅到球员，再加上总经理，没有任何一个人可以掉以轻心。</p><p>　　(XWT185)</p><a href=javascript:;></a><a href=javascript:;></a><a href=javascript:;></a><a href=http://sports.sina.com.cn/nba_in_wap.html><img src=http://i1.sinaimg.cn/ty/3g/nbaphone.GIF >hack</img></a><a href=http://sports.sina.com.cn/nba_in_wap.html></a><br/><a href=http://mynba.sports.sina.com.cn/></a><a href=http://sports.sina.com.cn/l/basketball/?agentId=214473></a><a href=http://sports.sina.com.cn/l/2caipiao/jclqrf/></a><a href=http://lottery.sina.2caipiao.com/member/register.jhtml></a><a href=http://www.google.com.hk/webhp?lr=&client=aff-sina&ie=gb&oe=utf8&hl=zh-CN&channel=contentrelatedsearch></a><a href=http://comment4.news.sina.com.cn/comment/skin/feedback.html></a><a href=javascript:SinaSavePage.save();></a><a href=javascript:LoadFullViewJs();></a><a href=javascript:doZoom(16)></a><a href=javascript:doZoom(14)></a><a href=javascript:doZoom(12)></a><a href=javascript:LoadPrintJs();></a><a href=javascript:window.close()></a><a href=http://sports.sina.com.cn/></a><h2><a href=http://sports.sina.com.cn/nba/></a></h2><h2><a href=http://search.sina.com.cn/?from=bottomkeywords&c=news&q=%BA%FE%C8%CB&range=all></a></h2><p><a href=http://sports.sina.com.cn/k/2011-11-27/00435845243.shtml></a></p><p><a href=http://sports.sina.com.cn/k/2011-11-26/18505845015.shtml></a></p><p><a href=http://sports.sina.com.cn/k/2011-11-26/13545844753.shtml></a></p><p><a href=http://sports.sina.com.cn/k/2011-11-26/12025844677.shtml></a></p><p><a href=http://blog.sina.com.cn/s/blog_5dc2e7360102dvgo.html?tj=1></a></p><p><a href=http://sms.sina.com.cn/magazine/mms/news.html></a><a href=http://sms.sina.com.cn/magazine/mms/pretty.html></a></p><p><a href=http://sms.sina.com.cn/magazine/mms/entertainments.html></a></p><p><a href=http://diy.sina.com.cn/cardshow.php></a><a href=http://mgame.sina.com/web/index.php></a></p><p><a href=http://sms.sina.com.cn/xuyuan.html></a><a href=http://sms.sina.com.cn/magazine/mms/jokes.html></a></p><p><a href=http://sms.sina.com.cn/xuyuan.html></a><a href=http://sms.sina.com.cn/xuyuan.html></a></p><p><a href=http://diy.sina.com.cn/cardshow.php?cid=4></a><a href=http://bf.sina.com.cn/sinarc_php/piclist.php?from=464&aid=113&type=500></a></p><p><a href=http://sms.sina.com.cn/mobiledo/theme/index.php></a><a href=http://sms.sina.com.cn/mobiledo/theme/jingpin.php?from=0&tagid=2></a><a href=http://sms.sina.com.cn/mobiledo/theme/jingpin.php?from=0&tagid=6></a></p><p><a href=http://sms.sina.com.cn/mobiledo/theme/jingpin.php?from=0&tagid=3></a><a href=http://sms.sina.com.cn/mobiledo/theme/jingpin.php?from=0&tagid=1></a><a href=http://sms.sina.com.cn/mobiledo/theme/jingpin.php?from=0&tagid=0></a></p><p><a href=http://bf.sina.com.cn></a><a href=http://bf.sina.com.cn/sinarc_php/piclist.php?from=464&aid=114&type=773></a></p><p><a href=http://bf.sina.com.cn/rng/1322_28100852_0.html></a><a href=http://bf.sina.com.cn/rng/1322_28108258_0.html></a></p><p><a href=http://sms.sina.com.cn/act/091222/xixin.html></a><a href=http://sms.sina.com.cn/shenfenzhengchaxun.html></a></p><p><a href=http://sms.sina.com.cn/tongmingtongxing.html></a><a href=http://bf.sina.com.cn/newbf/zt/ysqm.html></a></p><p><a href=http://bf.sina.com.cn/sinarc_php/yycqlist.php></a><a href=http://bf.sina.com.cn/newbf/zt/ysqm.html></a></p><p><a href=http://diy.sina.com.cn/cardshow.php?cid=2></a><a href=http://sms.sina.com.cn/xuyuan.html></a></p><p><a href=http://sms.sina.com.cn/></a><a href=http://weibo.com/1659151753/profile/></a></p><p><a href=http://mgame.sina.com/web/index.php/game/show/300019></a><a href=http://mgame.sina.com/web/index.php/game/show/300115></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-03/095130141.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-03/100130146.html></a></p><p><a href=http://gongyi.sina.com.cn/greenlife/2011-10-03/100730148.html></a></p><p><a href=http://gongyi.sina.com.cn/greenlife/2011-10-03/100930149.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-05/092430152.html></a></p><p><a href=http://gongyi.sina.com.cn/greenlife/2011-10-08/094030163.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-03/101630151.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-08/092430162.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-08/114130173.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-09/095330180.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-09/095930182.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-09/103930189.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/p/2011-10-09/110230191.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-08/105430169.html></a></p><p><a href=http://gongyi.sina.com.cn/gyzx/2011-10-08/092330161.html></a></p><p><a href=http://auto.sina.com.cn/guangzhouchezhan/></a></p><p><a href=http://city.finance.sina.com.cn/city/wlmp.html></a></p><p><a href=http://city.finance.sina.com.cn/city/dhcs.html></a></p><p><a href=http://mail.sina.net/daili/daili.htm></a></p><p><a href=http://www.sinanet.com></a></p><a href=http://sina.allyes.com/main/adfclick?db=sina&bid=145615,183791,188761&cid=0,0,0&sid=176456&advid=3406&camid=25022&show=ignore&url=http://play.sina.com.cn/></a><a href=http://v.sina.com.cn/v/z/hotvideo/index.html></a><p><a href=http://weibo.com/1798297245/xsGkNtfyQ></a></p><p><a href=http://weibo.com/z/wozai></a></p><p><a href=http://weibo.com/goldqilin></a></p><p><a href=http://talk.weibo.com/ft/201110172174></a></p><p><a href=http://talk.weibo.com/ft/201110172174></a></p><p><a href=http://finance.sina.com.cn/creditcard/></a></p><p><a href=http://finance.sina.com.cn/stock/index.shtml?c=spr_web_sina_zhengwen_fin_t001></a></p><p><a href=http://finance.sina.com.cn/calc/?c=spr_web_sina_zhengwen_fin_t002></a></p><p><a href=http://digi.sina.com.cn?c=spr_web_sina_zhengwen_tech_t001></a></p><p><a href=http://video.sina.com.cn/movie/?c=spr_web_sina_zhengwen_video_t001></a></p><p><a href=http://travel.sina.com.cn/places/?c=spr_web_sina_zhengwen_travel_t001></a></p><p><a href=http://baby.sina.com.cn/tools/?c=spr_web_sina_zhengwen_baby_t001></a></p><p><a href=http://data.auto.sina.com.cn/?c=spr_web_sina_zhengwen_auto_t001></a></p><p><a href=http://eladies.sina.com.cn/beauty/chanpinku/?c=spr_web_sina_zhengwen_lady_t001></a></p><p><a href=http://astro.sina.com.cn/luck/?c=spr_web_sina_zhengwen_astro_t001></a></p><p><a href=http://yingxun.ent.sina.com.cn/?c=spr_web_sina_zhengwen_ent_t001></a></p><p><a href=http://tvguide.ent.sina.com.cn/channel/1/cctv1/wed.html?c=spr_web_sina_zhengwen_ent_t002></a></p><p><a href=http://edu.sina.com.cn/college/index.shtml?c=spr_web_sina_zhengwen_edu_t001></a></p><p><a href=http://corp.sina.com.cn/chn/></a><a href=http://corp.sina.com.cn/eng/></a><a href=http://emarketing.sina.com.cn/></a><a href=http://www.sina.com.cn/contactus.html></a><a href=http://corp.sina.com.cn/chn/sina_job.html></a><a href=http://www.sina.com.cn/intro/lawfirm.shtml></a><a href=http://english.sina.com/></a><a href=http://members.sina.com.cn/apply/></a><a href=http://help.sina.com.cn/></a></p><p><a href=http://www.sina.com.cn/intro/copyright.shtml></a></p>";
        boolean repeat = true;
        while (repeat) {
            repeat = false;
            Matcher m = PAT_TAG_A_NO_TEXT.matcher(html);
            if (m.find()) {
                repeat = true;
                html = m.replaceAll("");
            }

        }
        System.out.println(html);
    }
}
