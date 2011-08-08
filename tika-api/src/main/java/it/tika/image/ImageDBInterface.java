package it.tika.image;

import flipdroid.grepper.TikaImage;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/15/11
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ImageDBInterface {
    public void ignore(TikaImage image);

    TikaImage find(String url);
}
