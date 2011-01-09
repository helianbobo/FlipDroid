package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.goal98.flipdroid.R;


public class SinaAccountActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sina_account);

        Button button = (Button)findViewById(R.id.sina_login);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Log.e(this.getClass().getName(), "Clicked!");
            }
        });
    }
}