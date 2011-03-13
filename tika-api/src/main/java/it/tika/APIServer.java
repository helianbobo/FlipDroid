package it.tika;


import org.restlet.Component;
import org.restlet.data.Protocol;

public class APIServer{


    public static void main(String[] args) throws Exception {

        Component c = new Component();

      // Create the HTTP server and listen on port 8182
        c.getServers().add(Protocol.HTTP, 8182);
        c.getDefaultHost().attach("/v1/url/abstract", URLAbstractResource.class);

//        Router router = new Router(c.getContext().createChildContext());
//        router.setDefaultMatchingQuery(true);
//        router.attach("/v1/url/abstract", URLAbstractResource.class);

        c.start();
   }

}
