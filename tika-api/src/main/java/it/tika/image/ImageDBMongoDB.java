package it.tika.image;

import com.mongodb.*;
import flipdroid.grepper.TikaImage;
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
public class ImageDBMongoDB implements ImageDBInterface {
    private static ImageDBMongoDB instance;
    private DB db;

    public static final String urlCollectionName = "image";

    private Logger logger = Logger.getLogger(ImageDBMongoDB.class.getName());

    private ImageDBMongoDB() throws UnknownHostException {
        Mongo mongo = new Mongo();
        db = mongo.getDB("tika");
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
        BasicDBObject sampleObject = new BasicDBObject();
        sampleObject.put("size", image.getSize());
        sampleObject.put("url", image.getUrl());
        sampleObject.put("height", image.getHeight());
        sampleObject.put("width", image.getWidth());

        try {
            db.getCollection(urlCollectionName).insert(sampleObject);
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
