package flipdroid.grepper;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * An Extractor instance extract a part of the raw content and its output is set back into its input which is an instance of URLAbstract
 */
public interface Extractor {
    public void extract(URLAbstract urlAbstract);
}
