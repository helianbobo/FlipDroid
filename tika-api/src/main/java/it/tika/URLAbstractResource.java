package it.tika;


import flipdroid.grepper.EncodingDetector;
import it.tika.exception.DBNotAvailableException;
import it.tika.exception.ExtractorException;
import it.tika.exception.URLRepoException;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Level;

public class URLAbstractResource extends ServerResource {

    private URLDBInterface getDB() {
        return URLDBMongoDB.getInstance();
    }

    public void setDB(URLDBInterface db) {
        //TODO: database implementation injection
    }

    private URLAbstract find() {

        URLAbstract result = null;
        Form form = this.getQuery();
        long start = 0;
        long end = 0;
        StopWatch sw = new StopWatch();
        try {
            String url = form.getFirst("url").getValue();
            boolean nocache = false;
            if (form.getFirst("nocache") != null) {
                nocache = Boolean.valueOf(form.getFirst("nocache").getValue());
            }

            String urlDecoded = java.net.URLDecoder.decode(url, "UTF-8");

            if (!nocache) {
                try {
                    sw.start("DB Cache Query");
                    result = getDB().find(urlDecoded);
                } catch (DBNotAvailableException e) {
                    getLogger().log(Level.INFO, e.getMessage(), e);
                }
            }

            sw.stopPrintReset();

            if (result == null) {
                sw.start("Bytes Fetching");
                byte[] rawBytes = URLRawRepo.getInstance().fetch(urlDecoded);
                sw.stopPrintReset();
                sw.start("Charset Detection");
                String charset = EncodingDetector.detect(urlDecoded);
                sw.stopPrintReset();
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
                    getLogger().log(Level.INFO, "Can't fetch document from url:" + urlDecoded);
                } else {
                    result = new URLAbstract(rawBytes, cs);
                    result.setUrl(url);
                    sw.start("Content Extraction");
                    result = WebpageExtractor.getInstance().extract(result);
                    sw.stopPrintReset();
                    if (result != null) {
                        try {
                            sw.start("Persist Result");
                            getDB().insert(result);
                            sw.stopPrintReset();
                        } catch (DBNotAvailableException e) {
                            getLogger().log(Level.INFO, e.getMessage(), e);
                        }
                    }
                }
            }

        } catch (UnsupportedEncodingException e) {
            getLogger().log(Level.INFO, e.getMessage(), e);
        } catch (NullPointerException ne) {
            getLogger().log(Level.SEVERE, ne.getMessage(), ne);
        } catch (URLRepoException urle) {
            getLogger().log(Level.SEVERE, urle.getMessage(), urle);
        } catch (ExtractorException ee) {
            getLogger().log(Level.SEVERE, ee.getMessage(), ee);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }

    @Get("JSON")
    public String toJson() {
        String result = "";
        URLAbstract urlAbstract = null;


        urlAbstract = find();


        if (urlAbstract == null) {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("title", urlAbstract.getTitle());
            jsonObject.accumulate("content", urlAbstract.getContent());
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        JsonRepresentation representation = new JsonRepresentation(jsonObject);
        StringWriter writer = new StringWriter();
        try {
            representation.write(writer);
            result += writer.toString();
        } catch (IOException e) {
            getLogger().log(Level.INFO, e.getMessage(), e);
        }
        return result;
    }
}

class StopWatch {
    long start = 0;
    long end = 0;
    String event;

    public void start() {
        start = System.currentTimeMillis();
    }

    public void start(String event) {
        start();
        this.event = event;
    }

    public void stop() {
        end = System.currentTimeMillis();
    }

    public void stopPrintReset() {
        stop();
        report();
        reset();
    }

    public void reset() {
        start = 0;
        end = 0;
        event = null;
    }

    public void report() {
        System.out.println(event == null ? "No name event" : event + " cost " + (end - start) + "ms");
    }
}
