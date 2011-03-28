package com.goal98.flipdroid.activity;

import android.app.Activity;
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
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.Constants;


public class SinaAccountActivity extends Activity {

    private AccountDB accountDB;
    private SourceDB sourceDB;
    private TextView usernameView;
    private TextView passwordView;
    private String nextActivity;

    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            nextActivity = extras.getString("next");

        setContentView(R.layout.sina_account);
        accountDB = new AccountDB(this);
        sourceDB = new SourceDB(this);

        usernameView = (TextView) findViewById(R.id.sina_username);
        passwordView = (TextView) findViewById(R.id.sina_password);

        Button button = (Button) findViewById(R.id.sina_login);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                try {
                    saveAccount();

                    goToNextActivity();

                } catch (Exception e) {
                    Log.e(SinaAccountActivity.class.getName(), e.getMessage());
                }

            }
        });
    }

    private void saveAccount() {
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        sourceDB.insert(Constants.TYPE_SINA_WEIBO, getString(R.string.my_timeline), Constants.SOURCE_HOME, getString(R.string.my_timeline_desc));
        accountDB.insertOrUpdate(username, password, Constants.TYPE_SINA_WEIBO);
        preferences.edit().putString("sina_account", username).commit();
    }

    private void goToNextActivity() {
        Intent intent;
        if (SourceSelectionActivity.class.getName().equals(nextActivity)) {
            intent = new Intent(this, SourceSelectionActivity.class);
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