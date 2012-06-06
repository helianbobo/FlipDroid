package flipdroid.grepper;


import com.goal98.tika.utils.Each;
import com.mongodb.DBObject;

import java.util.List;

public interface URLDBInterface {
    public URLAbstract find(String url);

    public void insert(URLAbstract urlAbstract);

    void insertOrUpdate(URLAbstract urlAbstract);

    List<URLAbstract> findBySource(String sourceId,int limit);

    List<URLAbstract> findByContainsImage(String image);

    void findAll(Each each);


    URLAbstract fromDBObjectToURLAbstract(DBObject urlFromDB);

    List<URLAbstract> findByCategory(String category, int limit, boolean imageOnly);

    void findAllInCat(String arg, Each each);

}
