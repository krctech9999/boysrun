package com.app.boysrun.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.app.boysrun.R;
import com.google.common.collect.Lists;


public class MiscTmp {
	static boolean rawmode = true;

	/** 影片變數 */
	private static List<String> globalVideoList = Lists.newArrayList(
		"http://209.95.35.8/vod/Test/Other/20150424/boysrun_cycle_video.mp4",
				
		"http://209.95.35.8/vod/Test/Other/20150424/papgo.mp4",
		"http://209.95.35.8/vod/Test/Other/20150424/wb0124_10km.mp4",
		"http://209.95.35.8/vod/Test/Other/20150427test/mov_20150320_15_a.mp4");	
	
	public static List<String> getTmpGlobalVideoList() {
		if (!rawmode) {
			globalVideoList.set(0, "http://209.95.35.8/vod/Test/Other/20150424/GoPro_Bicycle.mp4");
			globalVideoList.set(3, "http://209.95.35.8/vod/Test/Other/20150427test/20150320-15(A).mp4");
		}

		return globalVideoList;
	}

	/**
	 * @param videoName
	 * @return assets是否有這檔案
	 */
	public static boolean checkFileExistAsset(Context context, String videoName) {
		boolean exist = false;
		try {
			if (Arrays.asList(context.getResources().getAssets().list("")).contains(videoName)) {
				exist = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exist;
	}

	/**
	 * @param context
	 * @param videoName
	 * @return RAW是否有這檔案
	 */
	public static boolean checkFileExistRaw(Context context, String videoName) {
		if (!rawmode) {
			return false;
		}

		boolean exist = false;
		
		List<String> inbuiltVideoList = new ArrayList<String>();
		
		inbuiltVideoList.add("boysrun_cycle_video.mp4");
		
		//檢查內建影片
		for(String vn : inbuiltVideoList) {
			if(videoName.contains(vn)) {
				exist = true;
				break;
			}
		}
		
		return exist;
	}

	public static int tmpVidonameToRes(String videoName) {
		int id = 0;
		switch (videoName) {
		case "boysrun_cycle_video.mp4":
			id = R.drawable.boysrun_cycle_video;
			break;
		
		default:
			id = R.drawable.transparent;
			break;
		}
		return id;
	}

	/**
	 *  rawmode = false時    RAW資料夾無影片,mark R.raw.id
	 * 
	 * @param videoName
	 * @return
	 */
	public static int tmpVidonameToVideo(String videoName) {
		int id = 0;

		//*raw開關  mark* /
		if (rawmode) {
			switch (videoName) {
			case "gopro_bicycle.mp4":
				 id = R.raw.boysrun_cycle_video;
				break;
			default:
				 id = R.raw.boysrun_cycle_video;
				break;
			}
		}

		//*/
		
		return id;
	}
}
