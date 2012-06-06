package flipdroid.grepper;

import com.goal98.tika.utils.Each;
import com.mongodb.*;
import flipdroid.grepper.exception.DBNotAvailableException;
import it.tika.mongodb.MongoDBFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class URLDBMongoDB implements URLDBInterface {

    private static URLDBInterface instance;
    public static final String urlCollectionName = "url_abstract";
    private Logger logger = Logger.getLogger(URLDBMongoDB.class.getName());

    private DB db;

    private URLDBMongoDB() throws Exception {

        db = MongoDBFactory.getDBInstance();


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

    public URLAbstract fromDBObjectToURLAbstract(DBObject urlFromDB) {
        List<String> imageList = new ArrayList<String>();
        if (urlFromDB.get("images") instanceof BasicDBList) {
            BasicDBList images = (BasicDBList) urlFromDB.get("images");
            if (images != null)
                for (int i = 0; i < images.size(); i++)
                    imageList.add((String) images.get(i));
        }
        URLAbstract urlAbstract = new URLAbstract(
                (String) urlFromDB.get("url"),
                (String) urlFromDB.get("title"),
                (String) urlFromDB.get("content"),
                imageList);

        Object time = urlFromDB.get("time");
        if (time instanceof Date)
            urlAbstract.setCreateDate((Date) time);
        else
            urlAbstract.setCreateDate(new Date());

        urlAbstract.setReferencedFrom((String) urlFromDB.get("reference"));
        urlAbstract.setClazz((String) urlFromDB.get("sogou_class"));
        return urlAbstract;
    }

    public List<URLAbstract> findByCategory(String category, int limit, boolean imageOnly) {
        BasicDBObject categoryQuery = new BasicDBObject();
        categoryQuery.put("sogou_class", category);

        BasicDBObject gt0Query = new BasicDBObject();
        if (imageOnly) {
            gt0Query.put("$gt", 0);
        } else {
            gt0Query.put("$gt", -1);
        }

        BasicDBObject imageQuery = new BasicDBObject();
        imageQuery.put("imageSize", gt0Query);

        BasicDBObject andQuery = new BasicDBObject();
        ArrayList<DBObject> list = new ArrayList<DBObject>();
        list.add(imageQuery);
        list.add(categoryQuery);

        andQuery.put("$and", list);

        List<URLAbstract> urlAbstracts = new ArrayList<URLAbstract>();
        try {
            DBCursor urlFromDB = null;
            if (limit > 0) {
                urlFromDB = db.getCollection(urlCollectionName).find(andQuery).sort(new BasicDBObject("time", -1)).limit(limit);
            } else {
                urlFromDB = db.getCollection(urlCollectionName).find(andQuery).sort(new BasicDBObject("time", -1));
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

    @Override
    public void findAllInCat(String category, Each each) {
        BasicDBObject categoryQuery = new BasicDBObject();

        Pattern p = Pattern.compile(category);

        categoryQuery.put("sogou_class", p);

//        BasicDBObject gt0Query = new BasicDBObject();
//        if(tru){
//            gt0Query.put("$gt", 0);
//        }else{
//            gt0Query.put("$gt",-1);
//        }
//
//        BasicDBObject imageQuery = new BasicDBObject();
//        imageQuery.put("imageSize", gt0Query);
//
//        BasicDBObject andQuery = new BasicDBObject();
//        ArrayList<DBObject> list = new ArrayList<DBObject>();
//        list.add(imageQuery);
//        list.add(categoryQuery);

//        andQuery.put("$and", list);

        try {
            DBCursor urlFromDB = null;
            urlFromDB = db.getCollection(urlCollectionName).find(categoryQuery).sort(new BasicDBObject("time", -1));
            while (urlFromDB != null && urlFromDB.hasNext()) {
                each.doWith(urlFromDB.next());
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
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

        Pattern p = Pattern.compile(image);

        query.put("content", p);      //contains image

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

    @Override
    public void findAll(Each each) {
        try {
            DBCursor urlFromDB = db.getCollection(urlCollectionName).find().sort(new BasicDBObject("time", -1));
            while (urlFromDB != null && urlFromDB.hasNext()) {
                DBObject url = urlFromDB.next();
                each.doWith(url);
            }
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
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
        urlAbstractObj.put("sogou_class", urlAbstract.getSogouClass());
        urlAbstractObj.put("reference", urlAbstract.getReferencedFrom());
        urlAbstractObj.put("indexURL", urlAbstract.getIndexURL());
        urlAbstractObj.put("imageSize", urlAbstract.getImages().size());
        try {
            db.getCollection(urlCollectionName).insert(urlAbstractObj);
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }

    public void insertOrUpdate(URLAbstract urlAbstract) {
        BasicDBObject query = new BasicDBObject();

        BasicDBObject or1 = new BasicDBObject();
        or1.put("indexURL", urlAbstract.getUrl());

        BasicDBObject or2 = new BasicDBObject();
        or2.put("url", urlAbstract.getUrl());

        ArrayList<DBObject> list = new ArrayList<DBObject>();
        list.add(or1);
        list.add(or2);
        query.put("$or", list);

        try {
            DBObject urlFromDB = db.getCollection(urlCollectionName).findOne(query);
            if (urlFromDB == null) {
                insert(urlAbstract);
                return;
            }
            urlFromDB.put("title", urlAbstract.getTitle());
            urlFromDB.put("content", urlAbstract.getContent());
            urlFromDB.put("images", urlAbstract.getImages());
            urlFromDB.put("imageSize", urlAbstract.getImages().size());
            urlFromDB.put("reference", urlAbstract.getReferencedFrom());
            urlFromDB.put("sogou_class", urlAbstract.getSogouClass());
            urlFromDB.put("last_update", new Date());
            db.getCollection(urlCollectionName).update(query, urlFromDB);
        } catch (MongoException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }
}
