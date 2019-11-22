package com.app.boysrun.misc;

import java.util.Arrays;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MiscPage {
	private final static String TAG = "PageMisc";
	
	/**
	 * @param dataList
	 * @param idx
	 * @return
	 */
	public static List<?> getIdxData(final List<?> dataList, int idx, int sizePerPage) {
		Log.i(TAG, "============================================");
		Log.i(TAG, "total data list size = " + dataList.size());
		Log.i(TAG, "============================================");
		
		int page = idx + 1;
		try {
			List<?> pageData;
			Log.i(TAG, "**********************");
			Log.i(TAG, "idx = " + idx);
			Log.i(TAG, "page = " + page);
			Log.i(TAG, "dataList = " + dataList.size());
			Log.i(TAG, "idx * " + sizePerPage + " = " + (idx * sizePerPage));
			Log.i(TAG, "page * " + sizePerPage + " = " + (page * sizePerPage));
			Log.i(TAG, "**********************");
			if (dataList.size() >= page * sizePerPage) {
				Log.i(TAG, ">=");
				pageData = dataList.subList(idx * sizePerPage, (idx * sizePerPage) + sizePerPage);
			} else {
				//取餘數 
				Log.i(TAG, "<");
				pageData = dataList.subList(idx * sizePerPage, dataList.size());
			}
			
			Log.i(TAG, "getIdxData size = " + pageData.size());
			return pageData;
		} catch (Exception e) {
			//最後一頁exception
			e.printStackTrace();
			return null;
		}
	}
	
	public static void checkBtnVisible(Button btn, boolean visible) {
		if(visible) {
			btn.setVisibility(View.VISIBLE);
			btn.setFocusable(true);
		} else {
			btn.setVisibility(View.INVISIBLE);
			btn.setFocusable(false);
		}
	}
	
}
