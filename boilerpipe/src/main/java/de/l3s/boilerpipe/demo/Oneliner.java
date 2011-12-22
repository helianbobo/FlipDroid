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
        System.out.println("<p><blockquote><strong>雷军：</strong>小米手机三个月的销量很巨大，具体的数据是 21.5 万 + 10 万= 31.5 万。媒体评价很高，我很满意，业界很惊诧。</blockquote></p>".replaceAll("<p>", "<span>").replaceAll("(<blockquote>)|(</blockquote>)".replaceAll("</p>", "</span>").replaceAll("<span><.*?></span>", ""), ""));
    }
}
