package com.app.boysrun;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.boysrun.callback.EntryCallBack;
import com.app.boysrun.misc.DevTest;
import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.misc.MiscPage;
import com.app.boysrun.ormdb.data.User;

/**
 * 帳號管理
 */
public class EntryActivity extends Activity implements EntryCallBack {
	private final String TAG = "EntryActivity"; 
	private final String YY = "YY";
	
	private Context context;
	public static EntryCallBack entryCallBack;
	
	//widget--------------
	private Button btn_pre;
	private Button btn_next;
	
	private Button btn_return;
	private Button btn_add;
	
	private LinearLayout LL_ITEM_UP;
	private LinearLayout LL_ITEM_DOWN;
	
	//widget--------------	
	
	private final List<ViewHolder> holderList = new ArrayList<ViewHolder>();
	
	//-----------------------------------------------------------
	private final int sizePerPage = 6; //
	
	private int pageIdx = 0;  //目前游標所在頁數index
	private final int FIRST_ITEM_IDX = 0; //focus於item 1st
	private final int LAST_ITEM_IDX = 5;  //focus於item 5th
	//-----------------------------------------------------------
	private List<User> userList = new ArrayList<User>();
	
	private class ViewHolder {
		public ViewHolder(View v) {
			itemView = v;
		}
		
		public View itemView;
		
		public LinearLayout LL_IMG;
		public LinearLayout LL_DATA;
		
		public ImageView uimg;
		
		public TextView txv_name;
		public TextView txv_time;
		public TextView txv_distance;
		public TextView txv_calorie;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		context = this;
		entryCallBack = (EntryCallBack) this;
		
		findview();
		initView();
		initPage(true);
		
		/*
		addUser("王小明", 1);
		addUser("張三", 2);
		addUser("李四", 3);
		*/
	}
	
	private void findview() {
		btn_return = (Button) findViewById(R.id.btn_return);
		btn_add = (Button) findViewById(R.id.btn_add);
		
		LL_ITEM_UP = (LinearLayout) findViewById(R.id.LL_ITEM_UP);
		LL_ITEM_DOWN = (LinearLayout) findViewById(R.id.LL_ITEM_DOWN);
		
		btn_pre = (Button) findViewById(R.id.btn_pre);
		btn_next = (Button) findViewById(R.id.btn_next);
	}
	
	private void initView() {
		GlobalVar.setSize(btn_pre, 65, 65);
		GlobalVar.setSize(btn_next, 65, 65);
		
		GlobalVar.setHeight(LL_ITEM_UP, 260);
		GlobalVar.setHeight(LL_ITEM_DOWN, 260);
		
		GlobalVar.setSize(btn_return, -1, -1);
		GlobalVar.setSize(btn_add, -1, -1);
		
		GlobalVar.setMargin(btn_return, 40, 40, 0, 0);
		GlobalVar.setMargin(btn_add, 40, 40, 0, 0);
		
		GlobalVar.setMargin(btn_pre, 0, 0, 10, 0);
		GlobalVar.setMargin(btn_next, 10, 0, 0, 0);
		
		btn_return.setTextSize(23);
		btn_add.setTextSize(23);
		
		btn_return.setFocusable(true);
		btn_add.setFocusable(true);
		
		btn_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goAddUser();
			}
		});
		
		for (int i = 0; i <= 5; i++) {
			ViewHolder holder = 
					new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_user_layout2, null));
			
			holder.LL_IMG = (LinearLayout) holder.itemView.findViewById(R.id.LL_IMG);
			holder.LL_DATA = (LinearLayout) holder.itemView.findViewById(R.id.LL_DATA);
			
			holder.uimg = (ImageView) holder.itemView.findViewById(R.id.uimg);
			
			holder.txv_name = (TextView) holder.itemView.findViewById(R.id.txv_name);
			holder.txv_time = (TextView) holder.itemView.findViewById(R.id.txv_time);
			holder.txv_distance = (TextView) holder.itemView.findViewById(R.id.txv_distance);
			holder.txv_calorie = (TextView) holder.itemView.findViewById(R.id.txv_calorie);
			
			holderList.add(holder);
			
			if (i <= 2) {
				LL_ITEM_UP.addView(holder.itemView);
			} else if (i >= 3 && i <= 5) {
				LL_ITEM_DOWN.addView(holder.itemView);
			}
		}
		
		GlobalVar.setMargin(LL_ITEM_UP, 0, 0, 0, 10);
		
		for (int i = 0; i <= 5 ; i++) {
			GlobalVar.setSize(holderList.get(i).itemView, 380, 172);
			GlobalVar.setSize(holderList.get(i).LL_IMG, 190, 172);
			GlobalVar.setSize(holderList.get(i).LL_DATA, 190, 172);
			
			GlobalVar.setMargin(holderList.get(i).itemView, 5, 0, 5, 0);
		}
		
		for (ViewHolder holder : holderList) {
			holder.itemView.setFocusable(true);
			holder.itemView.setOnFocusChangeListener(itembtnFocusChangeListener());
		}
	}
	
	private OnFocusChangeListener itembtnFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					//v.bringToFront();
					//v.requestLayout();
					//v.animate().scaleX((float) 1.1).scaleY((float) 1.1).setDuration(80);
					
					for (int i = 0; i < holderList.size(); i++) {
						if(v.equals(holderList.get(i).itemView)) {
							Misc.setViewDraw(holderList.get(i).itemView, Misc.getFocusBorderShape());
						}
					}
				} else {
					//v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80);
					for (int i = 0; i < holderList.size(); i++) {
						if(v.equals(holderList.get(i).itemView)) {
							Misc.setViewDraw(holderList.get(i).itemView, null);
						}
					}
				}
			}
		};
	}
	
	public void getUserList() {
		try {
			userList = GlobalVar.userDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			userList = new ArrayList<User>();
		}
	}

	/**
	 * @param reset
	 */
	private void initPage(boolean reset) {
		if(reset) {
			pageIdx = 0;
		}
		getUserList();
		pageData(userList, FIRST_ITEM_IDX);
	}
	
	@SuppressWarnings("unchecked")
	private void pageData(final List<User> dataList, final int focusIdx) {
		Log.i(TAG, "pageIdx = " + pageIdx);
		resetBtnGroup();
		
		renderPageDataItem((List<User>) MiscPage.getIdxData(dataList, pageIdx, sizePerPage), focusIdx);
		MiscPage.checkBtnVisible(btn_pre, pageIdx > 0); //設定有無上一頁
		MiscPage.checkBtnVisible(btn_next, hasNextPageData(dataList)); //有無下一頁
		btnFocusListener(btn_pre, dataList, -1, LAST_ITEM_IDX);
		btnFocusListener(btn_next, dataList, 1, FIRST_ITEM_IDX);
	}
	
	/**
	 * @param btn
	 * @param dataList
	 * @param pageChange
	 * @param foucusIdx
	 */
	private void btnFocusListener(final Button btn, final List<User> dataList, final int pageChange, final int foucusIdx) {
		
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pageIdx = pageIdx + pageChange;
				
				Log.e(YY, "pageIdx = " + pageIdx);
				pageData(dataList, foucusIdx);
			}
		});
		
//		btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if (hasFocus) {
//					pageIdx = pageIdx + pageChange;
//					
//					Log.e(YY, "pageIdx = " + pageIdx);
//					pageData(dataList, foucusIdx);
//				}
//			}
//		});
	}
	
	private void resetBtnGroup() {
		for (ViewHolder holder : holderList) {
			holder.itemView.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * show current page data(category or article)
	 * @param pageDataList
	 * @param focusIdx
	 */
	private void renderPageDataItem(final List<User> pageDataList, final int focusIdx) {
		if(pageDataList == null) {
			return;
		}
		
		for (int position = 0; position < pageDataList.size(); position++) {
			final User user = pageDataList.get(position);
			
			if (position <= LAST_ITEM_IDX) {
				final ViewHolder holder = holderList.get(position);
				
				settingHolder(holder, "LV" + user.getLevel(), user.getName(), Misc.getUserPhotoResID(user.getPhotoIdx()));
				
				holder.itemView.setVisibility(View.VISIBLE);
				if (position == focusIdx) {
					holder.itemView.requestFocus();
				} 

				holder.itemView.setFocusable(true);
				holder.txv_name.setFocusable(false);
				
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						goUser(user);
					}
				});
			}
		}
	}

	private void settingHolder(ViewHolder holder, String level, String name, int resId) {		
		GlobalVar.setSize(holder.LL_IMG, 190, 172);
		GlobalVar.setSize(holder.LL_DATA, 190, 172);
		
		holder.txv_name.setTextSize(19);
		holder.txv_time.setTextSize(19);
		holder.txv_distance.setTextSize(19);
		holder.txv_calorie.setTextSize(19);
		
		holder.txv_name.setTextColor(Color.WHITE);
		holder.txv_time.setTextColor(Color.WHITE);
		holder.txv_distance.setTextColor(Color.WHITE);
		holder.txv_calorie.setTextColor(Color.WHITE);
		
		GlobalVar.setMargin(holder.txv_name, 0, 10, 10, 10);
		GlobalVar.setMargin(holder.txv_time, 0, 0, 10, 10);
		GlobalVar.setMargin(holder.txv_distance, 0, 0, 10, 10);
				
		holder.txv_name.setText("帐号名称" + " " + name);
		holder.txv_time.setText("运动时数" + " ");
		holder.txv_distance.setText("运动里程" + " ");
		holder.txv_calorie.setText("卡路里" + " ");
		
		holder.uimg.setBackgroundResource(resId);
	}
	
	/**
	 * @param dataList
	 * @return 有無下一頁資料
	 */
	private boolean hasNextPageData(final List<User> dataList) {
		int tmpIdx = pageIdx + 1;
		@SuppressWarnings("unchecked")
		final List<User> tmpArticleList = (List<User>) MiscPage.getIdxData(dataList, tmpIdx, sizePerPage);
		if (tmpArticleList != null) {
			if(tmpArticleList.size() > 0) { 
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 增加使用者
	 */
	@Override
	public void addUser(final String userName, final int photoIdx) {
		User user = new User(userName, photoIdx);
		
		try {
			GlobalVar.userDao.create(user);
			myToast("新增使用者成功");
			
			DevTest.forDebug(user, "box-1"); //測試資料
			
			initPage(false);
		} catch (SQLException e) {
			e.printStackTrace();
			myToast("新增使用者失败");
		}
	}
	
	private void goAddUser() {
		Intent intent = new Intent();
		intent.setClass(context, AddUserActivity.class);
		startActivity(intent);
	}
	
	private void goUser(User user) {
		Bundle inbundle = getIntent().getExtras();
		
		Bundle bundle = new Bundle();
		bundle.putParcelable("bluetooth.device", (BluetoothDevice) inbundle.getParcelable("bluetooth.device"));
		bundle.putInt("bluetooth.rssi", inbundle.getInt("bluetooth.rssi"));
		bundle.putSerializable("bluetooth.user", user);
		
		Intent intent = new Intent();
		intent.setClass(context, FunctionMenuActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	private void myToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
}
