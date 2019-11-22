package com.app.boysrun.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MiscNet {
	
	/**
	 * 判斷是否連接上網路
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()){
			return true;
		}
		return false;
	}
	
}
