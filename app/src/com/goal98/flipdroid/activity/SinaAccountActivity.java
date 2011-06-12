package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.util.Constants;
import weibo4j.WeiboException;


public class SinaAccountActivity extends Activity {

    private static final String SINA_ACCOUNT_PREF_KEY = "sina_account";

    protected AccountDB accountDB;
    protected SourceDB sourceDB;
    protected TextView usernameView;
    protected TextView passwordView;
    protected String nextActivity;

    protected SharedPreferences preferences;
    protected TableRow logoView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            nextActivity = extras.getString("next");


        accountDB = new AccountDB(this);
        sourceDB = new SourceDB(this);
        initView();
    }

    protected void initView() {
        setContentView(R.layout.sina_account);
        usernameView = (TextView) findViewById(R.id.sina_username);
        passwordView = (TextView) findViewById(R.id.sina_password);

        Button button = (Button) findViewById(R.id.sina_login);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    saveAccount();
                    goToNextActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(SinaAccountActivity.class.getName(), e.getMessage());
                }

            }
        });
    }

    protected void saveAccount() throws WeiboException {
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

//        Weibo wb = new Weibo(username, password);
//        if (wb.verifyCredentials().isVerified()) {
        sourceDB.insert(Constants.TYPE_SINA_WEIBO, getString(R.string.my_timeline), Constants.SOURCE_HOME, getString(R.string.my_timeline_desc), null);
        accountDB.insertOrUpdate(username, password, Constants.TYPE_SINA_WEIBO);
        preferences.edit().putString(SINA_ACCOUNT_PREF_KEY, username).commit();
//        } else {
//            AlarmSender.sendInstantMessage(R.string.credentialInCorrect, SinaAccountActivity.this);
//        }

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
        accountDB.close();
        sourceDB.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }
}