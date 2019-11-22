package com.app.boysrun;

import com.app.boysrun.misc.GlobalVar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetServerIpActivity extends Activity {

	private EditText ed_ip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setserverip);
		TextView tv_ip = (TextView) findViewById(R.id.tv_ip);
		ed_ip = (EditText) findViewById(R.id.ed_ip);
		String serverIp = GlobalVar.getConfig("serverIp", null);
		if(serverIp != null){
			ed_ip.setText(serverIp);
		}
		Button btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ed_ip.getText().length() > 0) {
					String ip = ed_ip.getText().toString();
					GlobalVar.setConfig("serverIp", ip);
					Toast.makeText(SetServerIpActivity.this, "已储存", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});
	}
}
