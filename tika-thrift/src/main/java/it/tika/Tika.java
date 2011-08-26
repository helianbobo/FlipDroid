package it.tika;

import flipdroid.grepper.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import flipdroid.grepper.extractor.ExtractorException;
import flipdroid.grepper.extractor.raw.URLRawRepo;
import flipdroid.grepper.extractor.raw.URLRepoException;
import it.tika.mongodb.image.TikaImageService;

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

        try {
            boolean bypassCache = nocache;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            final int responseCode = conn.getResponseCode();
            if(responseCode<200 || responseCode>299){
                return new URLAbstract();
            }

            urlString = conn.getURL().toString();
            if (!bypassCache) {
                try {
                    result = getDB().find(urlString);
                } catch (DBNotAvailableException e) {
//                    getLogger().log(Level.INFO, e.getMessage(), e);
                }
            }

            if (result == null || (result != null && result.getContent() == null)) {
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
//                    getLogger().log(Level.INFO, "Can't fetch document from url:" + urlDecoded);
                } else {
                    result = new URLAbstract(rawBytes, cs);
                    result.setUrl(urlString);
                    System.out.println("unextracted result:" + result.getRawContent());
                    result = new WebpageExtractor(new TikaImageService()).extract(result);
                    if (result != null && result.getContent() != null && result.getContent().length() != 0) {
                        try {
                            getDB().insertOrUpdate(result);
                        } catch (DBNotAvailableException e) {
//                            getLogger().log(Level.INFO, e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
//            getLogger().log(Level.INFO, e.getMessage(), e);
        } catch (NullPointerException ne) {
//            getLogger().log(Level.SEVERE, ne.getMessage(), ne);
        } catch (URLRepoException urle) {
//            getLogger().log(Level.SEVERE, urle.getMessage(), urle);
        } catch (ExtractorException ee) {
//            getLogger().log(Level.SEVERE, ee.getMessage(), ee);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }
}
