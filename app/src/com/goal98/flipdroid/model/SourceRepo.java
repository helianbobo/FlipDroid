package com.goal98.flipdroid.model;

import android.content.Context;
import android.util.Log;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SourceRepo {

    private Context context;

    public SourceRepo(Context context) {
        this.context = context;
    }

    public List<Map<String, String>> findSourceByType(String type) {
        LinkedList<Map<String, String>> result = new LinkedList<Map<String, String>>();
        String sourceName = type.toUpperCase() + "_" + Constants.RECOMMAND_SOURCE_SUFFIX;
        System.out.println("sourceName:"+sourceName);
        JSONArray array = getSourceJSON(type, result, sourceName);
        System.out.println(type);
        if (Constants.TYPE_SINA_WEIBO.equals(type)) {
            int count = array.length();
            for (int i = 0; i < count; i++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = array.getJSONObject(i);
                    Map<String, String> source1 = SourceDB.buildSource(type,
                            jsonObject.getString("name"),
                            jsonObject.getString("id"),
                            jsonObject.getString("desc"),
                            jsonObject.getString("image_url"));
                    result.add(source1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (Constants.TYPE_RSS.equals(type)) {
            int count = array.length();
            for (int i = 0; i < count; i++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = array.getJSONObject(i);
                    Map<String, String> source1 = SourceDB.buildSource(type,
                            jsonObject.getString("name"),
                            jsonObject.getString("id"),
                            jsonObject.getString("desc"),
                            jsonObject.getString("image_url"),
                            jsonObject.getString("content_url"));
                    result.add(source1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private JSONArray getSourceJSON(String type, LinkedList<Map<String, String>> result, String sourceName) {
        try {
            String sourceJsonStr = new FromFileSourceResolver(context).resolve(sourceName);
            return new JSONArray(sourceJsonStr);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        return null;
    }
}
