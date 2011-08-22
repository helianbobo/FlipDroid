package it.tika;

import flipdroid.grepper.URLAbstract;
import it.tika.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.util.concurrent.*;
import java.util.logging.Level;

public class URLAbstractBatchResource extends URLAbstractResource {

    /**
     * Examle v1/url/abstract/batch?urlList=[{"url":"http://www.ifanr.com/35935"},{"url":"http://www.ifanr.com/36970"},{"url":"http://www.ifanr.com/36928"}]
     *
     * @return
     */
    private Tika tikaService = Tika.getInstance();

    @Post("JSON")
    @Get("JSON")
    public String toJson() {
        Form form = this.getQuery();
        String urlListJson = form.getFirstValue("urlList");
        JSONArray urlList = null;
        try {
            urlList = new JSONArray(urlListJson);
        } catch (JSONException e) {

            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return null;
        }

        JSONArray resultList = new JSONArray();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < urlList.length(); i++) {

            final JSONObject parameters;
            try {
                parameters = (JSONObject) urlList.get(i);
            } catch (JSONException e) {
                continue;
            }
            Future future = executor.submit(new Callable() {
                public Object call() throws Exception {
                    try {
                        String url = parameters.getString("url");
                        boolean nocache = false;
                        try {
                            nocache = parameters.getBoolean("nocache");
                        } catch (JSONException e) {

                        }
                        URLAbstract urlAbstract = tikaService.extract(url, nocache);
                        if (urlAbstract == null) {
                            return null;
                        }
                        return convertURLAbstractToJSON(urlAbstract);


                    } catch (JSONException e) {
                        getLogger().log(Level.INFO, e.getMessage(), e);
                    }

                    return null;

                }
            });

            try {
                Object result = future.get();
                if (result != null)
                    resultList.put(result);
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            }

        }


        return Util.writeJSON(resultList);
    }

}
