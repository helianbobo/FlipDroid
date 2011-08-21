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
package de.l3s.boilerpipe.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes text according to Unicode word boundaries and strips off non-word
 * characters.
 * 
 * @author Christian Kohlschütter
 */
public class UnicodeTokenizer {
    private static final Pattern PAT_WORD_BOUNDARY = Pattern.compile("\\b");
    private static final Pattern PAT_NOT_WORD_BOUNDARY = Pattern
            .compile("[\u2063]*([\\\"'\\.,，。！￥（）“”‘；：· 《》、\\!\\@\\-\\:\\;\\$\\?\\(\\)/])[\u2063]*");

    /**
     * Tokenizes the text and returns an array of tokens.
     * 
     * @param text The text
     * @return The tokens
     */
    public static String[] tokenize(final CharSequence text) {

            return PAT_NOT_WORD_BOUNDARY.matcher(
                    PAT_WORD_BOUNDARY.matcher(text).replaceAll("\u2063"))
                    .replaceAll("$1").replaceAll("[ \u2063]+", " ").trim().split(
                            "[ ]+");

    }

    public static void main(String[] args) {
        String str = "\n" +
                "一个大盒子包住“前、后、左、右、上、下”6个面，因为定位产生层高的关系，所以它的顺序其实是“后、下、左、前、上、右”，下面的div就会自然的叠在上面，就可以不写z-index了。";
        String[] strArr = UnicodeTokenizer.tokenize(str);
        System.out.println(strArr);
        for (int i = 0; i < strArr.length; i++) {
            String s = strArr[i];
            System.out.println(s);
        }
    }
}
