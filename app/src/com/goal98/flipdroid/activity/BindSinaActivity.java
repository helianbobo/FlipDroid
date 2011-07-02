package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.goal98.flipdroid.R;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/8/11
 * Time: 7:04 PM
 * To change this template use FileType | Settings | FileType Templates.
 */
public class BindSinaActivity extends Activity {
    public static String URL = "url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.bindsina);
        WebView webview = (WebView) this.findViewById(R.id.bindSina);


        String url = this.getIntent().getStringExtra(BindSinaActivity.URL);

        WeiPaiWebViewClient weiPaiWebViewClient = new WeiPaiWebViewClient(BindSinaActivity.this);

        webview.setWebViewClient(weiPaiWebViewClient);

        webview.getSettings().setJavaScriptEnabled(true);

        webview.loadUrl(url);
        //webview.get
    }
}
