package it.tika.mongodb.logger;

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
public class LogDBMongoDB implements LogDBInterface {
    private static LogDBMongoDB instance;
    private DB db;

    public static final String urlCollectionName = "log";

    private Logger logger = Logger.getLogger(LogDBMongoDB.class.getName());

    private LogDBMongoDB() throws UnknownHostException {
        Mongo mongo = new Mongo();
        db = mongo.getDB("tika");
    }

    public static LogDBMongoDB getInstance() {
        if (instance == null)
            try {
                instance = new LogDBMongoDB();
            } catch (UnknownHostException e) {
                throw new DBNotAvailableException(e);
            }

        return instance;
    }

    public void addLog(Log log) {
        insert(log);
    }

    public void insert(Log log) {
        BasicDBObject sampleObject = new BasicDBObject();
        sampleObject.put("createdDate", log.getCreatedDate());
        sampleObject.put("url", log.getUrl());

        try {
            db.getCollection(urlCollectionName).insert(sampleObject);
        } catch (MongoException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
