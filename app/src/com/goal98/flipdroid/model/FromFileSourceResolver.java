package com.goal98.flipdroid.model;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FromFileSourceResolver {

    private Context context;

    public FromFileSourceResolver(Context context) {
        this.context = context;
    }

    public String resolve(String sourceName) throws IOException {
        AssetManager.AssetInputStream stream = (AssetManager.AssetInputStream) context.getResources().getAssets().open(sourceName);
        return inputStream2String(stream);
    }

    private String inputStream2String(InputStream stream) throws IOException {

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