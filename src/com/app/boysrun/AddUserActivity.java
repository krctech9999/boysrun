package com.app.boysrun;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;

public class AddUserActivity extends Activity {
	private Context context;
	int imgIdx = 0;
	boolean isMale = true;
	
	// widget
	private LinearLayout LL_TOP2;
	private TextView txv_choose_img;
	
	private LinearLayout LL1;
	private LinearLayout LL2;
	
	private LinearLayout L1;
	private LinearLayout L2;
	private LinearLayout L3;
	private LinearLayout L4;
	private LinearLayout L5;

	private ImageView img1;
	
	private TextView txv_user;
	private EditText edit_name;
	
	private TextView txv_user_height;
	private EditText edit_user_height;
	
	private TextView txv_user_weight;
	private EditText edit_user_weight;
	
	private TextView txv_user_birth;
	private EditText edit_user_birth;
	
	private TextView txv_user_gender; //性別
	private TextView edit_user_gender;
	
	private Button btn_ok;
	private Button btn_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_add_user);

		findview();
		setSize();
		setView();
	}

	private void findview() {
		LL_TOP2 = (LinearLayout) findViewById(R.id.LL_TOP2);
		txv_choose_img = (TextView) findViewById(R.id.txv_choose_img);
		
		
		LL1 = (LinearLayout) findViewById(R.id.LL1);
		LL2 = (LinearLayout) findViewById(R.id.LL2);
		
		L1 = (LinearLayout) findViewById(R.id.L1);
		L2 = (LinearLayout) findViewById(R.id.L2);
		L3 = (LinearLayout) findViewById(R.id.L3);
		L4 = (LinearLayout) findViewById(R.id.L4);
		L5 = (LinearLayout) findViewById(R.id.L5);

		img1 = (ImageView) findViewById(R.id.img1);
		/************/
		txv_user = (TextView) findViewById(R.id.txv_user);
		edit_name = (EditText) findViewById(R.id.edit_name);
		
		txv_user_height = (TextView) findViewById(R.id.txv_user_height);
		edit_user_height = (EditText) findViewById(R.id.edit_user_height);
		
		txv_user_weight = (TextView) findViewById(R.id.txv_user_weight);
		edit_user_weight = (EditText) findViewById(R.id.edit_user_weight);
		
		txv_user_birth = (TextView) findViewById(R.id.txv_user_birth);
		edit_user_birth = (EditText) findViewById(R.id.edit_user_birth);
		
		txv_user_gender = (TextView) findViewById(R.id.txv_user_gender); //性別
		edit_user_gender = (TextView) findViewById(R.id.edit_user_gender);
		/*************/

		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_return = (Button) findViewById(R.id.btn_return);
	}

	private void setSize() {
		GlobalVar.setHeight(LL_TOP2, 80);
		GlobalVar.setMargin(txv_choose_img, 45, 15, 0, 0);
		
		GlobalVar.setSize(LL1, 1215, 350);
		GlobalVar.setMargin(LL1, 65, 0, 0, 0);
		
		GlobalVar.setPadding(L1, 25, 0, 0, 0);
		GlobalVar.setPadding(L2, 25, 0, 0, 0);
		GlobalVar.setPadding(L3, 25, 0, 0, 0);
		GlobalVar.setPadding(L4, 25, 0, 0, 0);
		GlobalVar.setPadding(L5, 25, 0, 0, 0);
		
		//-------------------------------------------------
		GlobalVar.setSize(img1, 350, 350);
		GlobalVar.setPadding(img1, 20, 20, 20, 20);

		GlobalVar.setSize(btn_ok, -1, -1);
		GlobalVar.setSize(btn_return, -1, -1);
		
		GlobalVar.setMargin(btn_return, 40, 40, 0, 0);
		GlobalVar.setMargin(btn_ok, 40, 40, 0, 0);
	}

	private void setView() {
		txv_choose_img.setTextSize(28);
		txv_choose_img.setTextColor(Color.WHITE);
		
		btn_ok.setTextSize(23);
		btn_return.setTextSize(23);
		
		txv_user.setTextSize(25);
		edit_name.setTextSize(25);
		
		txv_user_height.setTextSize(25);
		edit_user_height.setTextSize(25);

		txv_user_weight.setTextSize(25);
		edit_user_weight.setTextSize(25);
		
		txv_user_birth.setTextSize(25);
		edit_user_birth.setTextSize(25);
			
		txv_user_gender.setTextSize(25); //性別
		edit_user_gender.setTextSize(25);
		
		
		txv_user.setTextColor(Color.WHITE);
		txv_user_height.setTextColor(Color.WHITE);
		txv_user_weight.setTextColor(Color.WHITE);
		txv_user_birth.setTextColor(Color.WHITE);
		txv_user_gender.setTextColor(Color.WHITE);
		
		edit_name.setTextColor(Color.WHITE);
		edit_name.setBackgroundColor(Color.TRANSPARENT);
		
		img1.setFocusable(true);
		
		txv_user.setFocusable(false);
		edit_name.setFocusable(true);
		
		edit_user_gender.setFocusable(true);
		
		btn_ok.setFocusable(true);
		btn_return.setFocusable(true);
		
		img1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imgIdx++;
				if(imgIdx > GlobalVar.userSize - 1) {
					imgIdx = 0;
				}
				
				Log.d("XXX", "photo click " + " imgIdx = " + imgIdx);				
				
				img1.setImageResource(Misc.getUserPhotoResID(imgIdx));				
			}
		});
		
		edit_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					edit_name.setCursorVisible(true);
					
					if(edit_name.getText().toString().length() == 0) {
						edit_name.setText(" "); //show EditText cursor
					}
				}
			}
		});
		
		edit_user_gender.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isMale = !isMale;
				if(isMale) {
					edit_user_gender.setText("男");
				} else {
					edit_user_gender.setText("女");
				}
			}
		});
		
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = edit_name.getText().toString().trim();
				
				if(userName.length() < 1){
					myToast("請輸入使用者名");
					return;
				}
				
				try {
					if(GlobalVar.userDao.queryBuilder().
							where().eq("name", userName).query().size() > 0) {
						myToast("使用者已存在");
						return;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
					myToast("資料庫錯誤");
					return;
				}
				
				EntryActivity.entryCallBack.addUser(userName, imgIdx);
				finish();
			}
		});
		
		btn_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void myToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}

