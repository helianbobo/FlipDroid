package it.tika;


import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class URLAbstractResource extends ServerResource {



    private URLAbstract find(){
        URLAbstract result = null;
        Form form = this.getQuery();
        try {
            String url = form.getFirst("url").getValue();
            String urlDecoded =  java.net.URLDecoder.decode(url,"UTF-8") ;
            getLogger().log(Level.INFO, urlDecoded);
            result = new URLAbstract("Hello World!", "Wooooooooooooooooow!");
        } catch (UnsupportedEncodingException e) {
            getLogger().log(Level.INFO, e.getMessage(), e);
        } catch (NullPointerException ne){
            getLogger().log(Level.SEVERE, ne.getMessage(), ne);
        }
        return result;
    }

    @Get("JSON")
    public String toJson() {
//       System.out.println(Thread.currentThread().getName());
        String result = "";
        URLAbstract urlAbstract = find();
        if(urlAbstract == null){
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }
        JsonRepresentation representation = new JsonRepresentation(urlAbstract);
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
