package it.tika.mongodb.source;

import com.goal98.tika.utils.Each;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/15/11
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SourceDBInterface {
    public Source findByURL(Source source);

    Source findByReference(String reference);

    void findAll(Each each);

}
