import net.paoding.analysis.analyzer.PaodingAnalyzer
import net.paoding.analysis.analyzer.PaodingAnalyzerBean
import org.apache.lucene.analysis.Token
import org.apache.lucene.analysis.TokenStream

def analyzer = new PaodingAnalyzer("/Users/jleo/IdeaProjects/FlipDroid/tika-chinese-analyzer/src/main/resources/paoding-analysis.properties");
analyzer.setMode(PaodingAnalyzerBean.MAX_WORD_LENGTH_MODE)

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

def processed1 = new File("/Users/jleo/Downloads/rar/SogouC.reduced/processed")

def d = {folder->
    def name = folder.getName()
    def pf = new File(processed1, name)
    if (!pf.exists())
        pf.mkdirs()

    folder.listFiles().each {File f ->
        def fname = f.name
        def words = []

        f.eachLine("gbk") {line ->
            line = line.replaceAll("&nbsp","")
            words.addAll(dissect(line))
        }
        def nf = new File(pf, fname)
        nf.delete()
        nf.createNewFile()
        words.each {w ->
            nf.append(w + "\n")
        }
    }
}

def listOfFile = file.listFiles()
def numOfFolder = listOfFile.size()/4
//Thread.start {
//    listOfFile[0..numOfFolder].each{
//        folder->
//        d(folder)
//    }
//}
//Thread.start {
//    listOfFile[numOfFolder+1..2*numOfFolder].each{
//        folder->
//        d(folder)
//    }
//}
//Thread.start {
//    listOfFile[2*numOfFolder+1..3*numOfFolder].each{
//        folder->
//        d(folder)
//    }
//}
//Thread.start {
//    listOfFile[3*numOfFolder+1..4*numOfFolder-1].each{
//        folder->
//        d(folder)
//    }
//}

 dissect("""还可以将上述各种方法相互组合，例如，可以将正向最大匹配方法和逆向最大匹配方法结合起来构成双向匹配法。由于汉语单字成词的特点，正向最小匹配和逆向最小匹配一般很少使用。一般说来，逆向匹配的切分精度略高于正向匹配，遇到的歧义现象也较少。统计结果表明，单纯使用正向最大匹配的错误率为1/169，单纯使用逆向最大匹配的错误率为1/245。但这种精度还远远不能满足实际的需要。实际使用的分词系统，都是把机械分词作为一种初分手段，还需通过利用各种其它的语言信息来进一步提高切分的准确率。
　　一种方法是改进扫描方式，称为特征扫描或标志切分，优先在待分析字符串中识别和切分出一些带有明显特征的词，以这些词作为断点，可将原字符串分为较小的串再来进机械分词，从而减少匹配的错误率。另一种方法是将分词和词类标注结合起来，利用丰富的词类信息对分词决策提供帮助，并且在标注过程中又反过来对分词结果进行检验、调整，从而极大地提高切分的准确率。""")
.each {
     println it
 }


Thread.sleep(Integer.MAX_VALUE)
