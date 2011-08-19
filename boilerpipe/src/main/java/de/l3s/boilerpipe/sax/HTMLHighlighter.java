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

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.DefaultLabels;
import de.l3s.boilerpipe.util.JaneSort;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Highlights text blocks in an HTML document that have been marked as "content"
 * in the corresponding {@link TextDocument}.
 *
 * @author Christian Kohlschütter
 */
public final class HTMLHighlighter {

    private URL url;

    /**
     * Creates a new {@link HTMLHighlighter}, which is set-up to return the full
     * HTML text, with the extracted text portion <b>highlighted</b>.
     */
    public static HTMLHighlighter newHighlightingInstance() {
        return new HTMLHighlighter(false);
    }

    /**
     * Creates a new {@link HTMLHighlighter}, which is set-up to return only the
     * extracted HTML text, including enclosed markup.
     */
    public static HTMLHighlighter newExtractingInstance() {
        return new HTMLHighlighter(true);
    }

    public static HTMLHighlighter newExtractingInstanceForChinese() {
        return new HTMLHighlighter(true, true);
    }

    private HTMLHighlighter(final boolean extractHTML) {
        this(extractHTML, false);
    }

    private HTMLHighlighter(final boolean extractHTML, boolean forChinese) {
        if (extractHTML) {
            setOutputHighlightOnly(true);
            setExtraStyleSheet("");
            setPreHighlight("");
            setPostHighlight("");
        }
        this.forChinese = forChinese;
    }

    private static TextDocument doc;

    /**
     * Processes the given {@link TextDocument} and the original HTML text (as a
     * String).
     *
     * @param doc      The processed {@link TextDocument}.
     * @param origHTML The original HTML document.
     * @throws BoilerpipeProcessingException
     */
//    public String process(final TextDocument doc, final String origHTML)
//            throws BoilerpipeProcessingException {
//        return process(doc, new InputSource(new StringReader(origHTML)));
//    }

    /**
     * Processes the given {@link TextDocument} and the original HTML text (as
     * an {@link InputSource}).
     *
     * @param doc    The processed {@link de.l3s.boilerpipe.document.TextDocument}.
     * @param is     The original HTML document.
     * @param images
     * @throws BoilerpipeProcessingException
     */
    public String process(final TextDocument doc, final InputSource is, List<String> images)
            throws BoilerpipeProcessingException {
        final Implementation implementation = new Implementation();
        implementation.process(doc, is, images);

        String html = implementation.html.toString();
        if (outputHighlightOnly) {
            Matcher m;

            boolean repeat = true;
            while (repeat) {
                repeat = false;
                m = PAT_TAG_NO_TEXT.matcher(html);
                if (m.find()) {
                    repeat = true;
                    html = m.replaceAll("");
                }

//                m = PAT_SUPER_TAG.matcher(html);
//                if (m.find()) {
//                    repeat = true;
//                    html = m.replaceAll(m.group(1));
//                }
            }
        }

        return html;


    }

    private static final Pattern PAT_TAG_NO_TEXT = Pattern.compile("<[^/][^>]*></[^>]*>");
    private static final Pattern PAT_SUPER_TAG = Pattern.compile("^<[^>]*>(<.*?>)</[^>]*>$");

    public String process(final URL url, final BoilerpipeExtractor extractor)
            throws IOException, BoilerpipeProcessingException, SAXException {
        final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);
        this.url = url;
        return process(htmlDoc, extractor);
    }

    boolean forChinese = false;

    public String process(final HTMLDocument htmlDoc, final BoilerpipeExtractor extractor)
            throws IOException, BoilerpipeProcessingException, SAXException {
        final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource())
                .getTextDocument(forChinese);  //for chinese

        Map<Integer, String> images = new HashMap<Integer, String>();
        for (TextBlock text : doc.getTextBlocks()) {
            if (text.isImage()) {
                images.put(text.getOffsetBlocksStart(), text.getText());
            }
        }

        extractor.process(doc);

        List<Block> contentArr = new ArrayList<Block>();
        for (TextBlock textBlock : doc.getTextBlocks()) {
            if (textBlock.isContent()) {
                int start = textBlock.getOffsetBlocksStart();
                int end = textBlock.getOffsetBlocksEnd();
                Block b = new Block();
                b.start = start;
                b.end = end;
                contentArr.add(b);
            }

        }
        List<String> result = JaneSort.findBetween(images, contentArr, 4);
        this.title = doc.getTitle();

        final InputSource is = htmlDoc.toInputSource();

        return postProcess(process(doc, is, result));
    }

    private String postProcess(String process) {
        return process;//.replace("<p><img", "<img").replace("</img></p>", "</img>");
    }

    private String title = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public class Block {
        public int start;
        public int end;
    }


    private boolean outputHighlightOnly = false;
    private String extraStyleSheet = "\n<style type=\"text/css\">\n"
            + ".x-boilerpipe-mark1 {" + " text-decoration:none; "
            + "background-color: #ffff42 !important; "
            + "color: black !important; " + "display:inline !important; "
            + "visibility:visible !important; }\n" + //
            "</style>\n";
    private String preHighlight = "<span class=\"x-boilerpipe-mark1\">";
    private String postHighlight = "</span>";

    /**
     * If true, only HTML enclosed within highlighted content will be returned
     */
    public boolean isOutputHighlightOnly() {
        return outputHighlightOnly;
    }

    /**
     * Sets whether only HTML enclosed within highlighted content will be
     * returned, or the whole HTML document.
     */
    public void setOutputHighlightOnly(boolean outputHighlightOnly) {
        this.outputHighlightOnly = outputHighlightOnly;
    }

    /**
     * Returns the extra stylesheet definition that will be inserted in the HEAD
     * element.
     * <p/>
     * By default, this corresponds to a simple definition that marks text in
     * class "x-boilerpipe-mark1" as inline text with yellow background.
     */
    public String getExtraStyleSheet() {
        return extraStyleSheet;
    }

    /**
     * Sets the extra stylesheet definition that will be inserted in the HEAD
     * element.
     * <p/>
     * To disable, set it to the empty string: ""
     *
     * @param extraStyleSheet Plain HTML
     */
    public void setExtraStyleSheet(String extraStyleSheet) {
        this.extraStyleSheet = extraStyleSheet;
    }

    /**
     * Returns the string that will be inserted before any highlighted HTML
     * block.
     * <p/>
     * By default, this corresponds to
     * <code>&lt;span class=&qupt;x-boilerpipe-mark1&quot;&gt;</code>
     */
    public String getPreHighlight() {
        return preHighlight;
    }

    /**
     * Sets the string that will be inserted prior to any highlighted HTML
     * block.
     * <p/>
     * To disable, set it to the empty string: ""
     */
    public void setPreHighlight(String preHighlight) {
        this.preHighlight = preHighlight;
    }

    /**
     * Returns the string that will be inserted after any highlighted HTML
     * block.
     * <p/>
     * By default, this corresponds to <code>&lt;/span&gt;</code>
     */
    public String getPostHighlight() {
        return postHighlight;
    }

    /**
     * Sets the string that will be inserted after any highlighted HTML block.
     * <p/>
     * To disable, set it to the empty string: ""
     */
    public void setPostHighlight(String postHighlight) {
        this.postHighlight = postHighlight;
    }

    private abstract static class TagAction {
        void beforeStart(final Implementation instance, final String localName, Attributes atts) {
        }

        void afterStart(final Implementation instance, final String localName) {
        }

        void beforeEnd(final Implementation instance, final String localName) {
        }

        void afterEnd(final Implementation instance, final String localName) {
        }
    }

    private static final TagAction TA_IGNORABLE_ELEMENT = new TagAction() {
        void beforeStart(final Implementation instance, final String localName, Attributes atts) {
            instance.inIgnorableElement++;
        }

        void afterEnd(final Implementation instance, final String localName) {
            instance.inIgnorableElement--;
        }
    };

//    private final TagAction TA_IMAGE = new TagAction() {
//        void beforeStart(final Implementation instance, final String localName, Attributes atts) {
//            int length = atts.getLength();
//
//            String it.tika.mongodb.image = null;
//            // Process each attribute
//            for (int i = 0; i < length; i++) {
//                // Get names and values for each attribute
//                String name = atts.getQName(i);
//                if (name.toUpperCase().equals("SRC")) {
//                    it.tika.mongodb.image = atts.getValue(i);
//                    break;
//                }
//            }
//            if (it.tika.mongodb.image != null)
//                for (TextBlock block : doc.getTextBlocks()) {
//                    if (block.getText() != null)
//                        if (block.getText().contains(it.tika.mongodb.image)) {
//                            images.add(it.tika.mongodb.image);
//                            break;
//                        }
//                }
//
//        }
//
//        void afterEnd(final Implementation instance, final String localName) {
//        }
//    };


    private static final TagAction TA_HEAD = new TagAction() {

        void beforeEnd(final Implementation instance, String localName) {
            instance.html.append(instance.hl.extraStyleSheet);
        }
    };
    public List<String> images = new ArrayList<String>();

    private static Map<String, TagAction> TAG_ACTIONS = new HashMap<String, TagAction>();

    static {
        TAG_ACTIONS.put("STYLE", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("SCRIPT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("OPTION", TA_IGNORABLE_ELEMENT);
        // TAG_ACTIONS.put("NOSCRIPT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("OBJECT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("EMBED", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("APPLET", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("LINK", TA_IGNORABLE_ELEMENT);


        TAG_ACTIONS.put("HEAD", TA_HEAD);
    }

    public List<String> getImages() {
        return images;
    }

    private final class Implementation extends AbstractSAXParser implements
            ContentHandler {
        StringBuilder html = new StringBuilder();

        private int inIgnorableElement = 0;
        private int characterElementIdx = 0;
        private final BitSet contentBitSet = new BitSet();
        private final HTMLHighlighter hl = HTMLHighlighter.this;
        private boolean inBlockQuote;

        Implementation() {
            super(new HTMLConfiguration());
            setContentHandler(this);
        }

        void process(final TextDocument doc, final InputSource is, List<String> images)
                throws BoilerpipeProcessingException {
            HTMLHighlighter.this.doc = doc;
            for (TextBlock block : doc.getTextBlocks()) {
                if (block.isContent()) {
                    final BitSet bs = block.getContainedTextElements();
                    if (bs != null) {
                        contentBitSet.or(bs);
                    }
                }
            }
            List<String> imageList = new ArrayList<String>();
            List<Integer> imagesPositionSet = new ArrayList<Integer>();
            for (String text : images) {
                imageList.add(text);
//                imagesPositionSet.add(text.get)
            }

            HTMLHighlighter.this.images = imageList;

            try {
                parse(is);
            } catch (SAXException e) {
                throw new BoilerpipeProcessingException(e);
            } catch (IOException e) {
                throw new BoilerpipeProcessingException(e);
            }
        }

        public void endDocument() throws SAXException {
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void ignorableWhitespace(char[] ch, int start, int length)
                throws SAXException {
        }

        public void processingInstruction(String target, String data)
                throws SAXException {
        }

        public void setDocumentLocator(Locator locator) {
        }

        public void skippedEntity(String name) throws SAXException {
        }

        public void startDocument() throws SAXException {
        }

        private int blockLevel = 0;
        private List<String> deferredImages = new ArrayList<String>();

        public void startElement(String uri, String localName, String qName,
                                 Attributes atts) throws SAXException {

            TagAction ta = TAG_ACTIONS.get(localName);

            if (ta != null) {
                ta.beforeStart(this, localName, atts);
            }
//            if (localName.toUpperCase().equals("IMG") || localName.toUpperCase().equals("IMAGE")) {
//                TA_IMAGE.beforeStart(this, localName, atts);
//            }
            try {
                if (inIgnorableElement == 0) {
                    if (qName.equalsIgnoreCase("h1") ||
                            qName.equalsIgnoreCase("h2") ||
                            qName.equalsIgnoreCase("h3") ||
                            qName.equalsIgnoreCase("h4") ||
                            qName.equalsIgnoreCase("h5") ||
                            qName.equalsIgnoreCase("h6") ||
                            qName.equalsIgnoreCase("strong")) {
                        html.append("<" + qName + ">");
                    }
                    if (qName.equalsIgnoreCase("p") ||
                            qName.equalsIgnoreCase("br") ||
                            qName.equalsIgnoreCase("li")
                            ) {
                        blockLevel++;
                        if (inBlockQuote) {
                            html.append("<p><blockquote>");
                        } else
                            html.append("<p>");
                    }
                    if (qName.equalsIgnoreCase("blockquote")) {
                        inBlockQuote = true;
                    }

                    if (qName.equalsIgnoreCase("img") || qName.equalsIgnoreCase("image")) {

                        int length = atts.getLength();
                        String image = null;
                        // Process each attribute
                        for (int i = 0; i < length; i++) {
                            // Get names and values for each attribute
                            String name = atts.getQName(i);
                            if (name.equalsIgnoreCase("src")) {
                                image = atts.getValue(i);
                                break;
                            }
                        }
                        if (image != null && image.trim().length() != 0)
                            for (int i = 0; i < HTMLHighlighter.this.images.size(); i++) {
                                String s = HTMLHighlighter.this.images.get(i);
                                if (s.indexOf(image) != -1) {
//                                    if (html.toString().length() != 0 && !html.toString().endsWith("</p>") && !html.toString().endsWith("/>")) {
//                                    if (inBlockQuote) {
//                                        html.append("</blockquote></p>");
//                                    } else
//                                        html.append("</p>");
//                                    }
                                    String img = "<img src=" + image + " >hack</img>";
                                    if (blockLevel == 0)
                                        html.append(img);//there must be a blank after image for parsing purpose
                                    else
                                        deferredImages.add(img);
//                                    if (inBlockQuote)
//                                        html.append("<blockquote><p>");
//                                    else
//                                        html.append("<p>");
                                    break;
                                }
                            }
                    }
                }
            } finally {
                if (ta != null) {
                    ta.afterStart(this, localName);
                }

            }
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
//            System.out.println(qName);

            TagAction ta = TAG_ACTIONS.get(localName);
            if (ta != null) {
                ta.beforeEnd(this, localName);
            }

            try {
                if (inIgnorableElement == 0) {
                    if (outputHighlightOnly) {
//                        boolean highlight = contentBitSet
//                                .get(characterElementIdx);
//
//                        if (!highlight) {
//                            return;
//                        }
                    }
                    if (qName.equalsIgnoreCase("h1") ||
                            qName.equalsIgnoreCase("h2") ||
                            qName.equalsIgnoreCase("h3") ||
                            qName.equalsIgnoreCase("h4") ||
                            qName.equalsIgnoreCase("h5") ||
                            qName.equalsIgnoreCase("h6") ||
                            qName.equalsIgnoreCase("strong")) {
                        html.append("</" + qName + ">");
                    }
                    if (qName.equalsIgnoreCase("p") ||
                            qName.equalsIgnoreCase("br") ||
                            qName.equalsIgnoreCase("li")
                            ) {
                        if (inBlockQuote) {
                            html.append("</blockquote></p>");
                        } else
                            html.append("</p>");
                        blockLevel--;
                        if (blockLevel == 0) {
                            for (int i = 0; i < deferredImages.size(); i++) {
                                String image = deferredImages.get(i);
                                html.append(image);
                            }
                            deferredImages.clear();
                        }
                    }
                    if (qName.equalsIgnoreCase("blockquote")) {
                        inBlockQuote = false;
                    }
                }
            } finally {
                if (ta != null) {
                    ta.afterEnd(this, localName);
                }
            }
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            characterElementIdx++;
            if (inIgnorableElement == 0) {

                boolean highlight = contentBitSet.get(characterElementIdx);

                if (!highlight && outputHighlightOnly) {
                    return;
                }
                if (highlight) {
                    html.append(preHighlight);
                }
                boolean addTag = false;
                if (blockLevel == 0)
                    addTag = true;
                if (addTag) {
                    String text = xmlEncode(String.valueOf(ch, start, length).replace("\n", ""));
                    html.append("<p>" + text + "</p>");
                } else
                    html.append(xmlEncode(String.valueOf(ch, start, length).replace("\n", "")));
                if (highlight) {
                    html.append(postHighlight);
                }
            }
        }

        public void startPrefixMapping(String prefix, String uri)
                throws SAXException {
        }

    }

    private static String xmlEncode(final String in) {
        if (in == null) {
            return "";
        }
//        char c;
//        StringBuilder out = new StringBuilder(in.length());
//
//        for (int i = 0; i < in.length(); i++) {
//            c = in.charAt(i);
//            switch (c) {
//                default:
//                    out.append(c);
//            }
//        }

        return in;
    }

}
