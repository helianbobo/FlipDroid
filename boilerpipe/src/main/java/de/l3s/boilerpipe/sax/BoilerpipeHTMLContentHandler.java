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

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.LabelAction;
import de.l3s.boilerpipe.util.UnicodeTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.sql.SQLOutput;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A simple SAX {@link ContentHandler}, used by {@link BoilerpipeSAXInput}. Can
 * be used by different parser implementations, e.g. NekoHTML and TagSoup.
 * 
 * @author Christian Kohlschütter
 */
public class BoilerpipeHTMLContentHandler implements ContentHandler {

	private final Map<String, TagAction> tagActions;
	private String title = null;

	static final String ANCHOR_TEXT_START = "$\ue00a<";
	static final String ANCHOR_TEXT_END = ">\ue00a$";

	StringBuilder tokenBuffer = new StringBuilder();
	StringBuilder textBuffer = new StringBuilder();

	int inBody = 0;
	int inAnchor = 0;
	int inIgnorableElement = 0;
	boolean sbLastWasWhitespace = false;
	private int textElementIdx = 0;

	private final List<TextBlock> textBlocks = new ArrayList<TextBlock>();

	protected String lastStartTag = null;
	@SuppressWarnings("unused")
	private String lastEndTag = null;
	@SuppressWarnings("unused")
	private Event lastEvent = null;

	protected int offsetBlocks = 0;
	protected BitSet currentContainedTextElements = new BitSet();

	private boolean flush = false;
	boolean inAnchorText = false;

	LinkedList<LabelAction> labelStack = new LinkedList<LabelAction>();
	LinkedList<Integer> fontSizeStack = new LinkedList<Integer>();
    public boolean LOOKING_FOR_IMAGE = false;

    /**
	 * Recycles this instance.
	 */
	public void recycle() {
		tokenBuffer.setLength(0);
		textBuffer.setLength(0);

		inBody = 0;
		inAnchor = 0;
		inIgnorableElement = 0;
		sbLastWasWhitespace = false;
		textElementIdx = 0;

		textBlocks.clear();

		lastStartTag = null;
		lastEndTag = null;
		lastEvent = null;

		offsetBlocks = 0;
		currentContainedTextElements.clear();

		flush = false;
		inAnchorText = false;
	}

	/**
	 * Constructs a {@link BoilerpipeHTMLContentHandler} using the
	 * {@link DefaultTagActionMap}.
	 */
	public BoilerpipeHTMLContentHandler() {
		this(DefaultTagActionMap.INSTANCE);
	}

	/**
	 * Constructs a {@link BoilerpipeHTMLContentHandler} using the given
	 * {@link TagActionMap}.
	 *
	 * @param tagActions
	 *            The {@link TagActionMap} to use, e.g.
	 *            {@link DefaultTagActionMap}.
	 */
	public BoilerpipeHTMLContentHandler(final TagActionMap tagActions) {
		this.tagActions = tagActions;
	}

	// @Override
	public void endDocument() throws SAXException {
		flushBlock();
	}

	// @Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	// @Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		if (!sbLastWasWhitespace) {
			textBuffer.append(' ');
			tokenBuffer.append(' ');
		}
		sbLastWasWhitespace = true;
	}

	// @Override
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	// @Override
	public void setDocumentLocator(Locator locator) {
	}

	// @Override
	public void skippedEntity(String name) throws SAXException {
	}

	// @Override
	public void startDocument() throws SAXException {
	}

	// @Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	// @Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		TagAction ta = tagActions.get(localName);
		if (ta != null) {
			flush = ta.start(this, localName, qName, atts) | flush;
		} else {
			flush = true;
		}

		lastEvent = Event.START_TAG;
		lastStartTag = localName;
	}

	// @Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		TagAction ta = tagActions.get(localName);
		if (ta != null) {
			flush = ta.end(this, localName, qName) | flush;
		} else {
			flush = true;
		}

		lastEvent = Event.END_TAG;
		lastEndTag = localName;
	}

	// @Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		textElementIdx++;

		if (flush) {
			flushBlock();
			flush = false;
		}

		if (inIgnorableElement != 0) {
			return;
		}

		char c;
		boolean startWhitespace = false;
		boolean endWhitespace = false;
		if (length == 0) {
			return;
		}

		final int end = start + length;
		for (int i = start; i < end; i++) {
			if (Character.isWhitespace(ch[i])) {
				ch[i] = ' ';
			}
		}
		while (start < end) {
			c = ch[start];
			if (c == ' ') {
				startWhitespace = true;
				start++;
				length--;
			} else {
				break;
			}
		}
		while (length > 0) {
			c = ch[start + length - 1];
			if (c == ' ') {
				endWhitespace = true;
				length--;
			} else {
				break;
			}
		}
		if (length == 0) {
			if (startWhitespace || endWhitespace) {
				if (!sbLastWasWhitespace) {
					textBuffer.append(' ');
					tokenBuffer.append(' ');
				}
				sbLastWasWhitespace = true;
			} else {
				sbLastWasWhitespace = false;
			}
			lastEvent = Event.WHITESPACE;
			return;
		}
		if (startWhitespace) {
			if (!sbLastWasWhitespace) {
				textBuffer.append(' ');
				tokenBuffer.append(' ');
			}
		}
		textBuffer.append(ch, start, length);
		tokenBuffer.append(ch, start, length);
		if (endWhitespace) {
			textBuffer.append(' ');
			tokenBuffer.append(' ');
		}

		sbLastWasWhitespace = endWhitespace;
		lastEvent = Event.CHARACTERS;

		currentContainedTextElements.set(textElementIdx);
	}

	List<TextBlock> getTextBlocks() {
		return textBlocks;
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

		int numWords = 0;
		int numLinkedWords = 0;
		int numWrappedLines = 0;
		int currentLineLength = -1; // don't count the first space
		final int maxLineLength = 80;
		int numTokens = 0;
		int numWordsCurrentLine = 0;

		for (String token : tokens) {
			if (ANCHOR_TEXT_START.equals(token)) {
				inAnchorText = true;
			} else if (ANCHOR_TEXT_END.equals(token)) {
				inAnchorText = false;
			} else if (isWord(token)) {
				numTokens++;
				numWords++;
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
				numWordsInWrappedLines, numWrappedLines, offsetBlocks,0,0,LOOKING_FOR_IMAGE);
		currentContainedTextElements = new BitSet();

		offsetBlocks++;

		textBuffer.setLength(0);
		tokenBuffer.setLength(0);

		// //System.out.println(tb.getText());
		// //System.out.println(numWords + "\t" + numLinkedWords + "\t"
		// + tb.textDensity + "\t" +
		// tb.linkDensity+"\t"+numWordsCurrentLine+"\t"+numWrappedLines);

		addTextBlock(tb);
	}

	protected void addTextBlock(final TextBlock tb) {

		for (Integer l : fontSizeStack) {
			if (l != null) {
				tb.addLabel("font-" + l);
				break;
			}
		}
		for (LabelAction labels : labelStack) {
			if (labels != null) {
				labels.addTo(tb);
			}
		}

		textBlocks.add(tb);
	}

	private static final Pattern PAT_VALID_WORD_CHARACTER = Pattern
			.compile("[\\p{L}\\p{Nd}\\p{Nl}\\p{No}]");

	private static boolean isWord(final String token) {
		return PAT_VALID_WORD_CHARACTER.matcher(token).find();
	}

	static private enum Event {
		START_TAG, END_TAG, CHARACTERS, WHITESPACE
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String s) {
		if (s == null || s.length() == 0) {
			return;
		}
		title = s;
	}

	/**
	 * Returns a {@link TextDocument} containing the extracted {@link TextBlock}
	 * s. NOTE: Only call this after parsing.
	 * 
	 * @return The {@link TextDocument}
	 */
	public TextDocument toTextDocument() {
		// just to be sure
		flushBlock();

		return new TextDocument(getTitle(), getTextBlocks());
	}

	public void addWhitespaceIfNecessary() {
		if (!sbLastWasWhitespace) {
			tokenBuffer.append(' ');
			textBuffer.append(' ');
			sbLastWasWhitespace = true;
		}
	}
}