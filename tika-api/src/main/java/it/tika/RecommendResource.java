package it.tika;

import com.goal98.tika.common.TikaConstants;
import it.tika.mongodb.recommend.Recommend;
import it.tika.mongodb.recommend.RecommendMongoDB;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-4
 * Time: 下午4:24
 * To Change this template use File | Settings | File Templates.
 */
public class RecommendResource extends ServerResource {

    @Get("JSON")
    public String getRecommend() throws IOException, JSONException {
        Form request = (Form) getRequest().getAttributes().get("org.restlet.http.headers");
        String ifModifiedSince = request.getValues("If-Modified-Since");
        long modifiedSince = -1;
        if (ifModifiedSince != null)
            try {
                modifiedSince = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").parse(ifModifiedSince).getTime();
            } catch (ParseException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        String type = this.getQuery().getFirst("type").getValue();
        Recommend recommend = RecommendMongoDB.getInstance().find(type);
        if (recommend != null) {
            long lastModified = recommend.getLastModified().getTime();
            System.out.println("lm:" + lastModified);
            System.out.println("ms:" + modifiedSince);
            if (lastModified > modifiedSince + 1000) {
                Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");
                if (responseHeaders == null) {
                    responseHeaders = new Form();
                    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
                }
                responseHeaders.add("Last-Modified-Tika", lastModified + "");
                return recommend.getBody();
            } else {
                getResponse().setStatus(new Status(304));
            }
        }

        return "";
    }
}
