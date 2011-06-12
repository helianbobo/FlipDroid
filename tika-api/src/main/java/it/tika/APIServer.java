package it.tika;


import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;

public class APIServer {

    public static final String ROOT_URI = "file:///G:\\helianbobo-FlipDroid-48d8e89\\tika-ui/index.html";
    public static final String CURRENT_VERSION = "v1";

    public static void main(String[] args) throws Exception {

        Component c = new Component();

        // Create the HTTP server and listen on port 8182
        c.getServers().add(Protocol.HTTP, 8182);
        c.getDefaultHost().attach("/"+CURRENT_VERSION+"/url/abstract", URLAbstractResource.class);
        c.getDefaultHost().attach("/"+CURRENT_VERSION+"/url/abstract/batch", URLAbstractBatchResource.class);
        c.getDefaultHost().attach("/"+CURRENT_VERSION+"/url/abstract/rating", URLAbstractRatingResource.class);


        // Create a component

        c.getClients().add(Protocol.FILE);

        Application application = new Application() {
            @Override
            public Restlet createInboundRoot() {
                return new Directory(getContext(), ROOT_URI);
            }
        };
        c.getDefaultHost().attach(application);
        c.start();
    }
}