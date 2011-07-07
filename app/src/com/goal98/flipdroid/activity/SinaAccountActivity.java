package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.client.OAuth;
import com.goal98.flipdroid.client.UserInfo;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.sina.OAuthHolder;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import weibo4j.WeiboException;


public class SinaAccountActivity extends Activity {


    protected TextView usernameView;
    protected TextView passwordView;
    protected String nextActivity;

    protected SharedPreferences preferences;
    protected TableRow logoView;

    String promptText = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            nextActivity = extras.getString("next");

        promptText = getIntent().getExtras().getString("PROMPTTEXT");
        initView();
    }


    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case GOTO_OAUTH:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(promptText)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FlipdroidApplications application = (FlipdroidApplications) getApplication();
                                OAuth oauth = new OAuth();
                                application.setOauth(oauth);
                                ////System.out.println("OAuthHolder.oauth" + application + oauth);
                                boolean result = oauth.RequestAccessToken(SinaAccountActivity.this, "flipdroid://SinaAccountSaver");
                                if (!result)
                                    AlarmSender.sendInstantMessage(R.string.networkerror, SinaAccountActivity.this);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SinaAccountActivity.this.finish();
                            }
                        });
                dialog = builder.create();
                break;

            default:
                dialog = null;
        }
        return dialog;
    }

    public static final int GOTO_OAUTH = 1;

    protected void initView() {
        showDialog(GOTO_OAUTH);
    }

    protected void goToNextActivity() {
        Intent intent;
        if (SinaSourceSelectionActivity.class.getName().equals(nextActivity)) {
            intent = new Intent(this, SinaSourceSelectionActivity.class);
        } else {
            intent = new Intent(this, AccountListActivity.class);
        }
        intent.putExtra("type", Constants.TYPE_SINA_WEIBO);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }
}