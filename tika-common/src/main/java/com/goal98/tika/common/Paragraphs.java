package com.goal98.tika.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/4/11
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class Paragraphs {


    List<TikaUIObject> paragraphs = new ArrayList<TikaUIObject>();

    public List<TikaUIObject> getParagraphs() {
        return paragraphs;
    }

    public String getImageSrc(String paragraph) {
        if (paragraph.startsWith("<img")) {
            return paragraph.substring(paragraph.indexOf("src=") + 4, paragraph.indexOf(ImageInfo.HACK_IMG));
        }
        return null;
    }

    public void toParagraph(String articleContent) {
        if (articleContent == null)
            return;
//        System.out.println(articleContent.length());
        int cutAt = 0;
        articleContent = parseImg(articleContent);
        while ((cutAt = findNextClosingTag(articleContent)) != -1) {
            if (cutAt == 0) {
                int endAt = articleContent.indexOf(">");
                articleContent = articleContent.substring(endAt + 1);
                continue;
            }
            int endAt = articleContent.indexOf(">", cutAt);
            String paragraph = articleContent.substring(0, endAt + 1);

            paragraph = parseImg(paragraph);

            paragraphs.add(new Text(paragraph));
            articleContent = articleContent.substring(endAt + 1);
        }
        articleContent = parseImg(articleContent);
    }
    static Pattern startTag = Pattern.compile("(<[^>]+>)|</[^>]+>");
    private int findNextClosingTag(String articleContent) {
//        articleContent = "<img src=http://www.ifanr.com/wp-content/uploads/avatar/1347.JPG >hack</img>";
        if(articleContent.trim().startsWith("</"))
            return 0;
        if(articleContent.trim().length()==0)
            return -1;



        Matcher startMatcher = startTag.matcher(articleContent);
        int level = 0;
        while (startMatcher.find()){
//            System.out.println(startMatcher.group());
            if(startMatcher.group().indexOf("</")==-1){
                level++;
            }else{
                level--;
            }
            if(level==0){
//                System.out.println(startMatcher.start());
                return startMatcher.start();
            }

        }
        throw new RuntimeException("not match");
    }

    private String parseImg(String paragraph) {
        while (paragraph.startsWith("<img")) {
            int endOfImg = paragraph.indexOf(ImageInfo.HACK_IMG);
            int endIndex = endOfImg + ImageInfo.HACK_IMG.length();
            final ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUnparsedInfo(paragraph.substring(0, endIndex));
            paragraphs.add(imageInfo);
            paragraph = paragraph.substring(endIndex);
        }
        return paragraph;
    }

    public void retain(List<String> filteredImages, Map<String, ImageInfo> imageInfoMap) {
        Iterator<TikaUIObject> iter = paragraphs.iterator();
        while (iter.hasNext()) {
            TikaUIObject tikaUIObject = iter.next();
            if (tikaUIObject.getType().equals(TikaUIObject.TYPE_IMAGE)) {
                final ImageInfo tikaImage = (ImageInfo) tikaUIObject;
                String src = tikaImage.getUrl();
                boolean found = false;
                for (int i = 0; i < filteredImages.size(); i++) {
                    String s = filteredImages.get(i);
                    if (s.indexOf(src) != -1) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    iter.remove();
                } else {
                    ImageInfo imageInfo = imageInfoMap.get(src);
                    if (imageInfo != null) {
                        tikaImage.setWidth(imageInfo.getWidth());
                        tikaImage.setHeight(imageInfo.getHeight());
                    } else {
                        iter.remove();
                    }
                }
            }
        }

    }

    public String toContent() {
        StringBuilder sb = new StringBuilder();
        Iterator<TikaUIObject> iter = paragraphs.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next().getOutput());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Paragraphs p = new Paragraphs();
        p.toParagraph("<img src=http://www.ifanr.com/wp-content/uploads/2011/08/App-Store-001.jpg >hack</img><p><STRONG>ifanr</STRONG><STRONG>：请介绍一下你对苹果应用市场（</STRONG><STRONG>Apple App Store</STRONG><STRONG>）发展过程、现状和趋势的看法。</STRONG></p><p>洪亮：Apple App Store发展到现在已经有40多万款应用，期间经历了许多变化，但总体趋势是一直在规范化的过程中。一个应用（产品）如何在众多的应用中脱颖而出，现在的难度越来越大。回顾在2007年，一款现在看起来很普通的应用，很容易就出现在排行榜的高位上，那时可以说是蓝海，而现在已经接近成为红海市场了。</p><p>但它依然是开发和创业者最好的平台，Apple App Store不断规范市场规则，提升应用品质，使得更好的产品出现在用户面前。</p><p>Apple App Store的封闭也有好处，就是开发者在一个公平的平台上展开竞争。当然随着竞争的加剧，对于个人工作室、小型开发团队来说，再出一款红遍全球的《愤怒的小鸟》的可能性虽然还有，但确实已经越来越小了。</p><p><STRONG>ifanr</STRONG><STRONG>：我们知道在</STRONG><STRONG>Apple App Store</STRONG><STRONG>的平台上，开发、推广产品并获得盈利是一个复杂的工程，这其中有哪些经验可以分享？</STRONG></p><p>洪亮：是的，我相信许多iPhone应用开发者都希望自己能开发出一款《愤怒的小鸟》。但现实是，许多应用在上线之后，获得了几百、几千的下载，然后就沦落到了Apple App Store几十万款应用的汪洋大海之中，难得再有发光之时。</p><p>我从一款应用的设计开发、推广运营的时间顺序上梳理一下大家应当注意的关键点，最后再总结一下对待Apple App Store的基本理念。</p><p><STRONG>产品设计必须重视市场/文化差异</STRONG></p><p>产品在开发之前必须要有很好的定位，这个定位不仅要清晰，而且要注意目标市场和用户的差异。无论对游戏还是工具软件，都可能会遇到文化差异或是地域特征的问题。</p><p>具体情况要做细致的分析，比如叫《三国》的游戏，在中国就很容易得到用户的认可，但如果到了美国市场就会下载者寥寥。</p><p>不仅在文化层面的差异要注意，在心理和习惯上的差异也同样需要重视。比如我自己在2009年开发的一款名为《帮我做决定》（英文名称是iDecide）的应用。2009年9月在美国市场上线，但一直反应不佳，后来尝试免费，效果依然不好，当天全球只有2000不到的下载。而这个产品到了中国市场，就打入中国总榜前30名。</p><p>事后分析原因时发现，中国人在遇到选择时会犹豫不决，这款应用正好可以起到帮助作用。而美国人很少有这样的情况，所以习惯差异最终导致了这款应用在中国和美国市场不同的境遇。</p><p><STRONG>第一印象：产品名字和图标的重要性</STRONG></p><p>产品的第一印象包括名字、图标、截图和说明，而这些是用户是否下载的决定因素。</p><p>首先是名字和图标，用户在方寸之间的手机屏幕上看到许多应用软件，这时如何让给你的软件在用户的视线中跳出来，就要看名字和图标的设计功力了。</p><p>在美国Apple App Store中有一个产品叫10 Balls 7 Cups，并不出众，但当它改名为Skee-Ball之后，就蹿升到了美国游戏排名第一。如果你了解美国生活，就会发现Skee-Ball是个在欧美盛行的街机游戏，所以当人们看到这个名字，自然就知道这个产品是什么，也就顺理成章地下载、开始玩起来。</p><p>这个例子就说明了名字的重要性，原来的名字10 Balls 7 Cups，虽然说明了游戏方式，但是让人感觉含糊。类似例子在中国市场中有《捕鱼达人》，在中国的游戏厅、大型商场里，都会有抓取毛绒玩具的游戏机，因此当大家看到这样的应用时，自然知道这个游戏的大致情况。</p><p>题材也是重要的因素，命名时选用大家知晓的名称，更容易吸引用户的注意和理解。比如说《三国塔防》，中国的游戏玩家对三国、塔防都很熟悉，通过名字就对游戏有个大致的了解，容易使得用户下载。</p><p>图标设计的要特别精彩，第一要抢眼，要让你的图标在屏幕中最突出；但同时也要让用户能够理解你的应用，将这两者结合在一起的图标才是完美的设计。</p><p>在产品名称和图标之后，用户就会看到截图和文字说明。截图也是很关键的项目，要能把你的产品特性一目了然说出来，好的产品不会只是简单地放置手机截图，而是经过专业的PS处理，把产品的主要功能、最炫的部分集中呈现出来，吸引用户的下载欲望。文字说明也要写的足够吸引人，足够有诱惑力。如果是英文版，一定要避免语法错误。</p><p><STRONG>产品预热和发布：许多开发者忽视的重要工作</STRONG></p><p>许多开发者不重视产品的预热，甚至没有意识到还有预热这件事，这是非常令人遗憾的事情。在上线之前，不论大公司还是小工作室的产品，都应该做些预热推广工作，当然有足够资金的前期宣传是最好的，但是如果没有这样的预算费用，也应该在免费论坛中，预发布图片，吸引用户的注意。尤其是游戏产品，在预热时，最好采用图片加视频的形式。</p><p>产品的上线对一个产品来说也许是唯一一次出现在用户面前的机会。要把握好这个机会，在最开始就获得更多用户的下载和使用，并争取赢得Apple App Store推荐。在产品上线时，要调动所有的渠道把消息传播出去，可以利用PR公司、BBS、Twitter、Facebook等。</p><p>收费产品的定价要合理，例如可定为0.99美元这样符合用户的常规心理价位的价格。这里要特别需要注意，产品的价格不是由开发时间和成本决定的，而是由市场决定的。</p><p>Apple App Store推荐的规则和标准外界并不知晓，但大家都知道推荐对于一个产品的成败非常重要，上了推荐，就离产品成功大大接近了一步。而许多产品，从开始到最后开发者放弃，都没有得到过推荐。</p><p>每周都至少有一款中国人开发的产品上推荐；</p><p>上推荐是比任何广告都有效果的推广方式；</p><p>好的产品上推荐可能性就大，不好的产品也不一定没机会；</p><p>上推荐的有效期只有一周，要好好珍惜；</p><p>上了推荐，不同的位置效果也不一样，位置越靠前效果越好；</p><p>如果收费产品的价格超过$0.99，建议降价冲榜，但千万不要免费。</p><p><STRONG>排行榜：力争上游，学习优秀者</STRONG></p><p>Apple App Store排行榜对于一个产品来说是决定性的，这是用户用手机投票做出的选择，是一个产品从技术开发到推广宣传之后，最终的一个结果。</p><p>Apple App Store有3个排行榜单，不同榜单有着不同价值。收费和免费项目排行榜是用户最常看的榜单，而对开发者而言，畅销排名是最值得学习和借鉴的。</p><p>要争取上收费和免费项目排名榜，冲击榜单是每个产品完成上线之后的核心工作。要根据不同产品类型，制定具体的方式和做法。比如设计开发一个主题的系列产品，相互推荐；引导用户在Apple App Store上评价产品；利用限时免费、开发免费版等多种方式，都是为了达成这个目标。</p><p>对于开发者来说，最有学习价值的就是从畅销排名（Top Grossing）中汲取经验，比如分析排名靠前的产品，他们如何设计名称和标志、编辑截图和说明；以及通过分析排名靠前的应用软件类别，来了解用户现在关注什么类型、什么题材的产品，寻找自己开发的方向和灵感。</p><p><STRONG>免费版：够威够力</STRONG></p><p>对于收费的游戏或是工具类应用软件，免费版都是一个非常好的推广手段。</p><p>免费使用版是一定要制作的，在完整版之前还是之后推出都可以。因为在Apple App Store上，最良性的推广模式就是先在免费榜里有非常好的位置，那收费版一定会在收费榜里也会排到一个很好的位置。</p><p>当然在免费榜里的竞争是非常激烈的，有时尽管产品很出色，但仍没有受到足够的关注，这时可以把正式收费版进行免费。国内有个乒乓球类的应用软件就是这种情况，没有借助任何外力，把产品免费，成为免费榜第一，转回收费榜以后，也进到了收费榜前二百名。当然这招奏效的前提是，你的产品做的足够好。</p><p>关于免费版，有几个和大家分享的经验：</p><p>绝不无缘无故的免费（收费新产品在一开始不要免费上，即便想免费也不要一开始就free）；</p><p>免费要在最高点结束才会有效果，不要只免费一天；</p><p>同类型产品免费推广才有用，或者用1.0版免费推广2.0版；</p><p>免费榜竞争也很激烈，除非是精品，不然想冲高要借助外力；</p><p>同类型产品之间，相互免费推广才有用。</p><p><STRONG>付费推广，钱要花在刀刃上</STRONG></p><p>关于付费推广，有几个和大家分享的经验：</p><p>如果预算只有几百美金，不如不做付费推广；</p><p>我个人的经验看，Admob用来推free产品更有效，收费产品要慎重；</p><p>在Web广告要多面开花才可能有效果，投入产出比不一定好；</p><p>大公司可以使用PR公司推广；</p><p>游戏内置广告购买（tapjoy，flurry等，更适合推广免费的产品）；</p><p>广告是门学问，设计、广告词、版式等等，都需要专门的人来做。</p><p><STRONG>变化是绝对的，接受变化，适应变化！</STRONG></p><p>Apple App store一直在变化，这里我们可以回顾一下这几年的重大变化，也让大家更能理解“变化是绝对的”含义：</p><p>以前在产品更新之后，会出现在New中的，后来因为许多产品利用这个规则，不断进行产品更新以求在New中出现，才改变为更新不会出现在New里。但这些造成一个不好的影响，就是一个产品，除了在上线时有露脸的机会，之后就很难得再有出现的机会了；</p><p>以前应用的名称、类别都可以修改，现在已经不行了；</p><p>允许免费产品中内置收费点；</p><p>手机上Apple App store改版，原来的推荐页之前，多了一个分类页，造成推荐效果较原来大幅降低，而推荐的产品基本上每周下移，造成只有New and Noteworthy才有效果；</p><p>电脑上Apple App store改版，增加了很多推荐位置，但是效果不是很好，说明大多数用户还是手机上直接购买；</p><p>2010年7月推出了iAd；</p><p>2010年9月推出Game Center，现在的游戏应用如果不设置连接Game Center是不可想象的。</p><p>2011年 推出iCloud。</p><p>大家可以看到，每次Apple App store规则的变化，产品运营就都必须做出相应变化。同时我们在被动接受这些变化时，也可以主动利用这些变化。比如追踪、运用APPLE的最新技术，是获得Apple App store推荐的一个好方法，但最好是在新技术正式推出的1-2周之内。</p><p><STRONG>迭代更新，修补提升，反馈用户</STRONG></p><p>迭代开发是不断完善产品、维系发展用户的重要手段，而在每次版本更新之后，截图和说明也要与时俱进。让用户看到你的进展和提升。就像《愤怒的小鸟》这款产品，虽然是两年前开发上线的，但是在这两年中，他们不断更新内容、增加难度，所以今天大家看到的《愤怒的小鸟》，其实是一个新的产品。</p><p>关于迭代开发的时间间隔如何把握，要看各自的具体情况，主要是吸取用户意见、增加新功能，修改BUG的综合情况选择决定的。如果遇到非常严重的BUG，当然是越早修补越好。</p><p>在正常情况下，迭代升级不要太密（比如一周一次），会引起用户反感；但也不要间隔太长（比如半年都不更新一次），用户就会觉得开发者自己都不重视自己的产品，对用户的意见和建议也不进行有效及时的修补。</p><p><STRONG>最好的模式，是产品自己会说话，但产品运营的作用日益重要</STRONG></p><p>产品是第一位，是最重要的，只有你的产品足够好，才有可能有比较大的受众，如果真能够做到极致，这个产品自己能说话，比如放出一个免费版，让用户觉得真的不错的话，那它的收费版的确能冲到很高的位置。比如iFingter 1945，在2008年是免费排名第一，在收费排名是第二。</p><p>花钱推广，可以让产品一时冲到高位，但是否能够长久，就要看产品的质量了。免费收费的配合，需要注意度的把握，如果收费内容的吸引度不高，就会造成收益不高，达不到预期效果。</p><p>同时我们也看到，随着竞争的加剧，产品运营的重要性日益上升。在这样一个竞争激烈的市场，运营已经上升为产品成败的关键因素之一了。</p><p><STRONG>ifanr</STRONG><STRONG>：最后，请给准备创业的应用开发者一些建议？</STRONG></p><p>洪亮：第一，对于想要进行创业的朋友们，我的建议是，最好是先做一款产品上线尝试一下，体会一下整个过程。再决定是否辞职专心创业。有许多美好的一夜成名的创业故事，但是在Apple App store上的竞争已经不是很容易的事情了，创业不能仅仅有热情。</p><p>第二，关注用户的反馈意见，及时回复总结。用户的意见千差万别，开发者可以从用户的角度去看问题，但要知道自己其实并非真实用户。而用户提出的建议和意见，往往会开启新的思路或是以往没有意识到的问题。</p><p>第三，现在许多移动应用开发团队对于开发平台选择有些困惑。我的建议是，如果不是大公司能同时展开多平台开发，一般的小团队最好是先做好在一个平台的开发，之后再把成功经验移植到别的平台。当然，现在的移植平台技术，也会让这种移植更加迅速。</p><p>祝大家好运！</p><img src=http://www.ifanr.com/wp-content/uploads/avatar/1347.JPG >hack</img>");
        List l = p.getParagraphs();
        System.out.println(p.toContent());
    }

    class Text implements TikaUIObject {
        String body;

        Text(String body) {
            this.body = body;
        }

        public String getType() {
            return TikaUIObject.TYPE_TEXT;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getObjectBody() {
            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getOutput() {
            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }
}
