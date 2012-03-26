package it.tika;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-27
 * Time: 上午10:55
 * To change this template use File | Settings | File Templates.
 */
public class QuestionAndAnswer implements  Comparable{
    private String type;
    private String choices;
    private String question;
    private String answer;
    private String analysis;

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int compareTo(Object o) {
        QuestionAndAnswer other = (QuestionAndAnswer) o;

        return compare(this.question,other.question);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int compare(String o1, String o2) {
        for (int i = 0; i < o1.length() && i < o2.length(); i++) {
            int codePoint1 = o1.charAt(i);
            int codePoint2 = o2.charAt(i);
            if (Character.isSupplementaryCodePoint(codePoint1)
                    || Character.isSupplementaryCodePoint(codePoint2)) {
                i++;
            }
            if (codePoint1 != codePoint2) {
                if (Character.isSupplementaryCodePoint(codePoint1)
                        || Character.isSupplementaryCodePoint(codePoint2)) {
                    return codePoint1 - codePoint2;
                }
                String pinyin1 = pinyin((char) codePoint1);
                String pinyin2 = pinyin((char) codePoint2);
                if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
                    if (!pinyin1.equals(pinyin2)) {
                        return pinyin1.compareTo(pinyin2);
                    }
                } else {
                    return codePoint1 - codePoint2;
                }
            }
        }
        return o1.length() - o2.length();
    }
    /**
     * 字符的拼音，多音字就得到第一个拼音。不是汉字，就return null。
     */
    private String pinyin(char c) {
        String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyins == null) {
            return null;
        }
        return pinyins[0];
    }
}
