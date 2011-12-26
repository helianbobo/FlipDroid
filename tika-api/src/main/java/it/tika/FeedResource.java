package it.tika;


import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.URLDBMongoDB;
import it.tika.mongodb.source.Source;
import it.tika.mongodb.source.SourceDBMongoDB;
import it.tika.util.Util;
import net.sf.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class FeedResource extends ServerResource {

    private Tika tikaService = Tika.getInstance();
    private Map<String, CachedContent> feedContentMap = new HashMap<String, CachedContent>();

    @Get("JSON")
    public String getFeed() {
        Form form = this.getQuery();

        Form request = (Form) getRequest().getAttributes().get("org.restlet.http.headers");
        String ifModifiedSince = request.getValues("If-Modified-Since");
        long modifiedSince = -1;
        try {
            if(ifModifiedSince != null)
                modifiedSince = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").parse(ifModifiedSince).getTime();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        String url = null;
        if (form.getFirst("source") != null) {
            url = form.getFirst("source").getValue();
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return null;
        }

        onRequestResource();
        Source source = new Source();
        source.setUrl(url);
        source = SourceDBMongoDB.getInstance().findByURL(source);
        List<URLAbstract> urlAbstracts = new ArrayList<URLAbstract>();
        if (source != null) {
            urlAbstracts = URLDBMongoDB.getInstance().findBySource(source.getId(), 20);
        }
        long returnedTime = 0;
        String returnBody = "";
        Date current = new Date();
        synchronized (feedContentMap) {
            if (!feedContentMap.containsKey(url)) {//first timer
                feedContentMap.put(url, new CachedContent(urlAbstracts, current));
                returnedTime = current.getTime();
                returnBody = Util.writeJSON(convertURLAbstractCollectionToJSON(urlAbstracts));
            } else {
                CachedContent cache = feedContentMap.get(url);
                if (cache.getLastModified().getTime() > modifiedSince) {//new
                    returnBody = Util.writeJSON(convertURLAbstractCollectionToJSON(urlAbstracts));
                    returnedTime = cache.getLastModified().getTime();
                    feedContentMap.put(url, new CachedContent(urlAbstracts, current));
                } else {      //old
                    returnedTime = modifiedSince;//doesn't matter
                    returnBody = "";
                    getResponse().setStatus(new Status(304));
                }

            }
            Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");
            if (responseHeaders == null) {
                responseHeaders = new Form();
                getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
            }
            responseHeaders.add("Last-Modified-Tika", returnedTime + "");
            return returnBody;
        }
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

    protected JSONObject convertURLAbstractCollectionToJSON(List<URLAbstract> urlAbstractList) {
        JSONObject urlAbstracts = new JSONObject();
        for (int i = 0; i < urlAbstractList.size(); i++) {
            URLAbstract urlAbstract = urlAbstractList.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("title", urlAbstract.getTitle());
                jsonObject.accumulate("content", urlAbstract.getContent());
                jsonObject.accumulate("sourceURL", urlAbstract.getUrl());
                JSONArray jsonArray = new JSONArray();
                List<String> images = urlAbstract.getImages();
                if (images != null) {
                    for (int j = 0; j < images.size(); j++) {
                        jsonArray.add(images.get(j));
                    }
                }
                jsonObject.accumulate("images", jsonArray);
                jsonObject.accumulate("createDate", urlAbstract.getCreateDate().getTime());
                urlAbstracts.accumulate("abstracts", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        return urlAbstracts;
    }
}