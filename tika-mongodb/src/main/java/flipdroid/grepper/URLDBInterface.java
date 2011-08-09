package flipdroid.grepper;



public interface URLDBInterface {
    public URLAbstract find(String url);

    public void insert(URLAbstract urlAbstract);

    void insertOrUpdate(URLAbstract urlAbstract);
}
