import junit.framework.TestCase;
import org.junit.Test;

public class ChineseWordCutterTest extends TestCase {
    @Test
    public void testCut(){
        String[] result = ChineseWordCutter.getInstance().cut("最近从英国传来了一则坏消息，给风头正劲的Spotify等流媒体音乐服务来了一个大大的意外。英国数字内容与唱片发行管理公司STHoldings近日宣布，将把旗下代理的200多家唱片发行机构的全部音乐内容撤出Spotify, Simfy, Rdio和Napster。\n" +
                "\n" +
                "经过STHoldings与唱片公司的协商，STHoldings将把代理的234家唱片公司的音乐内容撤出数字音乐服务，只有另外4家唱片公司表达了继续与数字音乐公司合作的意愿。对受影响的4家音乐服务来说，反应最大的应该是Spotify。Spotify的总部位于英国伦敦，而且英国还是Spotify正式迈向全球发展之路的第一站。对已经成功登录美国，已向欧洲11个国家开放服务的Spotify来说，此次的“后院起火”绝对是一个大大的意外。\n" +
                "\n" +
                "根据STHoldings发布的公告，STHoldings认为，数字音乐服务带来的音乐销售分成十分有限，并且无形之中降低了唱片本身的价值。由于数字音乐服务上的音乐标价过低，使得音乐创作者和唱片公司感觉唱片本身的价值被低估，带来的收益远远低于合作之前的预计。从某种意义上来说，STHoldings这种看似“反潮流”的举动并不是一时的鲁莽之举。自数字音乐兴起以来，传统音乐出版业一直有这样的认识：数字音乐创造的巨大收入只有很小的一部分流入了音乐创作者手中。最典型的例子，Lady Gaga的那首红得发紫的“Poker Face”在Spotify上播放了100万次后，Spotify付给Lady Gaga的分成只有可怜的167美元。\n" +
                "\n" +
                "下面说说我的分析，STHoldings撤离音乐服务的原因还可能与传统音乐出版业对数字音乐服务的过高期望有关。在传统音乐出版业者看来，作为内容的提供者，他们理应获得最多的收入。此外，音乐创作者也希望数字音乐强大的传播力量来推广自己，聚集人气。尽管数字音乐正版化开始走向正规，但不可否认，盗版音乐的存在依旧给音乐出版方和创作者带来了巨大的损失。当然，数字音乐的过低定价也让音乐出版方难以忍受。这种期望心理与现实的落差，导致了STHoldings做出这样的“疯狂”举动。\n" +
                "\n" +
                "这则坏消息再次提醒了所有的数字内容服务提供商：如何处理和内容创造者的关系依旧是一个需要仔细考量的问题，合理安排利益分成并不容易。");

        for (int i = 0; i < result.length; i++) {
            String s = result[i];
            System.out.println(s);
        }
    }
}