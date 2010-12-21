package com.goal98.flipdroid.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.goal98.flipdroid.util.Cache;
import com.goal98.flipdroid.util.CacheFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class InternetImageView extends ImageView {

    public static final String BITMAP_CACHE_NAME = "BITMAP_CACHE_NAME";

    private int sampleSize = 1;

    private HttpURLConnection conn;

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public InternetImageView(Context context, URL url) {
        super(context);
        loadImage(url);

    }

    public InternetImageView(Context context, URL url, int sampleSize) {
        super(context);
        loadImage(url);
        this.sampleSize = sampleSize;

    }

    private URL url;

    public void loadImage(URL url) {
        this.url = url;
        loadImage();
    }

    public void loadImage() {
        DownloadImageTask task = new DownloadImageTask();
        task.execute(url);
    }

    private Bitmap loadBitmap(URL url, BitmapFactory.Options options) {

        Cache bitmapCache = CacheFactory.getInstance().getCache(BITMAP_CACHE_NAME);
        Bitmap bitmapFromCache = (Bitmap) bitmapCache.get(url);
        if (bitmapFromCache == null) {

            Bitmap bitmap = null;
            InputStream in;
            try {
                in = OpenHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(in, null, options);
                if (in != null)
                    in.close();
                if(conn != null){
                    conn.disconnect();
                }
            } catch (IOException e1) {
                Log.e(this.getClass().getName(), e1.getMessage(), e1);
            }
            return bitmap;
        } else {
            return bitmapFromCache;
        }

    }

    private InputStream OpenHttpConnection(URL url) throws IOException {
        InputStream inputStream = null;

        conn = (HttpURLConnection)url.openConnection();

        try {
            conn.setUseCaches(true);
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
            }

        } catch (Exception ex) {
            Log.e(this.getClass().getName(), ex.getMessage(), ex);
        }
        return inputStream;
    }

    private class DownloadImageTask extends AsyncTask<URL, Integer, Bitmap> {
        protected Bitmap doInBackground(URL... urls) {
            BitmapFactory.Options bmOptions;
            bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = sampleSize;

            Bitmap bm = loadBitmap(url, bmOptions);
            return bm;
        }

        protected void onPostExecute(Bitmap bitmap) {
            setImageBitmap(bitmap);
        }
    }


}
