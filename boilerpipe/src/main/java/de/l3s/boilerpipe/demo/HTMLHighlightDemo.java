package de.l3s.boilerpipe.demo;

import java.net.URL;

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
		URL url = new URL("http://www.pcpop.com/doc/0/639/639786.shtml");

		// choose from a set of useful BoilerpipeExtractors...
		final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		// final BoilerpipeExtractor extractor =
		// CommonExtractors.DEFAULT_EXTRACTOR;
		// final BoilerpipeExtractor extractor =
		// CommonExtractors.CANOLA_EXTRACTOR;

		// choose the operation mode (i.e., highlighting or extraction)
		final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
		// final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();

		System.out.println(hh.process(url, extractor));
	}
}
