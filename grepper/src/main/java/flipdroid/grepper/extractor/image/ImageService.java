package flipdroid.grepper.extractor.image;

import it.tika.mongodb.image.TikaImage;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-8
 * Time: 下午9:21
 * To change this template use File | Settings | File Templates.
 */
public interface ImageService {
    boolean isBlacklisted(String queryURL);

    void cacheToDB(TikaImage newImage);

    TikaImage getImageInfoFromDBCache(String queryURL);
}
