package com.goal98.flipdroid.model;

import com.goal98.flipdroid.db.SourceDB;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SourceRepo {

    public List<Map<String, String>> findSourceByType(String type){
        //TODO: implement source file reading
        LinkedList<Map<String, String>> result = new LinkedList<Map<String, String>>();

        Map<String, String> source1 = buildSource("helianbobo", "1702755335", "A HOI MOD Author. A Grails Plugin developer.");

        result.add(source1);
        return result;
    }

    public static Map<String, String> buildSource(String name, String id, String desc){
        Map<String, String> result = new HashMap<String, String>();
        result.put(SourceDB.KEY_SOURCE_NAME, name);
        result.put(SourceDB.KEY_SOURCE_ID, id);
        result.put(SourceDB.KEY_SOURCE_DESC, desc);
        return  result;
    }

}
