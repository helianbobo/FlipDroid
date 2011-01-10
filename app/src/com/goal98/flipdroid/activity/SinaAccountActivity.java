package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.util.Constants;


public class SinaAccountActivity extends Activity {

    private AccountDB accountDB;
    private TextView usernameView;
    private TextView passwordView;
    private String nextActivity;

    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            nextActivity = extras.getString("next");

        setContentView(R.layout.sina_account);
        accountDB = new AccountDB(this);

        usernameView = (TextView) findViewById(R.id.sina_username);
        passwordView = (TextView) findViewById(R.id.sina_password);

        Button button = (Button) findViewById(R.id.sina_login);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();

                try {
                    accountDB.insertOrUpdate(username, password, Constants.TYPE_SINA_WEIBO);
                    preferences.edit().putString("sina_account", username).commit();

                    Intent intent;
                    if (SourceActivity.class.getName().equals(nextActivity)) {
                        intent = new Intent(SinaAccountActivity.this, SourceActivity.class);
                    }else {
                        intent = new Intent(SinaAccountActivity.this, AccountListActivity.class);
                    }
                    intent.putExtra("type", Constants.TYPE_SINA_WEIBO);

                    startActivity(intent);

                } catch (Exception e) {
                    Log.e(SinaAccountActivity.class.getName(), e.getMessage());
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }
}