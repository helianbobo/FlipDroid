package de.l3s.boilerpipe.extractors;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.filters.english.DensityRulesClassifier;
import de.l3s.boilerpipe.filters.english.KeepLargestFulltextBlockFilter;
import de.l3s.boilerpipe.filters.english.TerminatingBlocksFinder;
import de.l3s.boilerpipe.filters.heuristics.BlockProximityFusion;
import de.l3s.boilerpipe.filters.heuristics.DocumentTitleMatchClassifier;
import de.l3s.boilerpipe.filters.heuristics.ExpandTitleToContentFilter;
import de.l3s.boilerpipe.filters.simple.BoilerplateBlockFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 3/10/11
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChineseArticleExtractor extends ExtractorBase {
    public static final ChineseArticleExtractor INSTANCE = new ChineseArticleExtractor();

    /**
     * Returns the singleton instance for {@link ArticleExtractor}.
     */
    public static ChineseArticleExtractor getInstance() {
        return INSTANCE;
    }

    public boolean process(TextDocument doc)
            throws BoilerpipeProcessingException {

        boolean a1 = TerminatingBlocksFinder.INSTANCE.process(doc);
        boolean a2 = new DocumentTitleMatchClassifier(doc.getTitle()).process(doc);
        boolean a3 = DensityRulesClassifier.INSTANCE.process(doc);
        // boolean a4 = IgnoreBlocksAfterContentFilter.DEFAULT_INSTANCE.process(doc);
        boolean a5 = BlockProximityFusion.MAX_DISTANCE_1_CONTENT_ONLY.process(doc);
        boolean a6 = BoilerplateBlockFilter.INSTANCE.process(doc);
        boolean a7 = BlockProximityFusion.MAX_DISTANCE_1_CONTENT_ONLY.process(doc);
        boolean a8 = KeepLargestFulltextBlockFilter.INSTANCE.process(doc);
        boolean a9 = ExpandTitleToContentFilter.INSTANCE.process(doc);
        return a1 | a2 | a3 | a5 | a6 | a7 | a8 | a9;
    }
}
