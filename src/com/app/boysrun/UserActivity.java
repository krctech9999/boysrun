package com.app.boysrun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.ormdb.data.User;

public class UserActivity extends Activity {
	
	private final String TAG = "UserActivity"; 
	private Context context;
	
	//使用者
	private User user = null;
	
	//widget
	private LinearLayout LL;
	private RelativeLayout R1;
	
	private LinearLayout L_LEVEL;
	
	private LinearLayout L2;
	
	private ImageView img1;
	private TextView txv_name;
	private TextView txv_level;
	
	private Button btn_scan;
	private Button btn_chart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		context = this;

		findview();
		setSize();
		init();
	}

	private void findview() {
		LL = (LinearLayout) findViewById(R.id.LL);
		R1 = (RelativeLayout) findViewById(R.id.R1);
		L_LEVEL = (LinearLayout) findViewById(R.id.L_LEVEL);
		
		L2 = (LinearLayout) findViewById(R.id.L2);
		
		img1 = (ImageView) findViewById(R.id.img1);
		txv_name = (TextView) findViewById(R.id.txv_name);
		txv_level = (TextView) findViewById(R.id.txv_level);
		
		btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_chart = (Button) findViewById(R.id.btn_chart);
	}

	private void setSize() {
		GlobalVar.setSize(LL, 1280, 720);

		GlobalVar.setSize(R1, 200, 520);  
		GlobalVar.setSize(img1, 200, 520);
		GlobalVar.setSize(L_LEVEL, 135, 135);
		
		GlobalVar.setSize(txv_level, 135, 50);
		GlobalVar.setSize(txv_name, 135, 50);
		
		GlobalVar.setMargin(txv_level, 20, 0, 0, 0);
		GlobalVar.setMargin(txv_name, 20, 0, 0, 0);
		
		/****************************************************************/
		
		GlobalVar.setSize(L2, 764, 720);
		GlobalVar.setSize(btn_scan, 382, 300); GlobalVar.setSize(btn_chart, 382, 300);
		
		GlobalVar.setMargin(R1, 75, 50, 0, 0);
		GlobalVar.setMargin(L2, 95, 50, 0, 0);
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getSerializable("bluetooth.user");
		
		txv_level.setTextSize(23);
		txv_name.setTextSize(23);
		txv_level.setTextColor(Color.parseColor("#D70051"));
		txv_name.setTextColor(Color.BLACK);
		
		btn_scan.setTextSize(60);
		btn_chart.setTextSize(60);
		
		btn_scan.setTextColor(Color.WHITE);
		btn_chart.setTextColor(Color.WHITE);
		
		txv_name.setText(user.getName());
		txv_level.setText("LV" + user.getLevel());
		
		img1.setImageResource(Misc.getUserPhotoResID(user.getPhotoIdx()));
		
		btn_scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoActivity(BluActivity.class);
			}
		});
		
		btn_chart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoActivity(ChartActivity.class);
			}
		});
	}
	
	private void gotoActivity(final Class<?> cls) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("bluetooth.user", user);
		
		Intent intent = new Intent();
		intent.setClass(context, cls);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
