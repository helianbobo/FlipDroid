import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/20/11
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChineseWordCutter {

    private Analyzer analyzer;
    private static ChineseWordCutter cutter = new ChineseWordCutter();

    private ChineseWordCutter() {
        analyzer = new PaodingAnalyzer(System.getProperty("user.dir") + "/paoding-dic-home.properties");
    }

    public static ChineseWordCutter getInstance() {
        return cutter;
    }

    public String[] cut(String s) {
        try {
            TokenStream ts = analyzer.tokenStream("", new StringReader(s));
            Token token;

            List<String> all = new ArrayList<String>();
            while ((token = ts.next()) != null) {
                all.add(token.termText());
            }
            return all.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}
