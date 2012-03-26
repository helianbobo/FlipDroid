package it.tika

import org.apache.commons.digester.RegexMatcher
import java.util.regex.Pattern
import java.util.regex.Matcher
import org.apache.commons.io.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-28
 * Time: 下午12:25
 * To change this template use File | Settings | File Templates.
 */
class QAExtractor2 {
    def static answers = "BCEEADEAADBDDAADDEABEBABDCBDBBEEEEADCEDBADCAADDBDEBEBADDBEDBCCECCADCDDABBEBCCDEACDBDECCCABEABBCEBDD"
    public static void main(String[] args) {
        def file = new File("/Users/jleo/IdeaProjects/FlipDroid/tika-thrift/src/main/resources/病理学总论试题.txt")
        String fullText = file.text
        File f = new File("/Users/jleo/Download/病理学2.txt")
        List<QuestionAndAnswer> qnas = new ArrayList<QuestionAndAnswer>();
        fullText.split("[0-9]+?、").each {
            if(it.length() == 0)
                return

            String t = it
            int questionEnd = t.indexOf("\n")
            QuestionAndAnswer qna = new QuestionAndAnswer(question: t.substring(0,questionEnd),choices: t.substring(questionEnd+1))
            qnas.add(qna)
        }
        Collections.sort(qnas)
        StringBuffer sb = new StringBuffer()
        qnas.eachWithIndex {o, idx->

            o.answer = answers[idx-1]

            int answerStart = o.choices.indexOf(o.answer.replaceAll("\r\n",""))

            def answerEnd = o.choices.indexOf("\r\n", answerStart)
            if(answerEnd==-1){
                answerEnd = o.choices.length()
            }
            String answerWithContent = o.choices.substring(answerStart, answerEnd)

            sb.append(o.question + " " + "答案" + answerWithContent +"\r\n")
        }

        FileUtils.writeStringToFile(f,sb.toString())
    }
}
