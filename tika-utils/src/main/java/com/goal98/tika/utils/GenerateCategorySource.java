package com.goal98.tika.utils;

import com.mongodb.DBObject;
import it.tika.mongodb.source.SourceDBInterface;
import it.tika.mongodb.source.SourceDBMongoDB;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-6-2
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
public class GenerateCategorySource {
    public static void main(String[] args) {

        final SourceDBInterface sourceDb = SourceDBMongoDB.getInstance();
        final Set<String> classes = new HashSet<String>();
        sourceDb.findAll(new Each() {
            @Override
            public void doWith(DBObject urlFromDB) {
                classes.add((String) urlFromDB.get("class"));
            }
        });
        for (String clazz : classes) {
            System.out.println(clazz);
        }
    }
}
