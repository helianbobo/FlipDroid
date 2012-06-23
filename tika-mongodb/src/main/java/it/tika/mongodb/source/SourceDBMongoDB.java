package it.tika.mongodb.source;

import com.goal98.tika.utils.Each;
import com.mongodb.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import it.tika.mongodb.MongoDBFactory;
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

    public SourceDBMongoDB() throws UnknownHostException {
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
                existingSource.setClazz(urlFromDB.get("class").toString());
                existingSource.setType(urlFromDB.get("type").toString());
                existingSource.setUrl(source.getUrl());
                existingSource.setId(((ObjectId) urlFromDB.get("_id")).toStringMongod());
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return existingSource;
    }

    public Source findByReference(String reference) {
        BasicDBObject query = new BasicDBObject();

        if(reference!=null)
            try {
                query.put("_id", new ObjectId(reference));
            } catch (Exception e) {
                return null;
            }
        else
            return null;
        try {
            DBObject sourceFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (sourceFromDB != null) {
                Source existingSource = new Source();
                existingSource.setClazz(sourceFromDB.get("class").toString());
                existingSource.setType(sourceFromDB.get("type").toString());
                existingSource.setUrl(sourceFromDB.get("url").toString());
                existingSource.setId(((ObjectId) sourceFromDB.get("_id")).toStringMongod());
                return existingSource;
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void findAll(Each each) {
        DBCursor urlFromDB = null;
        urlFromDB = db.getCollection(urlCollectionName).find().sort(new BasicDBObject("time", -1));
        while (urlFromDB != null && urlFromDB.hasNext()) {
            each.doWith(urlFromDB.next());
        }
    }
}
