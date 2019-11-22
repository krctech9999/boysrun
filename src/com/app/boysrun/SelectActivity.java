/**
 * list_user_layout.xml 舊版刪除
 * 
 */

package com.app.boysrun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.app.boysrun.dialog.ProDialog;
import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.misc.MiscPage;
import com.app.boysrun.misc.MiscTmp;
import com.app.boysrun.tool.AsyncTaskStarter;
import com.google.common.collect.Lists;

public class SelectActivity extends Activity {
	private final String YY = "YY";
	
	private final String TAG = "SelectActivity";
	private Context context;
	
	private ProDialog proDialog;
	private static HashMap<String, Drawable> drawMap = new HashMap<String, Drawable>() ;
	
	private List<Video> videoList = new ArrayList<Video>();

	/** widget */
	private Button btn_return;
	private TextView txv_map;
	private RelativeLayout RR_ITEM;
	
	private Button btn_pre;
	private Button btn_next;
	
	/** widget */
	
	// view holder
	private final List<ViewHolder> holderList = new ArrayList<ViewHolder>();
	
	//-----------------------------------------------------------
	private int pageIdx = 0;  //目前游標所在頁數index
		
	private final int FIRST_ITEM_IDX = 0; //focus於item 1st
	private final int LAST_ITEM_IDX = 3;  //focus於item 4th
	//-----------------------------------------------------------	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		context = this;
		
		findview();
		initSize();
		initView();
		pageData(initVideoList(), FIRST_ITEM_IDX);
	}
	
	private class ViewHolder {
		public ViewHolder(View v) {
			itemView = v;
		}
		
		public View itemView;
		
		public RelativeLayout RR;
		
		public ImageView img;
		
		public TextView txv_content;
		public TextView txv_border;
	}
	
	private List<Video> initVideoList() {
		for(String url : MiscTmp.getTmpGlobalVideoList()) {
			String[] strAry = url.split("/");
			videoList.add(new Video().setUrl(url).setVideoName(strAry[strAry.length - 1]));
		}
		return videoList;
	}
	
	private void findview() {
		RR_ITEM = (RelativeLayout) findViewById(R.id.RR_ITEM);
		btn_return = (Button) findViewById(R.id.btn_return);
		txv_map = (TextView) findViewById(R.id.txv_map);
		
		btn_pre = (Button) findViewById(R.id.btn_pre);
		btn_next = (Button) findViewById(R.id.btn_next);
	}
	
	private void initSize() {
		GlobalVar.setMargin(btn_return, 40, 40, 0, 0);
		btn_return.setTextSize(23);
		
		GlobalVar.setMargin(txv_map, 45, 15, 0, 0);
		txv_map.setTextSize(28);
		
		GlobalVar.setSize(RR_ITEM, 1180, 410);
		
		GlobalVar.setSize(btn_pre, 80, 80);
		GlobalVar.setSize(btn_next, 80, 80);
		GlobalVar.setMargin(btn_pre, 0, 0, 30, 0);
		GlobalVar.setMargin(btn_next, 30, 0, 0, 0);
	}
	
	private void initView() {
		proDialog = new ProDialog(context);
		
		btn_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btn_pre.setOnClickListener(null);
		btn_next.setOnClickListener(null);
		
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		List<RelativeLayout.LayoutParams> paramsList = 
				Lists.newArrayList(params1, params2, params3, params4);
		
		for (int i = 0; i <= 3; i++) {
			ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_function_layout, null));
			holderList.add(holder);
			
			holder.RR = (RelativeLayout) holder.itemView.findViewById(R.id.RR);
			
			holder.img = (ImageView) holder.itemView.findViewById(R.id.img);
			holder.txv_content = (TextView) holder.itemView.findViewById(R.id.txv_content);
			holder.txv_border = (TextView) holder.itemView.findViewById(R.id.txv_border);
		}
		
		//定位在RelativeLayout
		for(int i = 0; i <= 3; i++) {
			View functionView = holderList.get(i).itemView;
			
			holderList.get(i).itemView.setId(i + 1);
			RelativeLayout.LayoutParams param = paramsList.get(i);
			  
			if (i > 0) {
				param.addRule(RelativeLayout.RIGHT_OF, holderList.get(i - 1).itemView.getId());
			}
			
			RR_ITEM.addView(functionView, param);
		}
		
		for(int i = 0; i <= 3; i++) {
			ViewHolder holder = holderList.get(i);
			GlobalVar.setMargin(holder.itemView, 5, 0, 5, 0);
			GlobalVar.setSize(holder.itemView, 280, 350);
			
			GlobalVar.setSize(holder.txv_content, 280, 60);
		}
		
		for (ViewHolder holder : holderList) {
			holder.itemView.setFocusable(true);
		}
	}
	
	public void waitLoading(ProDialog mDialog, final boolean start, final String message, Integer animationRes) {
		//是否可被cancel
		mDialog.setCancelable(false);
		if (start) {
			mDialog.show();
			mDialog.setDlgMsg(message);
			
			if(animationRes != null) {
				mDialog.setMyIndeterminate(animationRes);
			}
			mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					//取消事件
				}
			});
		} else {
			mDialog.dismiss();
			Log.d(TAG, "waitloading dismiss");
		}
	}
	
	private void goToVideo(String videoName) {
		Bundle inbundle = getIntent().getExtras();
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("Mode", (DataActivity.MODE) inbundle.getSerializable("Mode"));
		
		bundle.putSerializable("bluetooth.user", inbundle.getSerializable("bluetooth.user"));
		bundle.putParcelable("bluetooth.device", (BluetoothDevice) inbundle.getParcelable("bluetooth.device"));
		bundle.putInt("bluetooth.rssi", inbundle.getInt("bluetooth.rssi"));
		bundle.putString("videoName", videoName);
		
		Intent intent = new Intent();
		intent.setClass(context, DataActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		
		finish();
	}
	
	@SuppressWarnings("unchecked")
	private void pageData(final List<Video> dataList, final int focusIdx) {
		Log.i(TAG, "pageIdx = " + pageIdx);
		resetBtnGroup();
		
		renderPageDataItem((List<Video>) MiscPage.getIdxData(dataList, pageIdx, 4), focusIdx);
		MiscPage.checkBtnVisible(btn_pre, pageIdx > 0); //設定有無上一頁
		MiscPage.checkBtnVisible(btn_next, hasNextPageData(dataList)); //有無下一頁
		btnFocusListener(btn_pre, dataList, -1, LAST_ITEM_IDX);
		btnFocusListener(btn_next, dataList, 1, FIRST_ITEM_IDX);
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
	private void renderPageDataItem(final List<Video> pageDataList, final int focusIdx) {
		if(pageDataList == null) {
			Log.e(YY, "pageDataList = null");
			return;
		}
		
		for (int position = 0; position < pageDataList.size(); position++) {
			final Video video = pageDataList.get(position);
			
			if (position <= 3) {
				final ViewHolder holder = holderList.get(position);
				settingHolder(holder, video);
				
				//(demo)假圖-----
				switch(video.getVideoName()) {
				case "papgo.mp4":
					holder.itemView.setBackgroundResource(R.drawable.c07_01);
					break;
				case "wb0124_10km.mp4":
					holder.itemView.setBackgroundResource(R.drawable.c07_02);
					break;
				case "mov_20150320_15_a.mp4":
					holder.itemView.setBackgroundResource(R.drawable.c07_03);
					break;
				}
				//(demo)-----

				holder.itemView.setVisibility(View.VISIBLE);

				if (position == focusIdx) {
					holder.itemView.requestFocus();
				}
			}
		}
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
	
	private void settingHolder(final ViewHolder holder, final Video video) {		
//		GlobalVar.setSize(holder.ULAYOUT, 200, 520);  
//		GlobalVar.setSize(holder.uimg, 200, 520);
//		
//		GlobalVar.setSize(holder.L_LEVEL, 135, 135);
//		
//		GlobalVar.setSize(holder.txv_name, 95, 60);
//		holder.txv_level.setVisibility(View.GONE);
		
		holder.txv_content.setTextSize(20);
		
//		holder.txv_name.setTextColor(getResources().getColor(R.color.color_click_button_selector));
//		holder.txv_level.setTextColor(getResources().getColor(R.color.color_click_button_selector));

		String videoName = video.getVideoName();
		holder.txv_content.setText(videoName + needDownloadNote(videoName));
		
		if(drawMap.containsKey(videoName)) {
			holder.itemView.setBackground(drawMap.get(videoName));
		} else {
			holder.itemView.setBackgroundResource(R.color.text_gradient);
			AsyncTaskStarter.startAsyncTask(new SnapAsyncTask(holder.itemView, video));
		}
		
		holder.itemView.setFocusable(true);
		holder.RR.setOnFocusChangeListener(functionFocusChangeListener());
		
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String videoName = video.getVideoName();
				
				//(demo)---
				if(!videoName.contains("boysrun_cycle_video")) {
					//demo不開放
					return;
				}
				//(demo)---
				
				if(Misc.isVideoExist(context, videoName)) {
					goToVideo(videoName);
				} else if(MiscTmp.checkFileExistRaw(context, videoName)) {
					goToVideo(videoName);
				} else {
					//下載影片
					AsyncTaskStarter.startAsyncTask(new DownloadTask(video));
				}
			}
		});
	}
	
	/**
	 * @param dataList
	 * @return 有無下一頁資料
	 */
	private boolean hasNextPageData(final List<Video> dataList) {
		int tmpIdx = pageIdx + 1;
		@SuppressWarnings("unchecked")
		final List<Video> tmpArticleList = (List<Video>) MiscPage.getIdxData(dataList, tmpIdx, 4);
		if (tmpArticleList != null) {
			if(tmpArticleList.size() > 0) { 
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param btn
	 * @param dataList
	 * @param pageChange
	 * @param foucusIdx
	 */
	private void btnFocusListener(final Button btn, final List<Video> dataList, final int pageChange, final int foucusIdx) {
		btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					pageIdx = pageIdx + pageChange;
					
					Log.e(YY, "pageIdx = " + pageIdx);
					pageData(dataList, foucusIdx);
				}
			}
		});
	}
	
	private String needDownloadNote(String videoName) {
		if(MiscTmp.checkFileExistRaw(context, videoName)) {
			return " (內建)";
		}
		
		if(Misc.isVideoExist(context, videoName)) {
			return "";
		} else {
			return " (需要下载)";
		}
	}
	
	private class Video {
		private String videoName = null;
		private String url = null;
		
		public String getVideoName() {
			return videoName;
		}
		
		public String getUrl() {
			return url;
		}
		
		public Video setVideoName(String videoName) {
			this.videoName = videoName;
			return this;
		}
		
		public Video setUrl(String url) {
			this.url = url;
			return this;
		}
	}
	
	private class SnapAsyncTask extends AsyncTask<Void, Void, Void> {
		View view; 
		String videoName;
		Drawable draw = null;
		
		public SnapAsyncTask(View view, Video video) {
			this.view = view;
			videoName = video.getVideoName();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			if(Misc.isVideoExist(context, videoName)) {
				draw = Misc.getFileDrawable(context,
						new File(Misc.getBoysRunMoviePath(context) + videoName));
			} else if (MiscTmp.checkFileExistRaw(context, videoName)) {
				draw = getResources().getDrawable(MiscTmp.tmpVidonameToRes(videoName));
			} else {	
				draw = null;
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(draw != null) {
				drawMap.put(videoName, draw);
				view.setBackground(drawMap.get(videoName));
			}
		}
	}

	private class DownloadTask extends AsyncTask<Void, Integer, Void> {
	    private String webUrl;
	    private String videoName;
	    private String filePath;
	    
	    private File tmpFile = null;
	    
	    public DownloadTask(Video video) {
	        this.webUrl = video.getUrl();
	        this.videoName = video.getVideoName();
	        this.filePath = Misc.getBoysRunMoviePath(context) + videoName;
	        
	        tmpFile = new File(filePath + ".tmp"); 
	    }
	    
	    @Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitLoading(proDialog, true, "", null);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int percent = values[0];
			Log.d(TAG, "onPrsUpdate download = " + percent + "%");
			waitLoading(proDialog, true, "下载影片 " + videoName + " : " + percent + "%", null);
		}

		@Override
	    protected Void doInBackground(Void... arg0) {
	        InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        try {
	            URL url = new URL(webUrl);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	            	return null;
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = connection.getInputStream();
	            
	            tmpFile.delete(); // delete tmp file if exists
	            output = new FileOutputStream(tmpFile);

	            byte data[] = new byte[1024 * 4];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                }
	                total += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                	
	                output.write(data, 0, count);
	            }
	        } catch (IOException e) {
	        	e.printStackTrace();
	        	
	        	File file = new File(filePath);
	        	if(file.exists()) {
	        		file.delete();
	        	}
	        } catch (Exception e) { 
			} finally {
	            try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {
	            }
	            if (connection != null) {
	                connection.disconnect();
	            }
	        }
	        return null;
	    }

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			waitLoading(proDialog, false, "", null);

			// rename file
			tmpFile.renameTo(new File(filePath));
			
			pageData(videoList, FIRST_ITEM_IDX);
		}
	}
	
	private void myToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

}
