package it.tika

import org.apache.commons.io.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-28
 * Time: 下午12:25
 * To change this template use File | Settings | File Templates.
 */
class QAExtractor3 {
    def static answers = "DDCADECBECBDCDDDBDCDAACAAEBDEEAABCCCEBBDADBDDACDCDEDCCBDDDACEDBAEBEBECDDCCBEDEDEEDDCDBEBCBEBCCDABDDCDCDEBBBEBEBCADDDBEBBCECEADCEBDECCDCDCECBDEBDDEACBDDBBBBBBDBCDAEDCDBCDCDBCDCCAECABBBBCCDCBCCCEECECDEAECCCCDEEDBACDAAEEEDCDCBBCDEAAAAACCCDBEABADBAABEEAEBADDECCDDDEEABAEBEBBACDCCBEADACCBEEDDCADEAABEBEDEAADDCCCADCECEBEEDCDEABCCEAACBDCADEBBCEDBCCBACDEADAACDEBCBCBDEEACDACEEBCBAECCDDEBECCCCEBCDDCCABCAEABBADCBECBECACEABDEEACDCCCBDEDBBDECCEBBBAABAACADBCACDDAACCDBBCCBDADAEDCADEBEDECDCDEBDDDCEADEBDBEDECDEBADCBCDDBECEDDDECDADDCABACDAAADBEDAACECBBBBDBCABBCDBBDADABACCDCBEDAEEBAAADAEECEBEEBBBBABEBCBAAEEECDCBBEDBCEBEDCEADEADDCBCBBCEBDACCCADBBAEE"

    public static void main(String[] args) {
        def file = new File("/Users/jleo/IdeaProjects/FlipDroid/tika-thrift/src/main/resources/病理学总论试题+单选635题.txt")
        String fullText = file.text
        File f = new File("/Users/jleo/Download/病理学635.txt")
        List<QuestionAndAnswer> qnas = new ArrayList<QuestionAndAnswer>();
        List<String> ts = new ArrayList<String>()
        fullText.split("[0-9]+?\\.").each {
            if (it.length() == 0)
                return
            ts.add(it)

        }
        for (int i = 0; i < ts.size(); i++) {
            String o = ts.get(i);
            println i+1
            if (Character.isDigit(o[0].toCharacter())) {
                println ts[i - 1]
                ts[i - 1] = ts[i - 1] + ts[i]
                println ts[i - 1]
                ts.remove(i)
                i--
            }

        }
        ts.each {
            String t = it
            int questionEnd = t.indexOf(" A.")
            if (questionEnd == -1) {
                questionEnd = t.indexOf("end")
            }

            QuestionAndAnswer qna = new QuestionAndAnswer(question: t.substring(0, questionEnd - 3), choices: t.substring(questionEnd))
            qnas.add(qna)
        }
        Collections.sort(qnas)
        StringBuffer sb = new StringBuffer()
        qnas.eachWithIndex {o, idx ->

            o.answer = answers[idx - 1]

            int answerStart = o.choices.indexOf(o.answer.replaceAll("\r\n", ""))

            def answerEnd = o.choices.indexOf("\r\n", answerStart)
            if (answerEnd == -1) {
                answerEnd = o.choices.length()
            }
            String answerWithContent = o.choices.substring(answerStart, answerEnd)

            sb.append(o.question + " " + "答案" + answerWithContent + "\r\n")
        }

        FileUtils.writeStringToFile(f, sb.toString())
    }
}
