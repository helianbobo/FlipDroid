package flipdroid.grepper;

import com.mongodb.*;
import flipdroid.grepper.exception.DBNotAvailableException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class URLDBMongoDB implements URLDBInterface {

    private static URLDBInterface instance;
    public static final String urlCollectionName = "url";
    private Logger logger = Logger.getLogger(URLDBMongoDB.class.getName());

    private DB db;

    private URLDBMongoDB() throws Exception {

        Mongo mongo = new Mongo();
        db = mongo.getDB("tika");


    }

    public static URLDBInterface getInstance() throws DBNotAvailableException {
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
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB != null) {
                List<String> imageList = new ArrayList<String>();
                BasicDBList images = (BasicDBList) urlFromDB.get("images");
                if (images != null)
                    for (int i = 0; i < images.size(); i++)
                        imageList.add((String) images.get(i));

                urlAbstract = new URLAbstract(
                        url,
                        (String) urlFromDB.get("title"),
                        (String) urlFromDB.get("content"),
                        imageList);
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return urlAbstract;
    }

    public void insert(URLAbstract urlAbstract) {
        BasicDBObject urlAbstractObj = new BasicDBObject();
        urlAbstractObj.put("url", urlAbstract.getUrl());
        urlAbstractObj.put("title", urlAbstract.getTitle());
        urlAbstractObj.put("content", urlAbstract.getContent());
        urlAbstractObj.put("images", urlAbstract.getImages());

        try {
            db.getCollection(urlCollectionName).insert(urlAbstractObj);
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }

    public void insertOrUpdate(URLAbstract urlAbstract) {
        BasicDBObject query = new BasicDBObject();

        query.put("url", urlAbstract.getUrl());
        try {
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB == null) {
                insert(urlAbstract);
                return;
            }
            urlFromDB.put("title", urlAbstract.getTitle());
            urlFromDB.put("content", urlAbstract.getContent());
            urlFromDB.put("images", urlAbstract.getImages());
            db.getCollection(urlCollectionName).update(query, urlFromDB);
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }
}
