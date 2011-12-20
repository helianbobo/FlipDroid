package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.FromFileJSONReader;
import com.goal98.flipdroid.model.RecommendSource;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.GestureUtil;
import com.goal98.flipdroid.util.NetworkUtil;
import com.goal98.tika.common.TikaConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.transform.URIResolver;
import java.io.IOException;
import java.util.Map;


public class CoverActivity extends Activity {

    private boolean goingToSleep;

    private String deviceId;
    public static final int WIRELESS_SETTING = 1;
    private RecommendSourceDB recommendSourceDB;
    private final int delayMillis = 1200;
    private final int durationMillis = delayMillis - 300;

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case WIRELESS_SETTING:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.nonetwork)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CoverActivity.this.finish();
                            }
                        });
                dialog = builder.create();
                break;

            default:
                dialog = null;
        }
        return dialog;
    }

    private String TAG = this.getClass().getName();
    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cover);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constants.TIKA_HOST = preferences.getString(getString(R.string.key_tika_host), Constants.TIKA_HOST);

        final View view = this.findViewById(R.id.flipbar);
        view.setVisibility(View.GONE);
        view.post(new Runnable() {
            public void run() {
                DeviceInfo.getInstance(CoverActivity.this);
            }
        });
        recommendSourceDB = RecommendSourceDB.getInstance(this);
        initDefaultSource();

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();
        Log.v(this.getClass().getName(), "deviceId=" + deviceId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                goToNextActivity();
            }
        }, delayMillis);
    }

    private void initDefaultSource() {
        SourceDB sourceDB = new SourceDB(getApplicationContext());

        final Cursor cursor = sourceDB.findAll();
        startManagingCursor(cursor);
        if (cursor.getCount() == 0) {

            try {
                RecommendSource recommendSource = recommendSourceDB.findSourceByType(TikaConstants.TYPE_DEFAULT);
                String sourceName = TikaConstants.TYPE_DEFAULT + "_" + Constants.RECOMMAND_SOURCE_SUFFIX;
                String sourceJsonStr = null;
                if (recommendSource == null) {//read local file as a failover process
                    FromFileJSONReader fromFileSourceResolver = new FromFileJSONReader(this);
                    sourceJsonStr = fromFileSourceResolver.resolve(sourceName);
                    recommendSourceDB.insert(sourceJsonStr, sourceName);
                } else {
                    sourceJsonStr = recommendSource.getBody();
                }
                JSONArray defaultSourceList = new JSONArray(sourceJsonStr);
                for (int i = 0; i < defaultSourceList.length(); i++) {
                    JSONObject defaultSource = (JSONObject) defaultSourceList.get(i);
                    String contentURL = "";
                    if (defaultSource.has("content_url"))
                        contentURL = defaultSource.getString("content_url");

                    final Map<String, String> source = SourceDB.buildSource(defaultSource.getString("type"),
                            defaultSource.getString("name"),
                            defaultSource.getString("id"),
                            defaultSource.getString("desc"),
                            defaultSource.getString("image_url"),
                            contentURL,
                            "");
                    sourceDB.insert(source);
                }

            } catch (Exception e) {
                Log.w(TAG, e.getMessage(), e);
            } finally {
                sourceDB.close();
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0 && !goingToSleep) {
                    if (GestureUtil.flipRight(event))
                        goToNextActivity();
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean emulator = deviceId != null && deviceId.startsWith("0000");
                if (!goingToSleep && emulator) {
                    goToNextActivity();
                }
                break;

        }
        return true;
    }

    private void goToNextActivity() {

        boolean tips_read = preferences.getBoolean(Constants.PREFERENCE_TIPS_READ, false);
        if (tips_read) {
            startActivity(new Intent(this, IndexActivity.class));
        } else
            startActivity(new Intent(this, TipsActivity.class));
        goingToSleep = true;
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        goingToSleep = false;


    }

    @Override
    protected void onStart() {
        super.onStart();
        final View view = findViewById(R.id.flipbar);
        view.setVisibility(View.VISIBLE);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animation.setDuration(durationMillis);
        view.startAnimation(animation);
    }


}