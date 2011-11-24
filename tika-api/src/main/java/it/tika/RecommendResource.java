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
public class RecommendResource extends ServerResource {

    @Get("JSON")
    public String getRecommend() throws IOException, JSONException {
        String type = this.getQuery().getFirst("type").getValue();
        if("RSS".equals(type)){
            return IOUtils.toString(new FileInputStream("RSS_RECOMMAND_SOURCE_DATA.json"));
        }
        return "not found...";
    }
}
