package it.tika

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.apache.commons.io.FileUtils
import net.sourceforge.pinyin4j.PinyinHelper

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-27
 * Time: 下午8:25
 * To change this template use File | Settings | File Templates.
 */
class MedicQuery {


    public static void main(String[] args) {
        Class.forName("org.sqlite.JDBC");
        Connection conn =
            DriverManager.getConnection("jdbc:sqlite:medicqna");
        Statement stat = conn.createStatement();
        List<QuestionAndAnswer> qnas = new ArrayList<QuestionAndAnswer>();
        File f = new File("/Users/jleo/Download/病理学.txt")
        ResultSet rs = stat.executeQuery("select * from qna where type = '病理学'");
        StringBuffer sb = new StringBuffer()
        while (rs.next()) {
            String question= rs.getString("question");
            String choices = rs.getString("choices");
            String answer = rs.getString("answer");
            String analisys = rs.getString("analysis");
            analisys = analisys==null?"":analisys
            QuestionAndAnswer qna = new QuestionAndAnswer(analysis: analisys, question: question,answer: answer,choices: choices)
            qnas.add(qna);
        }
        Collections.sort(qnas)

        for (int i = 0; i < qnas.size(); i++) {
            QuestionAndAnswer o =  qnas.get(i);
            int answerStart = o.choices.indexOf(o.answer.replaceAll("\r\n",""))

            def answerEnd = o.choices.indexOf("\r\n", answerStart)
            if(answerEnd==-1){
                answerEnd = o.choices.length()
            }
            String answerWithContent = o.choices.substring(answerStart, answerEnd)
            sb.append(i+".  "+o.question + "\r\n\r\n" + o.choices +"\r\n\r\n" + "答案" + answerWithContent +"\r\n\r\n" + o.analysis +"\r\n\r\n\r\n")
        }
        FileUtils.writeStringToFile(f,sb.toString())
        rs.close();
        conn.close();
    }

    
}
