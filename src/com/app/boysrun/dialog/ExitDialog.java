package com.app.boysrun.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.boysrun.DataActivity;
import com.app.boysrun.R;
import com.app.boysrun.misc.GlobalVar;

public class ExitDialog extends AlertDialog {
	
	DataActivity.MODE mode;
	
	//UI
	TextView txv_title;
	TextView txv_msg;
	Button btn_again;
	Button btn_ok;

	public ExitDialog(Context context, DataActivity.MODE mode) {
		super(context);
		this.mode = mode;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_exit);

		findView();
		initView();
	}

	private void findView() {
		txv_title = (TextView) findViewById(R.id.txv_title);
		txv_msg = (TextView) findViewById(R.id.txv_msg);;
		btn_again = (Button) findViewById(R.id.btn_again);
		btn_ok = (Button) findViewById(R.id.btn_ok);

	}

	private void initView() {
		if(!mode.equals(DataActivity.MODE.GAME)) {
			btn_again.setVisibility(View.INVISIBLE);
		}
		
		GlobalVar.setSize(btn_again, 250, 80);
		GlobalVar.setSize(btn_ok, 250, 80);
		
		// TODO Auto-generated method stub
		txv_title.setText("结束运动");
		txv_msg.setText("确认结束跑步?");
		
		txv_title.setTextSize(30);
		txv_msg.setTextSize(26);
		
		btn_again.setTextSize(26);
		btn_ok.setTextSize(26);
		
		btn_again.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				DataActivity.dataCallBack.resetGame();
			}
		});
		
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				//DataActivity.dataCallBack.gotoActivity(R-esultActivity.class);
				DataActivity.dataCallBack.gotoResultDialog();
			}
		});
	}
}
