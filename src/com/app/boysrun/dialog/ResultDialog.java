package com.app.boysrun.dialog;

import java.text.NumberFormat;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.boysrun.DataActivity;
import com.app.boysrun.R;
import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.ormdb.data.User;

public class ResultDialog extends AlertDialog {
	
	Context context;
	DataActivity.MODE mode;
	BluetoothDevice device;
	User user;
	int finishRanking = -1; //最終排名
	
	LinearLayout L1;
	LinearLayout L2;
	LinearLayout L3;
	
	ImageView userImg;
	TextView txv_result;
	
	/*****/
	
	LinearLayout L_DIS;
	LinearLayout L_CAL;
	LinearLayout L_STEP;
	
	TextView txv_dis;
	TextView txv_dis_size;
	
	TextView txv_cal;
	TextView txv_cal_size;
	
	TextView txv_step;
	TextView txv_step_size;
	/*****/
	
	Button btn_race_again;
	Button btn_close;
	
	
	public ResultDialog(Context context, DataActivity.MODE mode, BluetoothDevice device, User u, int rank) {
		super(context);
		this.context = context;
		this.device = device;
		this.mode = mode;
		this.user = u;
		finishRanking = rank;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_result);
		
		findView();
		initSize();
		initWidget();
	}
	
	private void findView() {
		L1 = (LinearLayout) findViewById(R.id.L1);
		L2 = (LinearLayout) findViewById(R.id.L2);
		L3 = (LinearLayout) findViewById(R.id.L3);
		userImg = (ImageView) findViewById(R.id.userImg);
		txv_result = (TextView) findViewById(R.id.txv_result);
		
		
		L_DIS = (LinearLayout) findViewById(R.id.L_DIS);
		L_CAL = (LinearLayout) findViewById(R.id.L_CAL);
		L_STEP = (LinearLayout) findViewById(R.id.L_STEP);
		
		txv_dis = (TextView) findViewById(R.id.txv_dis);
		txv_dis_size = (TextView) findViewById(R.id.txv_dis_size);
		
		txv_cal = (TextView) findViewById(R.id.txv_cal);
		txv_cal_size = (TextView) findViewById(R.id.txv_cal_size);
		
		txv_step = (TextView) findViewById(R.id.txv_step);
		txv_step_size = (TextView) findViewById(R.id.txv_step_size);
		
		btn_race_again = (Button) findViewById(R.id.btn_race_again); 
		btn_close = (Button) findViewById(R.id.btn_close);
	}

	private void initSize() {
		GlobalVar.setMargin(userImg, 0, 20, 0, 20);
		
		GlobalVar.setMargin(L_DIS, 20, 0, 20, 0);
		GlobalVar.setMargin(L_CAL, 20, 0, 20, 0);
		GlobalVar.setMargin(L_STEP, 20, 0, 20, 0);
		
		GlobalVar.setSize(L1, 880, 240);
		GlobalVar.setSize(L2, 880, 120);
		GlobalVar.setSize(L3, 880, 100);

		txv_result.setTextSize(28);		
		GlobalVar.setSize(btn_race_again, 260, 100);
		GlobalVar.setSize(btn_close, 260, 100);
		
		txv_dis.setTextSize(26);
		txv_dis_size.setTextSize(26);
		txv_cal.setTextSize(26);
		txv_cal_size.setTextSize(26);
		txv_step.setTextSize(26);
		txv_step_size.setTextSize(26);
		
		GlobalVar.setMargin(txv_dis, 0, 5, 0, 0);
		GlobalVar.setMargin(txv_dis_size, 0, 5, 0, 0);
		
		GlobalVar.setMargin(txv_cal, 0, 5, 0, 0);
		GlobalVar.setMargin(txv_cal_size, 0, 5, 0, 0);
		
		GlobalVar.setMargin(txv_step, 0, 5, 0, 0);
		GlobalVar.setMargin(txv_step_size, 0, 5, 0, 0);
		
		btn_race_again.setTextSize(26);
		btn_close.setTextSize(26);
	}
	
	/**
	 * @return 產生步數圈數單位 
	 */
	private String genStrStepUnit() {
		if(device.getName().contains("UMHD")) {
			return "圈";
		} else {
			return "步";
		}
	}
	
	private void initWidget() {
		final NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setMaximumFractionDigits(3);
		
		txv_result.setTextColor(Color.BLACK);
		
		if(mode.equals(DataActivity.MODE.GAME)) {
			txv_result.setText("恭喜你本次比赛第" + finishRanking + "名 / 本次运动数据");
		} else {
			btn_race_again.setVisibility(View.GONE);
			txv_result.setText("本次运动数据");	
		}
		
		userImg.setBackgroundResource(Misc.getUserPhotoResID(user.getPhotoIdx()));

		txv_dis_size.setText(Misc.genStrFloatByDigit(DataActivity.distance_val / 1000, 3) + "公里");
		txv_cal_size.setText(Misc.genStrFloatByDigit(DataActivity.calorie_val, 3) + "卡");
		txv_step_size.setText(Math.round(DataActivity.step_val) + genStrStepUnit());
		
		btn_race_again.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//finish();
				dismiss();
				DataActivity.dataCallBack.resetGame();
			}
		});
		
		btn_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//finish();
				dismiss();
				DataActivity.dataCallBack.finishDataActivity();
			}
		});
		
		btn_close.requestFocus();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		default:
			break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
}
