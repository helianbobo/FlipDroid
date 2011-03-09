package it.tika;


public interface URLDBInterface {
    public URLAbstract find(String url);

    public void insert(URLAbstract urlAbstract);
}
