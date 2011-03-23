package it.tika;

import it.tika.cases.Case;
import it.tika.cases.CaseRepositoryDBMongoDB;
import it.tika.exception.DBNotAvailableException;
import it.tika.util.Util;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.Date;
import java.util.Random;
import java.util.logging.Level;

public class URLAbstractRatingResource extends ServerResource {

    @Get("JSON")
    public String toJson() {
        Form form = this.getQuery();
        String correct = form.getFirstValue("tick");
        String url = form.getFirstValue("url");
        String json = form.getFirstValue("sample");
        int caseId = new Random().nextInt(99999999);

        Case sample = new Case();
        sample.setCreatedDate(new Date());
        sample.setUrl(url);
        sample.setGood(Boolean.parseBoolean(correct));
        sample.setSampleBody(json);
        sample.setId(caseId);
        try {
            CaseRepositoryDBMongoDB.getInstance().addCase(sample);
        } catch (DBNotAvailableException e) {
            getLogger().log(Level.INFO, e.getMessage(), e);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("result", "succeed");
            jsonObject.accumulate("caseId", caseId);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return Util.writeJSON(jsonObject);

    }
}
