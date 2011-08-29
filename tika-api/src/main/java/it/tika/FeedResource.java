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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class FeedResource extends ServerResource {

    private Tika tikaService = Tika.getInstance();

    @Get("JSON")
    public String getFeed() {
        Form form = this.getQuery();

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
            urlAbstracts = URLDBMongoDB.getInstance().findBySource(source.getId());
        }

        return Util.writeJSON(convertURLAbstractCollectionToJSON(urlAbstracts));
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

    protected JSONObject convertURLAbstractCollectionToJSON(List<URLAbstract> urlAbstractList) {
        JSONObject urlAbstracts = new JSONObject();
        for (int i = 0; i < urlAbstractList.size(); i++) {
            URLAbstract urlAbstract = urlAbstractList.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("title", urlAbstract.getTitle());
                jsonObject.accumulate("content", urlAbstract.getContent());
                JSONArray jsonArray = new JSONArray();
                List<String> images = urlAbstract.getImages();
                if (images != null) {
                    for (int j = 0; j < images.size(); j++) {
                        jsonArray.add(images.get(j));
                    }
                }
                jsonObject.accumulate("images", jsonArray);
                urlAbstracts.accumulate("abstracts", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        return urlAbstracts;
    }
}