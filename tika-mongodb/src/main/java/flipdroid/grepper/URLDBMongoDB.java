package flipdroid.grepper;

import com.mongodb.*;
import flipdroid.grepper.exception.DBNotAvailableException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class URLDBMongoDB implements URLDBInterface {

    private static URLDBInterface instance;
    public static final String urlCollectionName = "url_abstract";
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

        query.put("indexURL", url);

        URLAbstract urlAbstract = null;
        try {
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB != null) {
                urlAbstract = fromDBObjectToURLAbstract(urlFromDB);
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return urlAbstract;
    }

    private URLAbstract fromDBObjectToURLAbstractURLOnly(DBObject urlFromDB) {
        URLAbstract urlAbstract = new URLAbstract();
        urlAbstract.setUrl((String) urlFromDB.get("url"));
        return urlAbstract;
    }

    private URLAbstract fromDBObjectToURLAbstract(DBObject urlFromDB) {
        List<String> imageList = new ArrayList<String>();
        BasicDBList images = (BasicDBList) urlFromDB.get("images");
        if (images != null)
            for (int i = 0; i < images.size(); i++)
                imageList.add((String) images.get(i));

        URLAbstract urlAbstract = new URLAbstract(
                (String) urlFromDB.get("url"),
                (String) urlFromDB.get("title"),
                (String) urlFromDB.get("content"),
                imageList);
        urlAbstract.setCreateDate((Date) urlFromDB.get("time"));
        urlAbstract.setReferencedFrom((String) urlFromDB.get("reference"));
        return urlAbstract;
    }

    public List<URLAbstract> findBySource(String sourceId, int limit) {
        BasicDBObject query = new BasicDBObject();

        query.put("reference", sourceId);

        List<URLAbstract> urlAbstracts = new ArrayList<URLAbstract>();
        try {
            DBCursor urlFromDB = null;
            if (limit > 0) {
                urlFromDB = db.getCollection(urlCollectionName).find(query).sort(new BasicDBObject("time", -1)).limit(limit);
            } else {
                urlFromDB = db.getCollection(urlCollectionName).find(query).sort(new BasicDBObject("time", -1));
            }
            while (urlFromDB != null && urlFromDB.hasNext()) {
                DBObject url = urlFromDB.next();
                URLAbstract urlAbstract = fromDBObjectToURLAbstract(url);
                urlAbstracts.add(urlAbstract);
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return urlAbstracts;
    }

    public List<URLAbstract> findByContainsImage(String image) {
        BasicDBObject query = new BasicDBObject();

        query.put("content", "/" + image + "/");      //contains image

        List<URLAbstract> urlAbstracts = new ArrayList<URLAbstract>();
        try {
            DBCursor urlFromDB = db.getCollection(urlCollectionName).find(query);
            while (urlFromDB != null && urlFromDB.hasNext()) {
                DBObject url = urlFromDB.next();
                URLAbstract urlAbstract = fromDBObjectToURLAbstractURLOnly(url);
                urlAbstracts.add(urlAbstract);
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return urlAbstracts;
    }

    public void insert(URLAbstract urlAbstract) {
        BasicDBObject urlAbstractObj = new BasicDBObject();
        urlAbstractObj.put("url", urlAbstract.getUrl());
        urlAbstractObj.put("title", urlAbstract.getTitle());
        urlAbstractObj.put("content", urlAbstract.getContent());
        urlAbstractObj.put("images", urlAbstract.getImages());
        urlAbstractObj.put("type", "user");
        urlAbstractObj.put("time", new Date());
        urlAbstractObj.put("state", "success");
        urlAbstractObj.put("reference", urlAbstract.getReferencedFrom());
        urlAbstractObj.put("indexURL", urlAbstract.getIndexURL());
        try {
            db.getCollection(urlCollectionName).insert(urlAbstractObj);
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }

    public void insertOrUpdate(URLAbstract urlAbstract) {
        BasicDBObject query = new BasicDBObject();

        query.put("indexURL", urlAbstract.getUrl());
        try {
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB == null) {
                insert(urlAbstract);
                return;
            }
            urlFromDB.put("title", urlAbstract.getTitle());
            urlFromDB.put("content", urlAbstract.getContent());
            urlFromDB.put("images", urlAbstract.getImages());
            urlFromDB.put("time", new Date());
            db.getCollection(urlCollectionName).update(query, urlFromDB);
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }
}
