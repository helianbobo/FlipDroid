package it.tika.cases;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import flipdroid.grepper.exception.DBNotAvailableException;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/15/11
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaseRepositoryDBMongoDB implements CaseRepositoryDBInterface {
    private static CaseRepositoryDBInterface instance;
    private DB db;

    public static final String urlCollectionName = "sample";

    private Logger logger = Logger.getLogger(CaseRepositoryDBMongoDB.class.getName());

    private CaseRepositoryDBMongoDB() throws UnknownHostException {
        Mongo mongo = new Mongo();
        db = mongo.getDB("tika");
    }

    public static CaseRepositoryDBInterface getInstance() {
        if (instance == null)
            try {
                instance = new CaseRepositoryDBMongoDB();
            } catch (UnknownHostException e) {
                throw new DBNotAvailableException(e);
            }

        return instance;
    }

    public int addCase(Case sample) {
        insert(sample);
        return sample.getId();
    }

    public void insert(Case sample) {
        BasicDBObject sampleObject = new BasicDBObject();
        sampleObject.put("createdDate", sample.getCreatedDate());
        sampleObject.put("url", sample.getUrl());
        sampleObject.put("sampleBody", sample.getSampleBody());
        sampleObject.put("classify", sample.isGood());
        sampleObject.put("id", sample.getId());

        try {
            db.getCollection(urlCollectionName).insert(sampleObject);
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }
}
