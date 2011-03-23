package it.tika.util;


import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static Logger log = Logger.getLogger(Util.class.getName());

    public static String writeJSON(JSONObject jsonObject) {
        JsonRepresentation representation = new JsonRepresentation(jsonObject);
        return writeJSONInternal(representation);
    }

    public static String writeJSON(JSONArray jsonObject) {


        JsonRepresentation representation = new JsonRepresentation(jsonObject);
        return writeJSONInternal(representation);
    }

    private static String writeJSONInternal(JsonRepresentation representation) {
        String result = "";
        StringWriter writer = new StringWriter();
        try {
            representation.write(writer);
            result += writer.toString();
        } catch (IOException e) {
            log.log(Level.INFO, e.getMessage(), e);
        }
        return result;
    }
}
