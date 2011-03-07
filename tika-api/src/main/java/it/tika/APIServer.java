package it.tika;


import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.io.StringWriter;

public class APIServer extends ServerResource {


    public static void main(String[] args) throws Exception {

        Component c = new Component();

      // Create the HTTP server and listen on port 8182
        Server server = c.getServers().add(Protocol.HTTP, 8182);
        server.getContext().getParameters().add("keepAlive", "true");
        server.setNext(APIServer.class);
        c.start();
   }

   @Get
   @Post
   public String toString() {
//       System.out.println(Thread.currentThread().getName());
       String result = "";
       JsonRepresentation representation = new JsonRepresentation(new URLAbstract("Hello World!", "Wooooooooooooooooow!"));
       StringWriter writer = new StringWriter();
       try {
           representation.write(writer);
           result += writer.toString();
       } catch (IOException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       }
       return result;
   }

}
