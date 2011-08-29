package it.tika.mongodb.source;

import it.tika.mongodb.logger.Log;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/15/11
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SourceDBInterface {
    public Source findByURL(Source source);
}
