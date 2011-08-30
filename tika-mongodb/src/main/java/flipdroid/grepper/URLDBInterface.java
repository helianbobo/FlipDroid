package flipdroid.grepper;


import java.util.List;

public interface URLDBInterface {
    public URLAbstract find(String url);

    public void insert(URLAbstract urlAbstract);

    void insertOrUpdate(URLAbstract urlAbstract);

    List<URLAbstract> findBySource(String sourceId,int limit);
}
