package it.tika;

import flipdroid.grepper.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import flipdroid.grepper.extractor.ExtractorException;
import flipdroid.grepper.extractor.raw.URLRawRepo;
import flipdroid.grepper.extractor.raw.URLRepoException;
import it.tika.mongodb.image.TikaImageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/21/11
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tika {
    private Tika() {

    }

    private static final Tika tika = new Tika();

    public static Tika getInstance() {
        return tika;
    }

    private URLDBInterface getDB() {
        return URLDBMongoDB.getInstance();
    }

    public URLAbstract extract(String urlString, boolean nocache) {
        URLAbstract result = null;
        System.out.println("fetching " + urlString);
        try {
            boolean bypassCache = nocache;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            final int responseCode = conn.getResponseCode();
            System.out.println("responseCode " + responseCode);
            if(responseCode<200 || responseCode>299){
                return new URLAbstract();
            }

            urlString = conn.getURL().toString();
            if (!bypassCache) {
                try {
                    result = getDB().find(urlString);
                } catch (DBNotAvailableException e) {
                    getLogger().error(e.getMessage(), e);
                }
            }

            if (result == null || (result != null && result.getContent() == null)) {
                System.out.println("db cache miss...");
                byte[] rawBytes = URLRawRepo.getInstance().fetch(urlString);
                if (rawBytes == null)
                    return null;
                System.out.println("rawBytes length:" + rawBytes.length);
                String charset = EncodingDetector.detect(new BufferedInputStream(new ByteArrayInputStream(rawBytes)));
//                String charset = EncodingDetector.detect(urlDecoded);
                Charset cs = null;
                if (charset != null)
                    cs = Charset.forName(charset);
                else {
                    try {
                        cs = Charset.forName("utf-8");
                    } catch (UnsupportedCharsetException e) {
                        // keep default
                    }
                }

                if (rawBytes == null) {
                    getLogger().info("Can't fetch document");
                    System.out.println("can't fetch document");
                } else {
                    result = new URLAbstract(rawBytes, cs);
                    result.setUrl(urlString);
                    System.out.println("unextracted result:" + result.getRawContent());
                    result = new WebpageExtractor(new TikaImageService()).extract(result);
                    if (result != null && result.getContent() != null && result.getContent().length() != 0) {
                        try {
                            getDB().insertOrUpdate(result);
                        } catch (DBNotAvailableException e) {
                            getLogger().error(e.getMessage(), e);
                        }
                    }
                }
            }else{
                System.out.println("db cache hit...");
                System.out.println("db cache:"+result.getContent());
            }
        } catch (UnsupportedEncodingException e) {
            getLogger().error(e.getMessage(), e);
        } catch (NullPointerException ne) {
            getLogger().error(ne.getMessage(), ne);
        } catch (URLRepoException urle) {
            getLogger().error(urle.getMessage(), urle);
        } catch (ExtractorException ee) {
            getLogger().error(ee.getMessage(), ee);
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
        return result;
    }
    private Log logger = LogFactory.getLog(Tika.class);

    private Log getLogger() {
        return logger;  //To change body of created methods use File | Settings | File Templates.
    }
}
