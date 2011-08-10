package it.tika;


import flipdroid.grepper.*;
import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.exception.DBNotAvailableException;
import flipdroid.grepper.extractor.ExtractorException;
import flipdroid.grepper.extractor.image.TikaImageService;
import flipdroid.grepper.extractor.raw.URLRawRepo;
import flipdroid.grepper.extractor.raw.URLRepoException;
import it.tika.util.StopWatch;
import it.tika.util.Util;
import net.sf.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedList;
import java.util.List;
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
            boolean bypassCache = nocache;

            String urlDecoded = java.net.URLDecoder.decode(url, "UTF-8");

            if (!bypassCache) {
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
                    result.setUrl(urlDecoded);
                    sw.start("Content Extraction");
                    result = new WebpageExtractor(new TikaImageService()).extract(result);
                    sw.stopPrintReset();
                    if (result != null) {
                        try {
                            sw.start("Persist Result");
                            getDB().insertOrUpdate(result);
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

        onRequestResource();
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


        return Util.writeJSON(convertURLAbstractToJSON(urlAbstract));

    }

    private void onRequestResource() {
        List<OnRequestListener> listeners = getListeners();
        for (int i = 0; i < listeners.size(); i++) {
            try {
                OnRequestListener onRequestListener = listeners.get(i);
                onRequestListener.onRequest(this);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, listeners.get(i).toString(), e);
            }
        }
    }

    private List<OnRequestListener> getListeners() {
        List<OnRequestListener> listeners = new LinkedList<OnRequestListener>();
        listeners.add(new Logger());
        return listeners;
    }

    protected JSONObject convertURLAbstractToJSON(URLAbstract urlAbstract) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate("title", urlAbstract.getTitle());
            jsonObject.accumulate("content", urlAbstract.getContent());
            JSONArray jsonArray = new JSONArray();
            List<String> images = urlAbstract.getImages();
            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    jsonArray.add(images.get(i));
                }
            }
            jsonObject.accumulate("images", jsonArray);


        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return jsonObject;
    }
}