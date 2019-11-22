///**
// *  How to align a two buttons layout left and right in android: http://stackoverflow.com/questions/11222916/how-to-align-a-two-buttons-layout-left-and-right-in-android
// * 
// */
//
//package com.app.boysrun;
//
//import java.text.NumberFormat;
//
//import android.app.Activity;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.app.boysrun.misc.GlobalVar;
//import com.app.boysrun.misc.Misc;
//import com.app.boysrun.ormdb.data.User;
//
///**
// * 運動數據結果
// *
// */
//public class ResultActivity extends Activity {
//	
//	LinearLayout L1;
//	LinearLayout L2;
//	LinearLayout L3;
//	
//	ImageView userImg;
//	TextView txv_result;
//	
//	/*****/
//	
//	LinearLayout L_DIS;
//	LinearLayout L_CAL;
//	LinearLayout L_STEP;
//	
//	TextView txv_dis;
//	TextView txv_dis_size;
//	
//	TextView txv_cal;
//	TextView txv_cal_size;
//	
//	TextView txv_step;
//	TextView txv_step_size;
//	/*****/
//	
//	Button btn_race_again;
//	Button btn_close;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_result);
//
//		findView();
//		initSize();
//		initWidget();
//	}
//
//	private void findView() {
//		L1 = (LinearLayout) findViewById(R.id.L1);
//		L2 = (LinearLayout) findViewById(R.id.L2);
//		L3 = (LinearLayout) findViewById(R.id.L3);
//		userImg = (ImageView) findViewById(R.id.userImg);
//		txv_result = (TextView) findViewById(R.id.txv_result);
//		
//		
//		L_DIS = (LinearLayout) findViewById(R.id.L_DIS);
//		L_CAL = (LinearLayout) findViewById(R.id.L_CAL);
//		L_STEP = (LinearLayout) findViewById(R.id.L_STEP);
//		
//		txv_dis = (TextView) findViewById(R.id.txv_dis);
//		txv_dis_size = (TextView) findViewById(R.id.txv_dis_size);
//		
//		txv_cal = (TextView) findViewById(R.id.txv_cal);
//		txv_cal_size = (TextView) findViewById(R.id.txv_cal_size);
//		
//		txv_step = (TextView) findViewById(R.id.txv_step);
//		txv_step_size = (TextView) findViewById(R.id.txv_step_size);
//		
//		btn_race_again = (Button) findViewById(R.id.btn_race_again); 
//		btn_close = (Button) findViewById(R.id.btn_close);
//	}
//
//	private void initSize() {
//		GlobalVar.setMargin(userImg, 0, 20, 0, 20);
//		
//		GlobalVar.setMargin(L_DIS, 20, 0, 20, 0);
//		GlobalVar.setMargin(L_CAL, 20, 0, 20, 0);
//		GlobalVar.setMargin(L_STEP, 20, 0, 20, 0);
//		
//		GlobalVar.setSize(L1, 880, 240);
//		GlobalVar.setSize(L2, 880, 120);
//		GlobalVar.setSize(L3, 880, 100);
//
//		txv_result.setTextSize(28);		
//		GlobalVar.setSize(btn_race_again, 260, 100);
//		GlobalVar.setSize(btn_close, 260, 100);
//		
//		txv_dis.setTextSize(26);
//		txv_dis_size.setTextSize(26);
//		txv_cal.setTextSize(26);
//		txv_cal_size.setTextSize(26);
//		txv_step.setTextSize(26);
//		txv_step_size.setTextSize(26);
//		
//		GlobalVar.setMargin(txv_dis, 0, 5, 0, 0);
//		GlobalVar.setMargin(txv_dis_size, 0, 5, 0, 0);
//		
//		GlobalVar.setMargin(txv_cal, 0, 5, 0, 0);
//		GlobalVar.setMargin(txv_cal_size, 0, 5, 0, 0);
//		
//		GlobalVar.setMargin(txv_step, 0, 5, 0, 0);
//		GlobalVar.setMargin(txv_step_size, 0, 5, 0, 0);
//		
//		btn_race_again.setTextSize(26);
//		btn_close.setTextSize(26);
//	}
//	
//	private void initWidget() {
//		txv_result.setTextColor(Color.BLACK);
//		
//		final NumberFormat numFormat = NumberFormat.getNumberInstance();
//        numFormat.setMaximumFractionDigits(3);
//		
//        Bundle inbundle = getIntent().getExtras();
//		User user = (User) inbundle.getSerializable("bluetooth.user");
//		
//		userImg.setBackgroundResource(Misc.getUserPhotoResID(user.getPhotoIdx()));
//		
//		try {
//			int finishRanking = (int) inbundle.getInt("bluetooth.finishRanking");
//			txv_result.setText("恭喜你本次比赛第" + finishRanking + "名,本次运动数据");
//		} catch(Exception e) {
//			txv_result.setText("本次运动数据");
//		}
//
//		txv_dis_size.setText(Misc.genStrFloatByDigit(DataActivity.distance_val / 1000, 3) + "公里");
//		txv_cal_size.setText(Misc.genStrFloatByDigit(DataActivity.calorie_val, 3) + "卡");
//		txv_step_size.setText(Math.round(DataActivity.step_val) + "步");
//		
//		btn_race_again.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//				DataActivity.dataCallBack.resetGame();
//			}
//		});
//		
//		btn_close.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//				DataActivity.dataCallBack.finishDataActivity();
//			}
//		});
//		
//		btn_close.requestFocus();
//	}
//	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			return true;
//		default:
//			break;
//		}
//		
//		return super.onKeyDown(keyCode, event);
//	}
//
//}