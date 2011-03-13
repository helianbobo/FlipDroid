package flipdroid.grepper

import org.mozilla.intl.chardet.nsDetector
import org.mozilla.intl.chardet.nsPSMDetector
import org.mozilla.intl.chardet.nsICharsetDetectionObserver
import org.mozilla.intl.chardet.HtmlCharsetDetector
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2/27/11
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
class EncodingDetector {
  public static String detect(urlStr) {
    def det = new nsDetector(nsPSMDetector.ALL);
    String detectedCharset
    det.Init([
            Notify: { charset ->
              detectedCharset = charset
            }
    ] as nsICharsetDetectionObserver);

    URL url = new URL(urlStr);
    BufferedInputStream imp = new BufferedInputStream(url.openStream());

    byte[] buf = new byte[1024];
    int len;
    boolean done = false;
    boolean isAscii = true;

    while ((len = imp.read(buf, 0, buf.length)) != -1) {
      if (isAscii)
        isAscii = det.isAscii(buf, len);


      if (!isAscii && !done)
        done = det.DoIt(buf, len, false);
    }
    det.DataEnd();

    if (isAscii) {
      detectedCharset = "ASCII"
    }
    return detectedCharset
  }
}
