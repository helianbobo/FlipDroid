package it.tika;


import flipdroid.grepper.URLAbstract;

public interface URLDBInterface {
    public URLAbstract find(String url);

    public void insert(URLAbstract urlAbstract);

    void insertOrUpdate(URLAbstract urlAbstract);
}
