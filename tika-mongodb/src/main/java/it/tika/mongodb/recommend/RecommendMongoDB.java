package it.tika.mongodb.recommend;


import com.mongodb.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import it.tika.mongodb.MongoDBFactory;
import it.tika.mongodb.image.ImageDBInterface;
import it.tika.mongodb.image.TikaImage;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/15/11
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecommendMongoDB implements RecommendInterface {
    private static RecommendMongoDB instance;
    private DB db;

    public static final String urlCollectionName = "RecommendSource";

    private Logger logger = Logger.getLogger(RecommendMongoDB.class.getName());

    private RecommendMongoDB() throws UnknownHostException {
        try {
            db = MongoDBFactory.getDBInstance();
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MongoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static RecommendMongoDB getInstance() {
        if (instance == null)
            try {
                instance = new RecommendMongoDB();
            } catch (UnknownHostException e) {
                throw new DBNotAvailableException(e);
            }

        return instance;
    }



    @Override
    public Recommend find(String type) {
        BasicDBObject query = new BasicDBObject();

        query.put("type", type);

        Recommend rcommend = null;
        try {
            DBObject recommendFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (recommendFromDB != null) {
                rcommend = new Recommend();
                rcommend.setBody((String) recommendFromDB.get("body"));
                Date date = (Date) recommendFromDB.get("lastModified");
                rcommend.setLastModified(date);
                rcommend.setType(type);
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return rcommend;
    }
}
