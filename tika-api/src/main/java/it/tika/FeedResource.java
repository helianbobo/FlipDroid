package it.tika;


import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.URLDBMongoDB;
import it.tika.mongodb.source.Source;
import it.tika.mongodb.source.SourceDBMongoDB;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Get;

import java.util.List;

public class FeedResource extends CachedURLAbstractServerResource {

    private FeedCache feedCache = FeedCache.getInstance();

    org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

    @Get("JSON")
    public String getFeed() {
        return super.getResource(new URLAbstractQuery() {
            private String cacheKey;

            @Override
            public List<URLAbstract> query(Form request) {
                String url = null;
                if (request.getFirst("source") != null) {
                    url = request.getFirst("source").getValue();
                } else {
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    return null;
                }

                onRequestResource();
                Source source = new Source();
                source.setUrl(url);
                source = SourceDBMongoDB.getInstance().findByURL(source);
                if (source != null) {
                    return URLDBMongoDB.getInstance().findBySource(source.getId(), 8);
                }

                return null;
            }

            @Override
            public String getCacheKey() {
                return cacheKey;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}