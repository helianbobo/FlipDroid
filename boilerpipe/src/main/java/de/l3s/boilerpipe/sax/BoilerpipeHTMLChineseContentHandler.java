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
package de.l3s.boilerpipe.sax;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.LabelAction;
import de.l3s.boilerpipe.util.UnicodeTokenizer;

/**
 * A simple SAX {@link ContentHandler}, used by {@link BoilerpipeSAXInput}. Can
 * be used by different parser implementations, e.g. NekoHTML and TagSoup.
 *
 * @author Christian Kohlschütter
 */
public class BoilerpipeHTMLChineseContentHandler extends BoilerpipeHTMLContentHandler {

    private static String regEx = "[\u4e00-\u9fa5]";
    static Pattern pat = Pattern.compile(regEx);
    private boolean containsChinese;
    private static final Pattern PAT_VALID_WORD_CHARACTER = Pattern
            .compile("[\\p{L}\\p{Nd}\\p{Nl}\\p{No}]");

    private static int isWord(final String token, final boolean containsChinese) {
        if (containsChinese) {

            if (PAT_VALID_WORD_CHARACTER.matcher(token).find()) {
                return token.replaceAll("[0-9-:.,?<>]","").length();
            } else
                return 0;
        } else {
            if (PAT_VALID_WORD_CHARACTER.matcher(token).find())
                return 1;
            else
                return 0;
        }
    }

    public static void main(String[] args) {
        String token = "归类于：Apple/iOS  标签： Apple, ios, iPad, smart cover";
        final String[] tokens = UnicodeTokenizer.tokenize(token);

        System.out.println(BoilerpipeHTMLChineseContentHandler.isWord(token, true));

    }

    void flushBlock() {
        if (inBody == 0) {
            if ("TITLE".equalsIgnoreCase(lastStartTag) && inBody == 0) {
                setTitle(tokenBuffer.toString().trim());
            }
            textBuffer.setLength(0);
            tokenBuffer.setLength(0);
            return;
        }

        final int length = tokenBuffer.length();
        switch (length) {
            case 0:
                return;
            case 1:
                if (sbLastWasWhitespace) {
                    textBuffer.setLength(0);
                    tokenBuffer.setLength(0);
                    return;
                }
        }


        final String[] tokens = UnicodeTokenizer.tokenize(tokenBuffer);

        int numSegment = 0;
        int numWords = 0;
        int numLinkedWords = 0;
        int numWrappedLines = 0;
        int currentLineLength = -1; // don't count the first space
        final int maxLineLength = 80;
        int numTokens = 0;
        int numWordsCurrentLine = 0;

        for (String token : tokens) {
            Matcher matcher = pat.matcher(token);
            this.containsChinese = matcher.find();

            int numberOfWord = 0;
            if (ANCHOR_TEXT_START.equals(token)) {
                inAnchorText = true;
            } else if (ANCHOR_TEXT_END.equals(token)) {
                inAnchorText = false;
            } else if ((numberOfWord = isWord(token, containsChinese)) != 0) {
                numTokens++;
                numSegment++;
                numWords+=numberOfWord;
                numWordsCurrentLine++;
                if (inAnchorText) {
                    numLinkedWords+=1;
                }
                final int tokenLength = token.length();
                currentLineLength += tokenLength + 1;
                if (currentLineLength > maxLineLength) {
                    numWrappedLines++;
                    currentLineLength = tokenLength;
                    numWordsCurrentLine = 1;
                }
            } else {
                numTokens++;
            }
        }
        if (numTokens == 0) {
            return;
        }

        int numWordsInWrappedLines;
        if (numWrappedLines == 0) {
            numWordsInWrappedLines = numWords;
            numWrappedLines = 1;
        } else {
            numWordsInWrappedLines = numWords - numWordsCurrentLine;
        }

        TextBlock tb = new TextBlock(textBuffer.toString().trim(),
                currentContainedTextElements, numWords, numLinkedWords,
                numWordsInWrappedLines, numWrappedLines, offsetBlocks, numSegment,0);
        currentContainedTextElements = new BitSet();

        offsetBlocks++;

        textBuffer.setLength(0);
        tokenBuffer.setLength(0);

        // System.out.println(tb.getText());
        // System.out.println(numWords + "\t" + numLinkedWords + "\t"
        // + tb.textDensity + "\t" +
        // tb.linkDensity+"\t"+numWordsCurrentLine+"\t"+numWrappedLines);

        addTextBlock(tb);
    }
}