package de.l3s.boilerpipe.demo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

/**
 * Demonstrates how to use Boilerpipe to get the main content, highlighted as
 * HTML.
 *
 * @author Christian Kohlschütter
 * @see Oneliner if you only need the plain text.
 */
public class HTMLHighlightDemo {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://www.ifanr.com/49360");

        final BoilerpipeExtractor extractor = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
        final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstanceForChinese();

        String process = hh.process(url, extractor);
        System.out.println(process);

//        String a = "<img src=http://www.ifanr.com/wp-content/uploads/2011/08/4101586572_6a33082f25.jpg/><p>RIM 现在的状况很尴尬，市场份额急转直下，盈利告急。有用户抱怨：为什么系统的应用迟迟达不到 iOS 的丰富程度，对 Android 程序的兼容计划也没有实质性成功。但，BlackBerry 和 iPhone 或者 Android 手机毕竟是不同，难道除了一味借鉴竞争对手就没有别的出路吗？理论上而言，我觉得不一定。</p><p>于是我胆大妄为地编撰了以下两则预言，以表达我个人对 BlackBerry 的支持。</p><p>预言一 RIM 进入高档手工手机市场</p><p>2015年8月4日</p><p>结束了和苏富比拍卖行合作的夏季拍卖会后，唯一一支采用月球铁矿石作为原料的 BlackBerry 01 被来自中国的富商匿名拍走，成交金额为 1000万美元。相比起拍价 100万美元，有十倍的涨幅，RIM 公司表示：采用外星稀有材料的 BlackBerry 0x 系列还将继续通过拍卖渠道供给收藏家们。来自苏富比拍卖行的官方发言人表示：和 RIM 公司的合作是富有创意的，手机将成为未来投资者的新宠儿。</p><p>同天下午，上海新天地百达翡丽专卖店附近的 RIM 旗舰店开张，这是继第五大道、迪拜、银座旗舰店之后的又一家旗舰店。店长正在接受电视台采访：来自中国的消费者可以莅临上海旗舰店，近距离感受由手工打造的全系列 RIM 手机，跨洋购买已经成为历史。</p><p>新的财报显示，RIM 公司的全年订单已经达到饱和，这家手工高档手机制造商自2012年从智能市场隐退后再一次成为加拿大市值最高的公司。</p><p>我的观点：</p><p>曾经有段时间大家都希望有一支电子手表：带秒表的，带闹钟的，带温度计的，带高度计的，带湿度计的，带GPS的，太阳能的，Casio 的，SWATCH 的。现在，如果你真的有钱，你会想要一支手表：带钻石的，铂金的，机械的，手工制造的，瑞士的，劳力士的，百达翡丽的。</p><p>RIM 可以不去做 Casio ，SWATCH，那么就要去争取做劳力士，百达翡丽。要学习从更少的用户手里赚到更多的钱否则，抱歉你会退败成一家小公司。</p><p>预言二  RIM 进入电子支付市场</p><p>2015年8月4日</p><p>这个月公司的收益不错，董事会后我决定向每位员工发放1000美金的奖励，途径？当然是著名的 RIM 金融快线，员工们很快就能拿着这笔钱去购买最新一代的 iPhone 8 了。</p><p>两年前因为千万名越狱用户银行账户被盗事件，Android 和 iOS 设备作为电子支付工具受到了各大银行的抵制，而之前默默耕耘在这一领域的 RIM 公司获得了极大的认可。安全性突出，制造工艺优秀的 BlackBerry 牌电子钱包成为了这个市场的霸者。可能我忘记介绍了：人们已经放弃手机支付这个古怪的途径了，因为近年来连续走高的硬件配置和停止不前的电池技术导致了用户们经常抱怨 “没电=没钱” ，这极大的伤害了银行家们进一步和手机商合作的热情，转而寻求一个独立装置。</p><p>新版本的 BlackBerry Money Carrier 800 外形和之前的 Blod 系列很像。家庭主妇们对其键盘手感赞赏有加，纷纷表示是购物时计算费用的好帮手，白领们则喜欢用它的全键盘输入购物心情，快速分享到社交平台。中国工商银行，中国移动，大众点评共同推出的联名版 Carrier 800 免去了 100元/月的管理费，实际售价仅仅 128元/月（需要签订2年协议），用户申领的热情很高。</p><p>由于 BlackBerry Money Service 深度整合了各类支付渠道，公共事业费，手机充值，消费积分兑换，特价团购，现在只需要轻击按键就能完成。而数字化的信用卡服务，能让用户在使用 NFC 支付时选择使用哪张信用卡。方便之处，不言而喻。</p><p>我的观点：</p><p>我们都有被父母责备的时候：为什么数学不如小张好，为什么英文不如小李棒。但成功者告诉我们，了解自己的长处，并利用好这个优势才是你生命的真谛。</p><p>姚明如果执意要在数学上和小张死磕而不去练球，我估计他也不会在 NBA 获得成功。同样的，RIM 需要在除了安全领域以外的其他地方和对手死磕吗？</p>";
//        List<String> result = toParagraph(a);
//        System.out.println(result.size());

//        System.out.println(a.replaceAll("<img.+?>",""));
    }






}
