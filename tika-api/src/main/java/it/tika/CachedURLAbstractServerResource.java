package it.tika;

import flipdroid.grepper.URLAbstract;
import it.tika.util.Util;
import net.sf.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-6-4
 * Time: 上午11:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class CachedURLAbstractServerResource extends ServerResource {
    private FeedCache feedCache = FeedCache.getInstance();

    org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

    public String getResource(URLAbstractQuery query) {
        Form form = this.getQuery();

        Form request = (Form) getRequest().getAttributes().get("org.restlet.http.headers");
        String ifModifiedSince = request.getValues("If-Modified-Since");
        long modifiedSince = -1;
        try {
            if (ifModifiedSince != null)
                modifiedSince = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").parse(ifModifiedSince).getTime();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        List<URLAbstract> urlAbstracts = query.query(form);
        if (urlAbstracts == null)
            return null;

        String url = query.getCacheKey();
        long returnedTime = 0;
        String returnBody = "";
        Date current = new Date();
        synchronized (feedCache) {
            if (!feedCache.containsKey(url)) {//first timer
                LOG.info("url:" + url + ", first timer");
                feedCache.put(url, new CachedContent(urlAbstracts, current));
                returnedTime = current.getTime();
                returnBody = Util.writeJSON(convertURLAbstractCollectionToJSON(urlAbstracts));
            } else {
                CachedContent cache = feedCache.get(url);
                System.out.println(urlAbstracts.size());
                System.out.println(((List<URLAbstract>) cache.getContent()).size());
                if (urlAbstracts.equals(cache.getContent())) {
                    LOG.info("url:" + url + ", found in cache , check if server has newer..");
                    if (cache.getLastModified().getTime() > modifiedSince + 1000) {//new
                        LOG.info("url:" + url + ", has update");
                        returnBody = Util.writeJSON(convertURLAbstractCollectionToJSON((List<URLAbstract>) cache.getContent()));
                        returnedTime = cache.getLastModified().getTime();
//                    feedContentMap.put(url, new CachedContent(urlAbstracts, current));
                    } else {      //old
                        LOG.info("url:" + url + ", cache hit");
                        returnedTime = modifiedSince;//doesn't matter
                        returnBody = "";
                        getResponse().setStatus(new Status(304));
                    }
                } else {
                    LOG.info("url:" + url + ", server needs update");
                    feedCache.put(url, new CachedContent(urlAbstracts, current));
                    returnBody = Util.writeJSON(convertURLAbstractCollectionToJSON(urlAbstracts));
                    returnedTime = current.getTime();
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

    protected void onRequestResource() {
        List<OnRequestListener> listeners = getListeners();
        for (int i = 0; i < listeners.size(); i++) {
            try {
                OnRequestListener onRequestListener = listeners.get(i);
                onRequestListener.onRequest(this);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private List<OnRequestListener> getListeners() {
        List<OnRequestListener> listeners = new LinkedList<OnRequestListener>();
        listeners.add(new RequestLogger());
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

interface URLAbstractQuery {
    public List<URLAbstract> query(Form request);

    String getCacheKey();
}
