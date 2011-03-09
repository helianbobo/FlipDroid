package it.tika;


import it.tika.exception.DBNotAvailableException;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class URLAbstractResource extends ServerResource {

    private URLDBInterface getDB(){
        return URLDBMongoDB.getInstance();
    }

    public void setDB(URLDBInterface db){
        //TODO: database implementation injection
    }

    private URLAbstract find(){

        URLAbstract result = null;
        Form form = this.getQuery();
        try {
            String url = form.getFirst("url").getValue();
            String urlDecoded =  java.net.URLDecoder.decode(url,"UTF-8") ;
            result = getDB().find(urlDecoded);

            if(result == null){
                String rawDocument = URLRawRepo.getInstance().fetch(urlDecoded);
                if(rawDocument == null){
                    getLogger().log(Level.INFO, "Can't fetch document from url:" + urlDecoded);
                }else{
                    result = ContentExtractor.getInstance().extract(rawDocument);
                    if (result != null){
                        getDB().insert(result);
                    }
                }
            }

        } catch (UnsupportedEncodingException e) {
            getLogger().log(Level.INFO, e.getMessage(), e);
        } catch (NullPointerException ne){
            getLogger().log(Level.SEVERE, ne.getMessage(), ne);
        }
        return result;
    }

    @Get("JSON")
    public String toJson() {
        String result = "";
        URLAbstract urlAbstract = null;
        try {
            urlAbstract = find();
        } catch (DBNotAvailableException e) {
            getLogger().log(Level.INFO,e.getMessage());
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            return null;
        }


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
            getLogger().log(Level.INFO, e.getMessage(), e);
        }
        return result;
    }
}
