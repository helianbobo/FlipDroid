package flipdroid.grepper;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TitleExtractor {
    String fireAbstract(byte[] data, Charset charset);
}
