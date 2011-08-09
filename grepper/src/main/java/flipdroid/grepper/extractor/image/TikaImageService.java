package flipdroid.grepper.extractor.image;

import it.tika.mongodb.blacklist.BlacklistedImageDBMongoDB;
import it.tika.mongodb.blacklist.BlacklistedTikaImage;
import it.tika.mongodb.image.ImageDBMongoDB;
import it.tika.mongodb.image.TikaImage;

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
