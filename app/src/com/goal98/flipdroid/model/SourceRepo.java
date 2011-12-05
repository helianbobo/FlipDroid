package com.goal98.flipdroid.model;

import android.content.Context;
import android.util.Log;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SourceRepo {

    private Context context;
    private FromFileJSONReader fromFileSourceResolver;
    public static final String KEY_NAME_SAMPLES = "samples";
    public static final String KEY_NAME_GROUP = "group";
    private RecommendSourceDB recommendSourceDB;

    public SourceRepo(Context context) {
        this.context = context;
        fromFileSourceResolver = new FromFileJSONReader(context);
        recommendSourceDB = RecommendSourceDB.getInstance(context);
    }

    public static GroupedSource group(List<Map<String, String>> sourceList) {
        Map<Map<String, String>, List<Map<String, String>>> groupsChildrenMap = new HashMap<Map<String, String>, List<Map<String, String>>>();

        List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> children = new ArrayList<List<Map<String, String>>>();

        for (int i = 0; i < sourceList.size(); i++) {
            Map<String, String> childValueMap = sourceList.get(i);
            String cat = childValueMap.get("cat");
            String sourceName = childValueMap.get(Source.KEY_SOURCE_NAME);
            final Map<String, String> group = new HashMap<String, String>();
            group.put(KEY_NAME_GROUP, cat);
            group.put(KEY_NAME_SAMPLES, "");
            int index = -1;
            for (int j = 0; j < groups.size(); j++) {
                Map<String, String> groupMap = groups.get(j);
                if (groupMap.get(KEY_NAME_GROUP).equals(cat)) {
                    index = j;
                    break;
                }
            }
            if (index == -1) {
                groups.add(group);
                group.put(KEY_NAME_SAMPLES, group.get(KEY_NAME_SAMPLES).concat(sourceName));
            } else {
                final Map<String, String> groupMap = groups.get(index);
                groupMap.put(KEY_NAME_SAMPLES, groupMap.get(KEY_NAME_SAMPLES).concat(", " + sourceName));
            }
            int tmp = 0;
            index = -1;

            for (Map.Entry<Map<String, String>, List<Map<String, String>>> entry : groupsChildrenMap.entrySet()) {
                final Map<String, String> key = entry.getKey();
                List<Map<String, String>> value = entry.getValue();
                if (key.get(KEY_NAME_GROUP).equals(cat)) {
                    index = tmp;
                    value.add(childValueMap);
                    break;
                }
                tmp++;
            }

            if (index == -1) {
                final ArrayList<Map<String, String>> maps = new ArrayList<Map<String, String>>();
                maps.add(childValueMap);
                groupsChildrenMap.put(group, maps);
            }
        }
        children = new ArrayList<List<Map<String, String>>>();
        for (int i = 0; i < groups.size(); i++) {
            Map<String, String> group = groups.get(i);
            List<Map<String, String>> list_childs = new ArrayList<Map<String, String>>();

            for (Map.Entry<Map<String, String>, List<Map<String, String>>> entry : groupsChildrenMap.entrySet()) {
                final Map<String, String> key = entry.getKey();
                List<Map<String, String>> value = entry.getValue();
                if (key.get(KEY_NAME_GROUP).equals(group.get(KEY_NAME_GROUP))) {
                    list_childs.addAll(value);
                }
            }
            children.add(list_childs);
        }
        GroupedSource gs = new GroupedSource();
        gs.setGroups(groups);
        gs.setChildren(children);
        return gs;
    }

    public List<Map<String, String>> findSourceByType(String type) {
        LinkedList<Map<String, String>> result = new LinkedList<Map<String, String>>();
        JSONArray array = getSourceJSON(type);
        if (array != null && Constants.TYPE_SINA_WEIBO.equals(type)) {
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
                            jsonObject.getString("cat"));
                    result.add(source1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (array != null && Constants.TYPE_RSS.equals(type)) {
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
                            jsonObject.getString("content_url"),
                            jsonObject.getString("cat"));
                    result.add(source1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private JSONArray getSourceJSON(String type) {
        try {
            String sourceJsonStr = null;
            RecommendSource recommendSource = recommendSourceDB.findSourceByType(type);
            String sourceName = type.toUpperCase() + "_" + Constants.RECOMMAND_SOURCE_SUFFIX;
            if (recommendSource == null) {//read local file as a failover process
                sourceJsonStr = fromFileSourceResolver.resolve(sourceName);
                recommendSourceDB.insert(sourceJsonStr, sourceName);
            } else {
                sourceJsonStr = recommendSource.getBody();
            }

            return new JSONArray(sourceJsonStr);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        return null;
    }

    public GroupedSource findGroupedSourceByType(String type) {
        return group(findSourceByType(type));
    }
}
