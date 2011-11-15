package it.tika;


import flipdroid.grepper.*;
import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.exception.DBNotAvailableException;
import flipdroid.grepper.extractor.ExtractorException;
import flipdroid.grepper.extractor.raw.URLRawRepo;
import flipdroid.grepper.extractor.raw.URLRepoException;
import it.tika.mongodb.image.TikaImageService;
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

    private Tika tikaService = Tika.getInstance();

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

        URLAbstract urlAbstract = null;
        String urlDecoded = null;
        try {
            urlDecoded = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        System.out.println(urlDecoded);
        urlAbstract = tikaService.extract(urlDecoded, nocache);

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
            jsonObject.accumulate("sourceURL", urlAbstract.getUrl());
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