package com.app.boysrun.misc;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.app.boysrun.R;

public class Misc {
	
	private final static String TAG = "Misc";
	
	/*轉換日期格式*/
	public final static String format_yymmdd = "yyyy/MM/dd";
	public final static String format_year = "yyyy";
	public final static String format_MM = "MM";
	public final static String format_dd = "dd";
	
	/**
	 * 產生日期字串
	 * @param date
	 * @param format 格式  "yyyy/MM/dd"  西元年/月/日
	 *                   "yyyy"       西元年
	 *                   "MM"         月 
	 *                   "dd"         日     
	 * @return
	 */
	public static String convertSimpleDateStr(Date date, String format) {
		SimpleDateFormat dformat = new SimpleDateFormat(format, java.util.Locale.getDefault());
		return dformat.format(date);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isSameDate(Date d1, Date d2) {
		String day1 = d1.getYear() + "/" + d1.getMonth() + "/" + d1.getDate();
		String day2 = d2.getYear() + "/" + d2.getMonth() + "/" + d2.getDate();
		if (day1.equals(day2)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @param d
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date resetDate(Date d) {
		Date date = new Date();
		date.setTime(0);
		date.setYear(d.getYear());
		date.setMonth(d.getMonth());
		date.setDate(d.getDate());
		return date;
	}

	/**
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}
	
	/**
	 * 格式化小數至指定位數的字串
	 * @param digit 位數
	 * @return
	 */
	public static String genStrFloatByDigit(float value, int digit) {
		final NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setMaximumFractionDigits(digit);
        return numFormat.format(value);
	}
	
	/**
	 * @param url
	 * @return Testing to see if URL is reachable
	 */
	public static boolean isReachableURL(String urlStr) {
		URL url;
		int code = -1;
		boolean isReachable = false;
		try {
			url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			code = connection.getResponseCode();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
		}
		
		if(code == 200) {
			isReachable = true;
			Log.d(TAG, "URL可取得");
		} else {
			Log.e(TAG, "URL無法取得");
		}
		
		return isReachable;
	}
	
	/**
	 * 取得影片縮圖
	 * @param context
	 * @param file
	 * @return
	 */
	public static Drawable getFileDrawable(Context context, File file) {
		BitmapDrawable drawable = null;

		try {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(file.getAbsolutePath());
			drawable = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(
					retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC), 200, 520, false));
		} catch (Exception e) {
			drawable = null;
		}
		
		return drawable;
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setViewDraw(View view, Drawable draw) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(draw);
		} else {
			view.setBackgroundDrawable(draw);
		}
	}
	
	public static Drawable getFocusBorderShape() {
		GradientDrawable gd = null;
		Resources res = GlobalVar.context.getResources();
		
		try {
			gd = (GradientDrawable) Drawable.createFromXml(res, res.getXml(R.drawable.focusborder));
			gd.setStroke(5, Color.WHITE);
		} catch (XmlPullParserException | IOException e) {
			e.printStackTrace();
		}
		
		return gd;
	}
	
	/**
	 * 影片是否存在
	 * @param videoName
	 * @return
	 */
	public static boolean isVideoExist(Context context, String videoName) {
		File file = new File(getBoysRunMoviePath(context) + videoName);
		if(file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 計算隨機數字
	 * @param min 最小值
	 * @param max 最大值s
	 * @return
	 */
	public static int randInt(int min, int max) {
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	/**
	 * 取得影片目錄
	 * @param context
	 * @return
	 */
	public static String getBoysRunMoviePath(Context context) {
		String cachePath = "";
		try {
			cachePath = context.getCacheDir().getCanonicalPath() + "/BoysRunMovies";
			if (!new File(cachePath).exists()) {
				new File(cachePath).mkdir();
			}
			cachePath = cachePath + File.separator;
		} catch (IOException e) {
			cachePath = "";
			e.printStackTrace();
		}
		
		Log.i(TAG, "cachePath = " + cachePath);
		return cachePath;
	}
	
	/**
	 * 取得使用者照片 res ID
	 * @param photoIdx
	 * @return
	 */
	public static int getUserPhotoResID(int photoIdx) {
		switch (photoIdx) {
		case 0:
			return R.drawable.user1;
		case 1:
			return R.drawable.user2;
		case 2:
			return R.drawable.user3;
		case 3:
			return R.drawable.user4;
		case 4:
			return R.drawable.user5;
		case 5:
			return R.drawable.user6;
		case 6:
			return R.drawable.user7;
		case 7:
			return R.drawable.user8;
		case 8:
			return R.drawable.user9;
		case 9:
			return R.drawable.user10;
		case 10:
			return R.drawable.user11;
		case 11:
			return R.drawable.user12;
		default: 
			return R.drawable.user1;
		}
	}
	
	/**
	 * 取得藍芽訊號強度圖
	 * @param strength
	 * @return
	 */
	public static int getStrengthImg(int strength) {
		if(strength >= -40) {
			return R.drawable.a13_06;
		} else if(strength >= -50) {
			return R.drawable.a13_05;
		} else if(strength >= -60) {
			return R.drawable.a13_04;
		} else if(strength >= -70) {
			return R.drawable.a13_03;
		} else if(strength >= -80) {
			return R.drawable.a13_02;
		} else {
			return R.drawable.a13_01;
		}
	}
}
