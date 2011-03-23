package it.tika;


import flipdroid.grepper.EncodingDetector;
import it.tika.cases.Case;
import it.tika.cases.CaseRepositoryDBMongoDB;
import it.tika.exception.DBNotAvailableException;
import it.tika.exception.ExtractorException;
import it.tika.exception.URLRepoException;
import it.tika.util.StopWatch;
import it.tika.util.Util;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;

public class URLAbstractResource extends ServerResource {

    private URLDBInterface getDB() {
        return URLDBMongoDB.getInstance();
    }

    public void setDB(URLDBInterface db) {
        //TODO: database implementation injection
    }

    protected URLAbstract find(String url, boolean nocache) {

        URLAbstract result = null;


        StopWatch sw = new StopWatch();
        try {


            String urlDecoded = java.net.URLDecoder.decode(url, "UTF-8");

            if (!nocache) {
                try {
                    sw.start("DB Cache Query");
                    result = getDB().find(urlDecoded);
                    sw.stopPrintReset();
                } catch (DBNotAvailableException e) {
                    getLogger().log(Level.INFO, e.getMessage(), e);
                }
            }


            if (result == null) {
                sw.start("Bytes Fetching");
                byte[] rawBytes = URLRawRepo.getInstance().fetch(urlDecoded);
                sw.stopPrintReset();
                sw.start("Charset Detection");
                String charset = EncodingDetector.detect(new BufferedInputStream(new ByteArrayInputStream(rawBytes)));
//                String charset = EncodingDetector.detect(urlDecoded);
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
        Form form = this.getQuery();

        String url = null;
        if (form.getFirst("url") != null) {
            url = form.getFirst("url").getValue();
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return null;
        }


        boolean nocache = false;
        if (form.getFirst("nocache") != null) {
            nocache = Boolean.valueOf(form.getFirst("nocache").getValue());
        }

        String result = "";
        URLAbstract urlAbstract = null;


        urlAbstract = find(url, nocache);


        if (urlAbstract == null) {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }


        JSONObject jsonObject = convertURLAbstractToJSON(urlAbstract);

        return Util.writeJSON(jsonObject);
    }

    protected JSONObject convertURLAbstractToJSON(URLAbstract urlAbstract) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate("title", urlAbstract.getTitle());
            jsonObject.accumulate("content", urlAbstract.getContent());
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return jsonObject;
    }


}

