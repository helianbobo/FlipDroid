package com.goal98.flipdroid.util;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsPSMDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.HtmlCharsetDetector;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2/27/11
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class EncodingDetector {
    private static final int CHUNK_SIZE = 2000;
    private static Pattern metaPattern =
            Pattern.compile("<meta\\s+([^>]*http-equiv=\"?content-type\"?[^>]*)>",
                    Pattern.CASE_INSENSITIVE);
    private static Pattern charsetPattern =
            Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)",
                    Pattern.CASE_INSENSITIVE);

    private static Pattern encodingPattern =
            Pattern.compile("encoding=\"\\s*([a-z][_\\-0-9a-z]*)\"",
                    Pattern.CASE_INSENSITIVE);
    private static String detectedCharset;


    private static String sniffCharacterEncoding(byte[] content) {
        int length = content.length < CHUNK_SIZE ?
                content.length : CHUNK_SIZE;

        String str = new String(content, 0, 0, length);

        Matcher metaMatcher = metaPattern.matcher(str);
        String encoding = null;
        if (metaMatcher.find()) {
            Matcher charsetMatcher = charsetPattern.matcher(metaMatcher.group(1));

            if (charsetMatcher.find())
                encoding = new String(charsetMatcher.group(1));

        } else {
            Matcher charsetMatcher2 = encodingPattern.matcher(new String(content));
            if (charsetMatcher2.find()) {
                encoding = new String(charsetMatcher2.group(1));
            }
        }


        return encoding;
    }


    public static String detect(String urlStr) throws IOException {
        URL url = null;
        url = new URL(urlStr);
        BufferedInputStream imp = new BufferedInputStream(url.openStream());
        return detect(imp);
    }

    public static String detect(InputStream imp) throws IOException {
        nsDetector det = new nsDetector(nsPSMDetector.ALL);
        det.Init(new nsICharsetDetectionObserver() {

            public void Notify(String s) {
                detectedCharset = s;
            }
        });


        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;

        byte[] first2000Byte = new byte[3072];
        int temp = 0;
        boolean sniffFail = false;
        while ((len = imp.read(buf, 0, buf.length)) != -1) {
            if (!sniffFail) {
                System.arraycopy(buf, 0, first2000Byte, temp, len);
                temp += len;
                if (temp > 2000) {
                    String encoding = sniffCharacterEncoding(first2000Byte);
                    if (encoding != null)
                        return encoding;
                    else
                        sniffFail = true;
                }
            }
            if (isAscii)
                isAscii = det.isAscii(buf, len);


            if (!isAscii && !done)
                done = det.DoIt(buf, len, false);
        }
        det.DataEnd();

        if (isAscii) {
            detectedCharset = "ASCII";
        }
        return detectedCharset;
    }
}
