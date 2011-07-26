package it.tika.blacklist;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/15/11
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlacklistedImageDBInterface {
    public void ignore(BlacklistedTikaImage imageBlacklisted);

    BlacklistedTikaImage find(String url);
}
