package it.tika.mongodb.image;


import com.mongodb.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import it.tika.mongodb.MongoDBFactory;

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
public class ImageDBMongoDB implements ImageDBInterface {
    private static ImageDBMongoDB instance;
    private DB db;

    public static final String urlCollectionName = "image";

    private Logger logger = Logger.getLogger(ImageDBMongoDB.class.getName());

    private ImageDBMongoDB() throws UnknownHostException {
        try {
            db = MongoDBFactory.getDBInstance();

            BasicDBObject index = new BasicDBObject();
            index.put("url", 1);
            db.getCollection(urlCollectionName).ensureIndex(index,"idx_image_url",true);

        } catch (UnknownHostException e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MongoException e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static ImageDBMongoDB getInstance() {
        if (instance == null)
            try {
                instance = new ImageDBMongoDB();
            } catch (UnknownHostException e) {
                throw new DBNotAvailableException(e);
            }

        return instance;
    }

    public void ignore(TikaImage image) {
        insert(image);
    }

    public void insert(TikaImage image) {
        BasicDBObject queryObject = new BasicDBObject();
        queryObject.put("url", image.getUrl());

        BasicDBObject object = new BasicDBObject();
        object.put("size", image.getSize());
        object.put("url", image.getUrl());
        object.put("height", image.getHeight());
        object.put("width", image.getWidth());

        try {
            DBCollection collection = db.getCollection(urlCollectionName);
            collection.update(queryObject, object, true, false);
        } catch (MongoException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public TikaImage find(String url) {
        BasicDBObject query = new BasicDBObject();

        query.put("url", url);

        TikaImage image = null;
        try {
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB != null) {
                image = new TikaImage();
                image.setUrl(url);
                image.setWidth((Integer) urlFromDB.get("width"));
                image.setHeight((Integer) urlFromDB.get("height"));
                image.setSize((Integer) urlFromDB.get("size"));
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return image;
    }
}
