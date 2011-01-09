package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sina_account);
        accountDB = new AccountDB(this);

        usernameView = (TextView)findViewById(R.id.sina_username);
        passwordView = (TextView)findViewById(R.id.sina_password);

        Button button = (Button)findViewById(R.id.sina_login);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();

                try {
                    accountDB.insertOrUpdate(username, password, Constants.TYPE_SINA_WEIBO);
                } catch (Exception e) {
                    Log.e(SinaAccountActivity.class.getName(), e.getMessage());
                }

            }
        });
    }
}