package com.goal98.flipdroid.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.util.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SourceRepo {

    private Context context;

    public SourceRepo(Context context) {
        this.context = context;
    }

    public List<Map<String, String>> findSourceByType(String type) {
        //TODO: implement source file reading
        LinkedList<Map<String, String>> result = new LinkedList<Map<String, String>>();

        if (Constants.TYPE_SINA_WEIBO.equals(type)) {
            String FILENAME = Constants.FILENAME_WEIBO_RECOMMAND_SOURCE;

            try {
                AssetManager.AssetInputStream stream= (AssetManager.AssetInputStream)context.getResources().getAssets().open(FILENAME);
                String sourceJsonStr = inputStream2String(stream);
                JSONArray array = new JSONArray(sourceJsonStr);

                int count = array.length();
                for(int i = 0; i < count; i++){
                    JSONObject jsonObject = array.getJSONObject(i);
                    Map<String, String> source1 = SourceDB.buildSource(type,
                            jsonObject.getString("name"),
                            jsonObject.getString("id"),
                            jsonObject.getString("desc"));
                    result.add(source1);
                }


            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }




        return result;
    }

    private String inputStream2String(InputStream stream)throws IOException{

        BufferedReader br;

        if (null == stream) {
            return null;
        }
        br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuffer buf = new StringBuffer();
        String line;
        while (null != (line = br.readLine())) {
            buf.append(line).append("\n");
        }
        stream.close();
        return buf.toString();

    }

}
