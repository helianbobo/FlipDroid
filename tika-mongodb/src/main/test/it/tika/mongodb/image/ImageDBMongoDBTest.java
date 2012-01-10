package it.tika.mongodb.image;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import it.tika.mongodb.MongoDBFactory;
import org.junit.Test;

import static org.junit.Assert.*;
public class ImageDBMongoDBTest {

    private DB db;

    public ImageDBMongoDBTest() throws Exception{
        db = MongoDBFactory.getDBInstance();
    }

    @Test
    public void testInsert() throws Exception {
        DBCollection collection = db.getCollection(ImageDBMongoDB.urlCollectionName);
        collection.drop();

        ImageDBMongoDB imageDBMongoDB = ImageDBMongoDB.getInstance();
        TikaImage image = new TikaImage();
        String url = "http://somedomain/1.jpg";
        image.setUrl(url);

        imageDBMongoDB.insert(image);

        image = new TikaImage();
        image.setUrl(url);
        imageDBMongoDB.insert(image);

        BasicDBObject object = new BasicDBObject();
        object.put("url", url);

        assertEquals(1, collection.find(object).size());

        image = new TikaImage();
        image.setUrl("http://somedomain/2.jpg");
        imageDBMongoDB.insert(image);

        assertEquals(2, collection.find().size());

    }
}
