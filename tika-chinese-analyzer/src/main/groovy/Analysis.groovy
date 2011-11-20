import net.paoding.analysis.analyzer.PaodingAnalyzer
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.Token

def analyzer = new PaodingAnalyzer("G:/sources/mining/paoding-dic-home.properties");
def file = new File("d:/record.txt")
StringBuilder sb = new StringBuilder();


def dissect = {input ->
    try {
        TokenStream ts = analyzer.tokenStream("", new StringReader(input));
        Token token;
        sb.setLength(0);

        def all = []
        while ((token = ts.next()) != null) {
            all << token.termText()
        }

        return all;
    } catch (Exception e) {
        e.printStackTrace();
        return "error";
    }
}

def map = [:].withDefault {k -> 0}
def next = false
file.eachLine {line ->
    if (next) {
        dissect(line).each {
            map[it]++
        }
    }
    if (line.indexOf("一步到位") != -1) {
        next = true
    } else {
        next = false;
    }
}

map.find {k, v ->
    if (v > 500)
        println k
}