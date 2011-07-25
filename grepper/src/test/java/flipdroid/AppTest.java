package flipdroid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public void testApp() throws MalformedURLException, ParseException {
//        Pattern encodingPattern =
//                Pattern.compile("encoding=\"\\s*([a-z][_\\-0-9a-z]*)\"",
//                        Pattern.CASE_INSENSITIVE);
//
//        Matcher charsetMatcher2 = encodingPattern.matcher(new String("<xml encoding=\"gb2312\"?/>"));
//        if (charsetMatcher2.find()) {
//            String encoding = new String(charsetMatcher2.group(1));
////            System.out.println(en);
//        }
//        URL url = new URL("http://www.a.com/a/b.html");
//        System.out.println(url.getPath());
//
//        "1".substring(0,0);

        String a = "Sun, 24 Jul 2011 06:31:29 GMT";
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").parse(a);
    }
}
