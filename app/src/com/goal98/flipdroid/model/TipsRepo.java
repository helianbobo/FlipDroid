package com.goal98.flipdroid.model;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/2/11
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TipsRepo {
    private Context context;
    public FromFileJSONReader fromFileSourceResolver;
    public static final String TIP_ASSET_NAME = "TIPS.json";

    public TipsRepo(Context context) {
        this.context = context;
        fromFileSourceResolver = new FromFileJSONReader(context);
    }

    public List<Tip> getTips() {
        List<Tip> tips = new ArrayList<Tip>();
        try {
            String tipsJSON = fromFileSourceResolver.resolve(TIP_ASSET_NAME);
            JSONArray tipsArray = new JSONArray(tipsJSON);
            int count = tipsArray.length();
            for (int i = 0; i < count; i++) {
                JSONObject jsonObject = tipsArray.getJSONObject(i);
                String tipText = jsonObject.getString("name");
                int tipId = jsonObject.getInt("id");
                Tip tip = new Tip(tipText,tipId);
                tips.add(tip);
            }
            return tips;
        } catch (IOException e) {
            return new ArrayList<Tip>();
        } catch (JSONException e) {
            return new ArrayList<Tip>();
        }
    }
}
