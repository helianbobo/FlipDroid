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
package de.l3s.boilerpipe.extractors;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.filters.chinese.ChineseNumWordsRulesClassifier;
import de.l3s.boilerpipe.filters.english.IgnoreBlocksAfterContentFilter;
import de.l3s.boilerpipe.filters.english.KeepLargestFulltextBlockFilter;
import de.l3s.boilerpipe.filters.english.NumWordsRulesClassifier;
import de.l3s.boilerpipe.filters.english.TerminatingBlocksFinder;
import de.l3s.boilerpipe.filters.heuristics.BlockProximityFusion;
import de.l3s.boilerpipe.filters.heuristics.DocumentTitleMatchClassifier;
import de.l3s.boilerpipe.filters.heuristics.ExpandTitleToContentFilter;
import de.l3s.boilerpipe.filters.simple.BoilerplateBlockFilter;

/**
 * A full-text extractor which is tuned towards news articles. In this scenario
 * it achieves higher accuracy than {@link DefaultExtractor}.
 * 
 * @author Christian Kohlschütter
 */
public final class ArticleExtractor extends ExtractorBase {
    public static final ArticleExtractor INSTANCE = new ArticleExtractor();

    /**
     * Returns the singleton instance for {@link ArticleExtractor}.
     */
    public static ArticleExtractor getInstance() {
        return INSTANCE;
    }

    public boolean process(TextDocument doc)
            throws BoilerpipeProcessingException {


        boolean a1 = TerminatingBlocksFinder.INSTANCE.process(doc);
        boolean a2 = new DocumentTitleMatchClassifier(doc.getTitle()).process(doc);
        boolean a3 = NumWordsRulesClassifier.INSTANCE.process(doc);
        boolean a4 = IgnoreBlocksAfterContentFilter.DEFAULT_INSTANCE.process(doc);
        boolean a5 = BlockProximityFusion.MAX_DISTANCE_1.process(doc);
        boolean a6 = BoilerplateBlockFilter.INSTANCE.process(doc);
        boolean a7 = BlockProximityFusion.MAX_DISTANCE_1_CONTENT_ONLY.process(doc);
        boolean a8 = KeepLargestFulltextBlockFilter.INSTANCE.process(doc);
        boolean a9 =  ExpandTitleToContentFilter.INSTANCE.process(doc);
        return a1|a2|a3|a4|a5|a6|a7|a8|a9;
    }
}
