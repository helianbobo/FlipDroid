package it.tika;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-4
 * Time: 下午4:24
 * To change this template use File | Settings | File Templates.
 */
public class SourcesResource extends ServerResource {

    @Get("JSON")
    public String getRecommend() throws IOException, JSONException {
        String keyword = this.getQuery().getFirst("kw").getValue();
        String jsonString = IOUtils.toString(new FileInputStream("recommend.json"));
        JSONArray sources = new JSONArray(jsonString);
        for (int i = 0; i < sources.length(); i++) {
             JSONObject source = (JSONObject) sources.get(i);
             String name = source.getString("name");
             String desc = source.getString("desc");

             if(name.indexOf(keyword) == -1 && desc.indexOf(keyword)==-1){
                 sources.remove(i);
                 i--;
                 if(sources.length() == 0){
                     return "{}";
                 }
             }
        }
        return sources.toString();
    }
}
