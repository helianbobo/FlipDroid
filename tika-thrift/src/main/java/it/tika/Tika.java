package it.tika;

import com.goal98.tika.common.URLConnectionUtil;
import com.goal98.tika.common.URLRawRepo;
import com.goal98.tika.common.URLRepoException;
import flipdroid.grepper.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import flipdroid.grepper.extractor.ExtractorException;
import it.tika.mongodb.image.TikaImageService;
import it.tika.mongodb.source.Source;
import it.tika.mongodb.source.SourceDBInterface;
import it.tika.mongodb.source.SourceDBMongoDB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/21/11
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tika {

    public WebpageExtractor webpageExtractor;
    org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Tika.class);

    private Tika() {
        webpageExtractor = new WebpageExtractor(new TikaImageService());
    }

    private static final Tika tika = new Tika();

    public static Tika getInstance() {
        return tika;
    }

    private URLDBInterface getDB() {
        return URLDBMongoDB.getInstance();
    }

    public URLAbstract extract(String urlString, boolean nocache) {
        return extract(urlString, nocache, null);
    }

    public URLAbstract extract(final String urlString, boolean nocache, String referencedFrom) {
        URLAbstract result = null;
//        LOG.info("fetching " + urlString);
        try {
            boolean bypassCache = nocache;

            if (!bypassCache) {
                try {
                    result = getDB().find(urlString);
                } catch (DBNotAvailableException e) {
                    getLogger().error(e.getMessage(), e);
                }
            }

            if (result == null || (result != null && result.getContent() == null)) {
                if (nocache) {
//                    LOG.info(Thread.currentThread().getName() + " no cache flag");
                } else {
//                    LOG.info(Thread.currentThread().getName() + " db cache miss...");
                }

                HttpURLConnection conn = null;
                int count = 0;
                int responseCode = 0;
                boolean isText = false;
                while (count < 3) {
                    try {
//                        LOG.info("trying "+urlString);
                        URL url = new URL(urlString);
                        conn = URLConnectionUtil.decorateURLConnection(url);

                        responseCode = conn.getResponseCode();
                        isText = conn.getContentType().toUpperCase().indexOf("TEXT/HTML") != -1;
//                        LOG.info("responseCode " + responseCode);
                        if (responseCode >= 200 && responseCode <= 299) {
                            break;
                        } else {
                            count++;
                        }
                    } catch (IOException e) {
                        count++;
                        getLogger().error("tried " + count + " times, " + e.getMessage(), e);
                    }
                }
                if (responseCode < 200 || responseCode > 299 || !isText) {
                    return null;
                }
                String originalURLString = conn.getURL().toString();

                byte[] rawBytes = URLRawRepo.getInstance().fetch(conn);
                if (rawBytes == null)
                    return null;
//                LOG.info("rawBytes length:" + rawBytes.length);
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
//                    LOG.info("can't fetch document");
                } else {
                    result = new URLAbstract(rawBytes, cs);
                    result.setUrl(originalURLString);
                    result.setReferencedFrom(referencedFrom);
                    result.getIndexURL().add(urlString);
                    if (!urlString.equals(originalURLString))
                        result.getIndexURL().add(originalURLString);

//                    LOG.info("unextracted result:" + result.getRawContent());

                    if (referencedFrom != null) {
                        SourceDBInterface sourceDBInterface = new SourceDBMongoDB();
                        Source source = sourceDBInterface.findByReference(referencedFrom);
                        result.setClazz(source.getClazz());
                    }
                    result = webpageExtractor.extract(result);
                    if (result != null && result.getContent() != null && result.getContent().length() != 0) {
                        try {
                            getDB().insertOrUpdate(result);
                        } catch (DBNotAvailableException e) {
                            //getLogger().error(e.getMessage(), e);
                        }
                    }
                }
            } else {
//                LOG.info("db cache hit...");
//                LOG.info("db cache:" + result.getContent());
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
