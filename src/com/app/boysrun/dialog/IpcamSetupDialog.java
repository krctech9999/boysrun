package com.app.boysrun.dialog;

import com.app.boysrun.DataActivity;
import com.app.boysrun.R;
import com.app.boysrun.misc.GlobalVar;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class IpcamSetupDialog extends AlertDialog {
	RelativeLayout RL;
	EditText edit1;
	Button btn_ok;

	public IpcamSetupDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dlg_input_ipcam);
		
		findView();
		initView();
	}

	private void findView() {
		RL = (RelativeLayout) findViewById(R.id.RL);
		edit1 = (EditText) findViewById(R.id.edit1);
		btn_ok = (Button) findViewById(R.id.btn_ok);
	}

	private void initView() {
		GlobalVar.setSize(RL, 1000, 300);
		GlobalVar.setSize(edit1, 900, 50);
		
		// 使Dialog可跳出鍵盤
		this.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				
		edit1.setText(GlobalVar.getConfig("webcam", "http://admin:admin@210.242.155.15/video.cgi"));
		
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String vurl = edit1.getText().toString(); 
				
				if (!vurl.equals(GlobalVar.getConfig("webcam", ""))) {
					GlobalVar.setConfig("webcam", vurl);
				}
				
				DataActivity.dataCallBack.startVlcVideo();
				IpcamSetupDialog.this.dismiss();
			}
		});
	}

	

}
