package com.goal98.flipdroid.activity;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.FromFileJSONReader;

public class SendMessageActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);
        String numbers = null;
        try {
            numbers = new FromFileJSONReader(this).resolve("number.txt");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        final String[] number = numbers.split("\n");
        for (int i = 0; i < number.length; i++) {
            String s = number[i];
            System.out.println("number"+s);
        }
        // 根据ID获取按钮
		Button button = (Button) this.findViewById(R.id.send);
		// 注册按钮被单击事件
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				// 根据ID获取手机号码编辑框
				EditText mobileText = (EditText) findViewById(R.id.mobile);

				// 获取手机号码
				String mobile = "13671673853";

				// 根据ID获取信息内容编辑框
				EditText messageText = (EditText) findViewById(R.id.message);

				// 获取信息内容
				String message = "电视片“大病的日子”本周六10:40上海教育台“健康宝典”栏目播出介绍中山医院肠癌综合诊治，电视和网络同时直播请收看.许剑民 请回复";

				// 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
				if (message != null) {
				SmsManager sms = SmsManager.getDefault();

				// 如果短信没有超过限制长度，则返回一个长度的List。
				List<String> texts = sms.divideMessage(message);

//				for (String num : number) {
				sms.sendTextMessage(mobile, null, message, null, null);
				Log.i("sms", "send a message to "+mobile);
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//
//                    }
//                }
				}
			}
		});
	}
}