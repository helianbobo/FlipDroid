package it.tika.mongodb.source;

import com.mongodb.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import it.tika.mongodb.MongoDBFactory;
import it.tika.mongodb.blacklist.BlacklistedTikaImage;
import it.tika.mongodb.logger.Log;
import org.bson.types.ObjectId;

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
public class SourceDBMongoDB implements SourceDBInterface {
    private static SourceDBMongoDB instance;
    private DB db;

    public static final String urlCollectionName = "source";

    private Logger logger = Logger.getLogger(SourceDBMongoDB.class.getName());

    private SourceDBMongoDB() throws UnknownHostException {
        db = MongoDBFactory.getDBInstance();
    }

    public static SourceDBMongoDB getInstance() {
        if (instance == null)
            try {
                instance = new SourceDBMongoDB();
            } catch (UnknownHostException e) {
                throw new DBNotAvailableException(e);
            }

        return instance;
    }

    public Source findByURL(Source source) {
        BasicDBObject query = new BasicDBObject();

        query.put("url", source.getUrl());

        Source existingSource = null;
        try {
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB != null) {
                existingSource = new Source();
                existingSource.setUrl(source.getUrl());
                existingSource.setId(((ObjectId) urlFromDB.get("_id")).toStringMongod());
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return existingSource;
    }
}
