/**
 * boilerpipe
 *
 * Copyright (c) 2009 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.l3s.boilerpipe.demo;

import java.net.URL;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * Demonstrates how to use Boilerpipe to get the main content as plain text.
 * Note: In real-world cases, you'd probably want to download the file first using a fault-tolerant crawler.
 * 
 * @author Christian Kohlschütter
 * @see HTMLHighlightDemo if you need HTML as well.
 */
public class Oneliner {
    public static void main(final String[] args) throws Exception {
        String content = "<img src=http://www.blogcdn.com/www.engadget.com/media/2011/12/samsungsmarttv00.jpgc72ce73b-d7af-431d-91e9-8075bf4a9280large.jpg width=438 height=423 >hack</img>显然 <a href=http://cn.engadget.com/tag/samsung/>Samsung</a> 还是想偷偷卖个关子，这个在 Youtube 上的预告片段，其实只有在结尾时才仅有几行文字出现，所以我们跟各位一样知道的也并不多。三星刚刚释出「Through the years」（中译：那些年，我们看过的电视！？）的广告，把超过 50 年的电视产品史浓缩为一个片段（例如：黑白转换为彩色的时代、常常要调整方位的兔耳型天线、DVR、3D 电视... 等），并且用「<a href=http://www.engadget.com/2011/09/01/samsungs-smart-tv-update-will-feature-youtube-3d-videos/>Smart TV</a> 的未来体验」做为结尾，还预告了其将在次月 CES 来到的消息。不过如同以往，我们也<a href=http://www.engadget.com/2011/11/29/engadget-the-official-online-news-source-of-ces-2012-and-the-ce/>将在现场</a>，所以大家就先看看来源的影片，预想一下猜猜究竟出现的会是什么样的产品，再等候我们的后续报导吧！<br/><p><br/></p>";

        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(article.getContent());

    }
}
