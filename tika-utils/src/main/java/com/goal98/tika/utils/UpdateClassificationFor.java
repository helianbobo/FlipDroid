package com.goal98.tika.utils;

import com.mongodb.DBObject;
import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.URLDBInterface;
import flipdroid.grepper.URLDBMongoDB;
import it.tika.mongodb.source.Source;
import it.tika.mongodb.source.SourceDBInterface;
import it.tika.mongodb.source.SourceDBMongoDB;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-6-2
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
public class UpdateClassificationFor {
    public static void main(String[] args) {

        final URLDBInterface urldb = URLDBMongoDB.getInstance();
        final SourceDBInterface sourceDBInterface = SourceDBMongoDB.getInstance();
        urldb.findAllInCat(args[0], new Each() {
            @Override
            public void doWith(DBObject urlFromDB) {
                URLAbstract urlAbstract = urldb.fromDBObjectToURLAbstract(urlFromDB);
                String reference = urlAbstract.getReferencedFrom();
                Source s = sourceDBInterface.findByReference(reference);
                if(s == null){
                    System.out.println(urlAbstract.getTitle() + "passed");
                    return;
                }
                String sogouClass = s.getClazz();

                urlAbstract.setClazz(sogouClass);
                urldb.insertOrUpdate(urlAbstract);
                System.out.println(urlAbstract.getTitle()+"updated to "+urlAbstract.getSogouClass());
            }
        });

    }
}
