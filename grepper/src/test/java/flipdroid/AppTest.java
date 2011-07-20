package flipdroid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        Pattern encodingPattern =
                Pattern.compile("encoding=\"\\s*([a-z][_\\-0-9a-z]*)\"",
                        Pattern.CASE_INSENSITIVE);

        Matcher charsetMatcher2 = encodingPattern.matcher(new String("<xml encoding=\"gb2312\"?/>"));
        if (charsetMatcher2.find()) {
            String encoding = new String(charsetMatcher2.group(1));
//            System.out.println(en);
        }

    }
}
