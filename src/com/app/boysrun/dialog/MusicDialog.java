//package com.app.boysrun.dialog;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//
//import com.app.boysrun.DataActivity;
//import com.app.boysrun.R;
//import com.app.boysrun.misc.GlobalVar;
//import com.app.boysrun.misc.MiscMusic;
//
//public class MusicDialog extends AlertDialog {
//	RelativeLayout RL;
//	Button btn1;
//	Button btn2;
//	Button btn3;
//
//	public MusicDialog(Context context) {
//		super(context);
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.dlg_music);
//
//		findView();
//		initView();
//	}
//
//	private void findView() {
//		RL = (RelativeLayout) findViewById(R.id.RL);
//		
//		btn1 = (Button) findViewById(R.id.btn1);
//		btn2 = (Button) findViewById(R.id.btn2);
//		btn3 = (Button) findViewById(R.id.btn3);
//	}
//
//	private void initView() {
//		GlobalVar.setSize(RL, 1000, 300);
//		
//		btn1.setOnClickListener(createClickListener(R.raw.boysrun_musicdemo));
//		btn2.setOnClickListener(createClickListener(R.raw.boysrun_musicdemo));
//		btn3.setOnClickListener(createClickListener(R.raw.boysrun_musicdemo));
//	}
//	
//	private View.OnClickListener createClickListener(final int rawId) {
//		return new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				gobackDataActivity(rawId);
//			}
//		};
//	}
//	
//	private void gobackDataActivity(final int rawId) {
//		DataActivity.dataCallBack.initMusic(rawId);
//		MusicDialog.this.dismiss();
//	}
//
//}
