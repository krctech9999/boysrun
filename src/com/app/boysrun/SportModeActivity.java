package com.app.boysrun;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.boysrun.misc.GlobalVar;
import com.google.common.collect.Lists;


/**
 * 主功能選單
 *
 */
public class SportModeActivity extends Activity {
	
	private Context context;
	
	//view holder 
	private final List<ViewHolder> holderList = new ArrayList<ViewHolder>();
	
	//UI
	private RelativeLayout RR_FUNCTION;
	private Button btn_return;
	
	private TextView txv_user;
	private TextView txv_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_mode);
		context = this;
		
		findView();
		initSize();
		init();
	}
	
	private void findView() {
		RR_FUNCTION = (RelativeLayout) findViewById(R.id.RR_FUNCTION);
		btn_return = (Button) findViewById(R.id.btn_return);
		
		txv_user = (TextView) findViewById(R.id.txv_user);
		txv_msg = (TextView) findViewById(R.id.txv_msg);
	}
	
	private void initSize() {
		GlobalVar.setSize(RR_FUNCTION, 1200, 410);
		GlobalVar.setMargin(RR_FUNCTION, 50, 50, 0, 0);
		
		GlobalVar.setMargin(btn_return, 40, 40, 0, 0);
		btn_return.setTextSize(23);
		
		txv_user.setTextSize(28);
		GlobalVar.setMargin(txv_user, 45, 15, 0, 0);
		txv_msg.setTextSize(28);
		GlobalVar.setMargin(txv_msg, 45, 15, 0, 0);
	}
	
	private class ViewHolder {
		public ViewHolder(View v) {
			functionView = v;
		}
		
		public View functionView;
		
		public RelativeLayout RR;
		
		public ImageView img;
		
		public TextView txv_content;
		public TextView txv_border;
	}
	
	private OnFocusChangeListener functionFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					v.bringToFront();
					v.requestLayout();
					v.animate().scaleX((float) 1.1).scaleY((float) 1.1).setDuration(80);
				} else {
					v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80);
				}
			}
		};
	}
	
	private void init() {
		btn_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		List<RelativeLayout.LayoutParams> paramsList = 
				Lists.newArrayList(params1, params2, params3, params4);
		
		for (int i = 0; i <= 3; i++) {
			ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_function_layout, null));
			holderList.add(holder);
			
			holder.RR = (RelativeLayout) holder.functionView.findViewById(R.id.RR);
			
			holder.img = (ImageView) holder.functionView.findViewById(R.id.img);
			holder.txv_content = (TextView) holder.functionView.findViewById(R.id.txv_content);
			holder.txv_border = (TextView) holder.functionView.findViewById(R.id.txv_border);
		}
		
		//定位在RelativeLayout
		for(int i = 0; i <= 3; i++) {
			View functionView = holderList.get(i).functionView;
			
			holderList.get(i).functionView.setId(i + 1);
			RelativeLayout.LayoutParams param = paramsList.get(i);
			  
			if (i > 0) {
				param.addRule(RelativeLayout.RIGHT_OF, holderList.get(i - 1).functionView.getId());
			}
			
			RR_FUNCTION.addView(functionView, param);
		}
		
		for(int i = 0; i <= 3; i++) {
			ViewHolder holder = holderList.get(i);
			
			int marginLeft = 0;
			if(i == 0) {
				marginLeft = 30;
			} else {
				marginLeft = 10;
			}
			 
			GlobalVar.setMargin(holder.functionView, marginLeft, 25, 0, 0);

			GlobalVar.setSize(holder.functionView, 280, 350);
			GlobalVar.setSize(holder.txv_content, 280, 60);
			
			holder.txv_content.setTextSize(28);
		}
		
		List<String> functionList = Lists.newArrayList("自由模式", "Live陪跑", "Live活动", "竞赛模式");
		List<Integer> functionImgList = Lists.newArrayList(
				R.drawable.c04_01, R.drawable.c04_02, R.drawable.c04_03, R.drawable.c04_04);
		
		for (int i = 0; i <= 3; i++) {
			holderList.get(i).txv_content.setText(functionList.get(i));
			holderList.get(i).img.setBackgroundResource(functionImgList.get(i));
		}
		
		for (ViewHolder holder : holderList) {
			holder.functionView.setFocusable(true);
			holder.RR.setOnFocusChangeListener(functionFocusChangeListener());
		}
		
		for (int i = 0; i <= 3; i++) {  //demo: 只有自由模式(0)   競賽模式(3)
			Log.d("TEST", "i = " + i);
			
			if(i > 0 && i < 3) {
				continue;
			}
			
			Bundle inbundle = getIntent().getExtras();
			
			final Bundle bundle = new Bundle();
			bundle.putSerializable("bluetooth.user", inbundle.getSerializable("bluetooth.user"));
			bundle.putParcelable("bluetooth.device", (BluetoothDevice) inbundle.getParcelable("bluetooth.device"));
			bundle.putInt("bluetooth.rssi", inbundle.getInt("bluetooth.rssi"));
			switch(i) {
				case 0:
					bundle.putSerializable("Mode", DataActivity.MODE.FREE);
					break;
				case 3:
					bundle.putSerializable("Mode", DataActivity.MODE.GAME);
					break;
			}
			
			ViewHolder holder = holderList.get(i);
			holder.functionView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//開始跑步
					
					Intent intent = new Intent();
					intent.setClass(context, SelectActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
		}
	}
}

