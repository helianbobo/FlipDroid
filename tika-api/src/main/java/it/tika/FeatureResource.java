package it.tika;


import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.URLDBMongoDB;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Get;

import java.util.List;

public class FeatureResource extends CachedURLAbstractServerResource {

    private FeedCache feedCache = FeedCache.getInstance();

    org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

    @Get("JSON")
    public String getCategory() {
        return super.getResource(new URLAbstractQuery() {
            private String cacheKey;

            @Override
            public List<URLAbstract> query(Form request) {
                cacheKey = request.getFirst("source").getValue();
                String category = null;

                if (request.getFirst("source") != null) {
                    category = request.getFirst("source").getValue();
                } else {
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    return null;
                }

                onRequestResource();
                return URLDBMongoDB.getInstance().findByCategory(category, 10, true);
            }

            @Override
            public String getCacheKey() {
                return cacheKey;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });


    }
}

