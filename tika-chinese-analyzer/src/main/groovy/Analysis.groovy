import net.paoding.analysis.analyzer.PaodingAnalyzer
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.Token

def analyzer = new PaodingAnalyzer("/Users/jleo/IdeaProjects/FlipDroid/tika-chinese-analyzer/src/main/resources/paoding-analysis.properties");
def file = new File("/Users/jleo/Downloads/rar/SogouC.reduced/Reduced")
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

def processed = new File("/Users/jleo/Downloads/rar/SogouC.reduced/processed")


file.listFiles().each {folder->
    def name = folder.getName()
    def pf = new File(processed,name)
    if(!pf.exists())
        pf.mkdirs()

    folder.listFiles().each {File f->
        def fname = f.name
        def words = []
        f.eachLine("gbk") {line->
            words.addAll(dissect(line))
        }
        def nf = new File(pf,fname)
        nf.delete()
        nf.createNewFile()
        words.each {w->
            nf.append(w+"\n")
        }
    }
}
BufferedReader read = new BufferedReader();
new InputStreamReader();
read.read
//def map = [:].withDefault {k -> 0}
//def next = false
//file.eachLine {line ->
//    if (next) {
//        dissect(line).each {
//            map[it]++
//        }
//    }
//    if (line.indexOf("一步到位") != -1) {
//        next = true
//    } else {
//        next = false;
//    }
//}
//
//map.find {k, v ->
//    if (v > 500)
//        println k
//}