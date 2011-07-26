package it.tika.blacklist;

import com.mongodb.*;
import it.tika.exception.DBNotAvailableException;

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
public class BlacklistedImageDBMongoDB implements BlacklistedImageDBInterface {
    private static BlacklistedImageDBMongoDB instance;
    private DB db;

    public static final String urlCollectionName = "blacklistedImage";

    private Logger logger = Logger.getLogger(BlacklistedImageDBMongoDB.class.getName());

    private BlacklistedImageDBMongoDB() throws UnknownHostException {
        Mongo mongo = new Mongo();
        db = mongo.getDB("tika");
    }

    public static BlacklistedImageDBMongoDB getInstance() {
        if (instance == null)
            try {
                instance = new BlacklistedImageDBMongoDB();
            } catch (UnknownHostException e) {
                throw new DBNotAvailableException(e);
            }

        return instance;
    }

    public void ignore(BlacklistedTikaImage imageBlacklisted) {
        insert(imageBlacklisted);
    }

    public void insert(BlacklistedTikaImage imageBlacklisted) {
        BasicDBObject sampleObject = new BasicDBObject();
        sampleObject.put("url", imageBlacklisted.getUrl());

        try {
            db.getCollection(urlCollectionName).insert(sampleObject);
        } catch (MongoException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public BlacklistedTikaImage find(String url) {
        BasicDBObject query = new BasicDBObject();

        query.put("url", url);

        BlacklistedTikaImage imageBlacklisted = null;
        try {
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB != null) {
                imageBlacklisted = new BlacklistedTikaImage();
                imageBlacklisted.setUrl(url);
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return imageBlacklisted;
    }
}
