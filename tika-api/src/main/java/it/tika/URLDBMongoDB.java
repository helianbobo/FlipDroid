package it.tika;

import com.mongodb.*;
import it.tika.exception.DBNotAvailableException;

public class URLDBMongoDB implements URLDBInterface {

    private static URLDBInterface instance;

    private DB db;

    private URLDBMongoDB() throws Exception {

        Mongo mongo = new Mongo();
        db = mongo.getDB("tika");


    }

    public static URLDBInterface getInstance() throws DBNotAvailableException{
        if (instance == null) {
            try {
                instance = new URLDBMongoDB();
            } catch (Exception e) {
                throw new DBNotAvailableException(e);
            }
        }

        return instance;
    }

    public URLAbstract find(String url) {
        BasicDBObject query = new BasicDBObject();
        query.put("url", url);

        URLAbstract urlAbstract = null;
        try {
            DBObject urlFromDB = db.getCollection("url").findOne(query);
            urlAbstract = new URLAbstract(
                    (String) urlFromDB.get("title"),
                    (String) urlFromDB.get("content"));
        } catch (MongoException e) {
        }
        return urlAbstract;
    }

}
