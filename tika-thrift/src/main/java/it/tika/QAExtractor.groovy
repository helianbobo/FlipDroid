package it.tika

import org.apache.commons.io.FileUtils
import groovy.io.FileType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLOutput

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-27
 * Time: 下午12:03
 * To change this template use File | Settings | File Templates.
 */
class QAExtractor {
    private static HashMap<String,String> htmlEntities;
    static {
        htmlEntities = new HashMap<String,String>();
        htmlEntities.put("&lt;","<")    ; htmlEntities.put("&gt;",">");
        htmlEntities.put("&amp;","&")   ; htmlEntities.put("&quot;","\"");
        htmlEntities.put("&agrave;","à"); htmlEntities.put("&Agrave;","À");
        htmlEntities.put("&acirc;","â") ; htmlEntities.put("&auml;","ä");
        htmlEntities.put("&Auml;","Ä")  ; htmlEntities.put("&Acirc;","Â");
        htmlEntities.put("&aring;","å") ; htmlEntities.put("&Aring;","Å");
        htmlEntities.put("&aelig;","æ") ; htmlEntities.put("&AElig;","Æ" );
        htmlEntities.put("&ccedil;","ç"); htmlEntities.put("&Ccedil;","Ç");
        htmlEntities.put("&eacute;","é"); htmlEntities.put("&Eacute;","É" );
        htmlEntities.put("&egrave;","è"); htmlEntities.put("&Egrave;","È");
        htmlEntities.put("&ecirc;","ê") ; htmlEntities.put("&Ecirc;","Ê");
        htmlEntities.put("&euml;","ë")  ; htmlEntities.put("&Euml;","Ë");
        htmlEntities.put("&iuml;","ï")  ; htmlEntities.put("&Iuml;","Ï");
        htmlEntities.put("&ocirc;","ô") ; htmlEntities.put("&Ocirc;","Ô");
        htmlEntities.put("&ouml;","ö")  ; htmlEntities.put("&Ouml;","Ö");
        htmlEntities.put("&oslash;","ø") ; htmlEntities.put("&Oslash;","Ø");
        htmlEntities.put("&szlig;","ß") ; htmlEntities.put("&ugrave;","ù");
        htmlEntities.put("&Ugrave;","Ù"); htmlEntities.put("&ucirc;","û");
        htmlEntities.put("&Ucirc;","Û") ; htmlEntities.put("&uuml;","ü");
        htmlEntities.put("&Uuml;","Ü")  ; htmlEntities.put("&nbsp;"," ");
        htmlEntities.put("&copy;","\u00a9");
        htmlEntities.put("&beta;","\u00a9");
        htmlEntities.put("&reg;","\u00ae");
        htmlEntities.put("&euro;","\u20a0");
    }


    public static void main(String[] args) {
        def folder = new File("/Users/jleo/Downloads/ps")
        List<QuestionAndAnswer> qnas = new ArrayList<QuestionAndAnswer>();
        folder.eachFile {it ->
            String content = it.text
            int questionStart = content.indexOf("<b>原题</b>:<br>") + "<b>原题</b>:<br>".length()
            int choiceStartPos = content.indexOf(">A.")
            int questionEndAt = content.substring(questionStart, choiceStartPos).lastIndexOf("<")

            def choiceEnd = content.indexOf("<br><b>答案")
            int answerStartPos = choiceEnd + "<br><b>答案".length();
//            println content

            String question = unescape(content.substring(questionStart, questionStart + questionEndAt).replaceAll("<hr size=1>","")).trim()
            String type = question.substring(question.indexOf("[")+1, question.indexOf("]")).trim()
            question=question.substring(type.length()+2)
            String choices = unescape(content.substring(choiceStartPos+1, choiceEnd)).trim()
            String answers = unescape(content.substring(answerStartPos, answerStartPos + content.substring(answerStartPos).lastIndexOf("</div")).replaceAll("：</b><br>","")).trim()
            String analysis = null
            if(answers.indexOf("解析")!=-1){
                def analysisStart = answers.indexOf("解析")
                analysis = answers.substring(analysisStart+2)
                answers  = answers.substring(0, analysisStart)
            }
//            String analysis = unescape(content.substring())
            QuestionAndAnswer qna = new QuestionAndAnswer(analysis: analysis, answer: answers,choices: choices,type: type,question: question)
            qnas.add(qna)
        }

        Class.forName("org.sqlite.JDBC");
        Connection conn =
            DriverManager.getConnection("jdbc:sqlite:medicqna");
        Statement stat = conn.createStatement();
        stat.executeUpdate("drop table if exists qna;");
        stat.executeUpdate("create table qna (id,type, question,choices,answer,analysis);");
        PreparedStatement prep = conn.prepareStatement(
                "insert into qna values (?, ?, ?, ?, ?,?);");
        conn.setAutoCommit(false);
        qnas.eachWithIndex {it,idx->
            prep.setInt(1,idx)
            prep.setString(2,it.type)
            prep.setString(3,it.question)
            prep.setString(4,it.choices)
            prep.setString(5,it.answer)
            prep.setString(6,it.analysis)
            prep.addBatch();

            if(idx % 1000 == 0){
                prep.executeBatch()
                conn.commit()
            }
        }
        prep.executeBatch();
        conn.commit()
        conn.setAutoCommit(true);

        conn.close();


    }

    public static String unescape(text){

        return unescapeHTML(text.replaceAll("<[Bb]>","").replaceAll("</[Bb]>","").replaceAll("<\\?[Xx][Mm][Ll].*?","").replaceAll("<[Oo]:[Pp]>","").replaceAll("</[Oo]:[Pp]>","").replaceAll("</CA>","").replaceAll("<[Ss][uU][Bb]>","").replaceAll("</[Ss][Uu][Bb]>","").replaceAll("<[Ss][Uu][Pp]>","").replaceAll("</[Ss][Uu][Pp]>","").replaceAll("</strong>","").replaceAll("</DIV>","").replaceAll("<TABLE.*?>","").replaceAll("<TBODY.*>","").replaceAll("<TD.*>","").replaceAll("<TR.*>","").replaceAll("<IMG.*?>","").replaceAll("<[Ss][Pp][aA][nN].*?>","").replaceAll("</[Ss][pP][aA][nN]>","").replaceAll("<A.*?>","").replaceAll("</A>","").replaceAll("<DIV.*?>","").replaceAll("<CENTER>","").replaceAll("</CENTER>","").replaceAll("<strong>","").replaceAll("<[Pp].*?>","\r\n").replaceAll("</[Pp]>","").replaceAll("<[Hh][Rr]>","").replaceAll("<b>选项:</b>","").replaceAll("&mdash;","-").replaceAll("&nbsp;"," ").replaceAll("<br />","\r\n").replaceAll("<br/>","\r\n").replaceAll("<br>","\r\n").replaceAll("<BR>","\r\n")).replaceAll("<img.*?>","").replaceAll("<[Ff][Oo][Nn][Tt].*?>","").replaceAll("</[Ff][oO][nN][tT]>","").replaceAll("<b>","").replaceAll("<STRONG>","").replaceAll("</STRONG>","")
    }

    public static final String unescapeHTML(String source) {
        return org.apache.commons.lang.StringEscapeUtils.unescapeHtml(source)
    }
}
