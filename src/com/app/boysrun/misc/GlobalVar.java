package com.app.boysrun.misc;

import java.sql.SQLException;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.app.boysrun.ormdb.BoysRunOrmHelper;
import com.app.boysrun.ormdb.data.BTRecord;
import com.app.boysrun.ormdb.data.User;
import com.j256.ormlite.dao.Dao;

public class GlobalVar extends Application {
	
	public static Context context = null;
	private static String productNumber = "1234";
	
	//screen scale data
	public static int width;
	public static int height;
	public static float scale_width;
	public static float scale_height;
	
	// ormlite -------------------------------------
	static BoysRunOrmHelper ormhelper;
	public static Dao<BTRecord, Integer> recordDao = null;
	public static Dao<User, String> userDao = null;
	
	// user
	public static final int userSize = 12; //使用者圖像數量
		
	@Override
	public void onCreate() {
		super.onCreate();
		context = this.getApplicationContext();
		initDao();
	}
	
	public static void initDao() {
		if (recordDao == null || userDao == null) {
			ormhelper = new BoysRunOrmHelper(context);
			try {
				recordDao = ormhelper.getRecordDao();
				userDao = ormhelper.getUserDao();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	// ormlite -------------------------------------

	public static void setConfig(String key, String value) {
		SharedPreferences settings = context.getSharedPreferences(productNumber, 0);
		settings.edit().putString(key, value).commit();
	}

	public static String getConfig(String key, String def) {
		SharedPreferences settings = context.getSharedPreferences(productNumber, 0);
		return settings.getString(key, def);
	}
	
	public static void screenScale(WindowManager winMgr) {
		Point point = new Point();
		(winMgr.getDefaultDisplay()).getSize(point);
		width = point.x;
		height = point.y;
		GlobalVar.setScale(((float) width) / 1280, ((float) height) / 720);
	}
	
	private static void setScale(float w, float h) {
		scale_width = w;
		scale_height = h;
	}
	
	public static void setSize(View v, int w , int h) {
		if(w > 0){
			v.getLayoutParams().width = (int) (w * scale_width);
		}
		if(h > 0){
			v.getLayoutParams().height = (int) (h * scale_height);
		}
	}
	public static void setHeight(View v, int h) {
		if (h > 0) {
			v.getLayoutParams().height = (int) (h * scale_height);
		}
	}
	
	public static void setWidth(View v, int w) {
		if (w > 0) {
			v.getLayoutParams().width = (int) (w * scale_width);
		}
	}
	
	/**
	 * 設定 margin
	 * @param v
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public static void setMargin(View v, int left, int top, int right, int bottom) {
		if (v != null) {
			if (v.getLayoutParams() != null && v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
				lp.leftMargin = (int) (left * scale_width);
				lp.topMargin = (int) (top * scale_height);
				lp.rightMargin = (int) (right * scale_width);
				lp.bottomMargin = (int) (bottom * scale_height);
				v.setLayoutParams(lp);
			}
		}
	}

	/**
	 * 設定 padding
	 * @param v
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public static void setPadding(View v, int left, int top, int right, int bottom) {
		if (v != null) {
			v.setPadding((int) (left * scale_width), (int) (top * scale_height), (int) (right * scale_width),
					(int) (bottom * scale_height));
		}
	}

}
