package it.tika.image;

import flipdroid.grepper.ImageService;
import flipdroid.grepper.TikaImage;
import it.tika.blacklist.BlacklistedImageDBMongoDB;
import it.tika.blacklist.BlacklistedTikaImage;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-8
 * Time: 下午9:12
 * To change this template use File | Settings | File Templates.
 */
public class TikaImageService implements ImageService {
    private ImageDBMongoDB dbInstance;

    public TikaImageService(){
        dbInstance = ImageDBMongoDB.getInstance();
    }

    @Override
    public boolean isBlacklisted(String queryURL) {
        try {
            BlacklistedTikaImage blacklistedTikaImage = BlacklistedImageDBMongoDB.getInstance().find(queryURL);
            if (blacklistedTikaImage != null)
                return true;
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public void cacheToDB(TikaImage newImage) {
        try {
            if (dbInstance != null)
                dbInstance.insert(newImage);
        } catch (Exception e) {

        }
    }

    @Override
    public TikaImage getImageInfoFromDBCache(String queryURL) {
        TikaImage tikaImage;
        try {
            dbInstance = ImageDBMongoDB.getInstance();
            tikaImage = dbInstance.find(queryURL);
        } catch (Exception e) {
            tikaImage = null;
        }
        return tikaImage;
    }
}
