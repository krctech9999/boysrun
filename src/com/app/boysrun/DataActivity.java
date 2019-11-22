/**
 *  不需要的code //(mark)
 *  
 *  demo cide //(demo)
 */

package com.app.boysrun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import krc.app.media.vlc.VLCMediaPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaExtractor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.boysrun.callback.DataCallBack;
import com.app.boysrun.dialog.ExitDialog;
import com.app.boysrun.dialog.IpcamSetupDialog;
import com.app.boysrun.dialog.ResultDialog;
import com.app.boysrun.handle.PlayerThread;
import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.misc.MiscMusic;
import com.app.boysrun.misc.asynctask.MusicTask;
import com.app.boysrun.misc.asynctask.StopMusicTask;
import com.app.boysrun.ormdb.data.User;
import com.app.boysrun.tool.AsyncTaskStarter;

public class DataActivity extends Activity implements DataCallBack, SurfaceHolder.Callback {
	private static final String TAG = "DataActivity"; 
	private static final String TAG_DATA_DEBUG = "DATA_DEBUG";
	
	private Context context;
	public static DataCallBack dataCallBack;
	
	/**
	 * DataActivity是否在前景運作
	 */
	private boolean isDataActivityForeground = false;
	
	//-------------------------------------------------------
    
    //widget-------------------------------------------------
    private AnimationDrawable raceStartAnimation;
    private ImageView raceStartGantryImg;
    
    private Button btn_ipcam;
    private SurfaceView surfaceView;
    
    private LinearLayout DATA_LL;
    private TextView txv_cal;
    private TextView txv_step;
    private TextView txv_dis;
    
    
    //-------------------------------------------------------
    
    private User user = null;
    private BluetoothDevice device = null;
    private String videoName;
    
    private boolean threadFlag = false;
    
    private final String str_null = "0";
    /**
     * 公里
     */
    private final String str_dis = "公里";
    /**
     * 卡路里
     */
    private final String str_cal = "卡路里";
	
    /**
	 * @return 產生步數圈數單位 
	 */
	private String genStrStepUnit() {
		if(device.getName().contains("UMHD")) {
			return "圈数";
		} else {
			return "步数";
		}
	}
    
    //-------------------------------------------------------
    //數值
    
    /**
     *  起跑步數比較值(實際步數相減)
     */
    private int initStep = -1;
    
	public static int step_val = 0;
	public static float distance_val = 0;
	public static float calorie_val = 0;
	
	/**
	 * 跑步資料初始化
	 */
	private void resetInitDataStatus() {
		initStep = -1;
		clearDisplayValues();
	}

	@Override
	public void resetGame() {
		finishDataActivity();
		gotoActivity(DataActivity.class);
	}
    
    //-------------------------------------------------------
    private PlayerThread mPlayerThread = null;

	//-------------------------------------------------------
	
	private Handler dialogHandler;
	private Runnable msgRun = null;
	private Runnable musicRun = null;
	
	//-------------------------------------------------------
	// lib VLC
	private SurfaceView vlcSurface;
	private VLCMediaPlayer vlcPlayer = null;
	private IpcamSetupDialog ipcamSetupDialog;
	
	private ExitDialog exitDialog;
	private ResultDialog resultDialog;
	
	//-------------------------------------------------------
	//-------------------------------------------------------
	
	public enum MODE{
		FREE,GAME
	}
	
	public enum IPCAM_STATUS {
		OPEN, CLOSE
	}
	
	private MODE mode;
	private IPCAM_STATUS ipcam_status = IPCAM_STATUS.CLOSE;	
	
	private ImageView img_player1;
	private ImageView img_player2;
	private int player1Percent = 0 ;
	private int player2Percent = 0 ;
	private final int[] xArray = new int[] { 120, 118, 116, 112, 107, 104, 101, 97,
			94, 90, 87, 85, 84, 84, 83, 81, 78, 76, 74, 70, 66, 62, 60, 58, 53,
			50, 48, 45, 43, 41, 39, 37, 34, 31, 30, 30, 29, 29, 29, 31, 33, 34,
			37, 40, 42, 44, 45, 45, 45, 46, 48, 51, 55, 58, 63, 66, 68, 71, 73,
			75, 77, 80, 83, 87, 89, 90, 91, 93, 95, 97, 100, 104, 107, 111,
			114, 116, 118, 120, 121, 123, 126, 129, 133, 139, 143, 146, 149,
			152, 155, 159, 163, 168, 173, 177, 181, 185, 190, 195, 200, 203,
			203 };
	private final int[] yArray = new int[] { 69, 65, 62, 60, 59, 60, 61, 62, 64, 68,
			71, 74, 78, 83, 87, 90, 92, 95, 98, 101, 104, 108, 108, 110, 112,
			113, 114, 114, 115, 116, 117, 118, 120, 123, 125, 129, 133, 136,
			139, 141, 143, 146, 148, 150, 153, 155, 158, 162, 166, 169, 172,
			175, 177, 177, 177, 175, 173, 171, 169, 167, 165, 164, 162, 163,
			166, 169, 173, 176, 178, 181, 182, 183, 182, 180, 178, 175, 173,
			171, 168, 166, 163, 159, 156, 153, 152, 149, 146, 143, 140, 138,
			136, 135, 134, 133, 131, 129, 127, 127, 127, 125, 125 };
	
	private final int[] mapInfoImage = new int[]{R.drawable.m01,R.drawable.m02,R.drawable.m03,R.drawable.m04,R.drawable.m05,R.drawable.m06,R.drawable.m07};
	private final String[] mapInfoText = new String[]{"欢迎来到义大利！ ！\n您正悠游在贝卢诺省的多洛米蒂山脉之中～",
			"贝卢诺省的首府在贝卢诺，下分有69个市镇，\n现任省长是Daniela Larese Filon。",
			"贝卢诺省（Provincia di Belluno）是义大利威尼托的\n一个省，面积3,678平方公里，2011年人口约21万人。",
			"多洛米蒂山脉（Dolomiti）是阿尔卑斯山的一部分，\n位于义大利东北部三个省，其70%地区位于贝卢诺省。",
			"多洛米蒂山脉名字的由来，是由于一位法国矿物学家，\n用白云石（dolomite）来描述这些山的形状和颜色。",
			"许多游客会来到多洛米蒂山脉体验「铁索攀岩」活动，\n这里有很多长距离的徒步小径贯穿在山脉之中。",
			"被称为「Alte vie」(高空小径)。小径从 1 到 8 编号，\n至少需一周的时间才能穿过山峰，沿途设有大量棚屋。"};
	
	private Thread createSocketThread;
	private Thread receiveThread;
	private Thread sendNameDataThread;
	private Thread sendDataThread;
	private Thread sendDisconnectThread;
	private ExecutorService sendexecutor = Executors.newFixedThreadPool(5);
	private Socket socket;
	private long mediaPercent;
	private TextView tv_game_msg;
	private ImageView iv_game_msg_icon;
	private int myNum;
	private boolean isGameOver = false;
	private int finishRanking;//最終排名
	private boolean noRunning;
	private int noRunningCount;
	private TextView tv_player1_name;
	private TextView tv_player2_name;
	private String player1Name;
	private String player2Name;
	private LinearLayout DATA_DEMO;
	private AbsoluteLayout abs_game;
	private ImageView img_player_icon;
	private ImageView img_map;
	private RelativeLayout rl_map_info;
	private ImageView img_info_logo;
	private TextView tv_info_content;
	private ImageView img_info_icon;
	private int mapInfoCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		setContentView(R.layout.activity_data);
		context = this;
		dataCallBack = (DataCallBack) DataActivity.this;
		
		initBT();
		findView();
		setWidget();
		
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		isDataActivityForeground = true;
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        AsyncTaskStarter.startAsyncTask(new StopMusicTask());
        
        //terminate mThread
        threadFlag = false;
    }

    @Override
    protected void onStop() {
    	Log.d(TAG, "onStop");
    	
        super.onStop();
        isDataActivityForeground = false;         
        
        //terminate mThread
        threadFlag = false;
        
        //Disconnect from any active tag connection
            //BluActivity.getBtHandle().closeGatt();
        
        dialogHandler.removeCallbacks(msgRun);
        
        finishVlc();
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			exitDialog = new ExitDialog(context, mode);
			exitDialog.show();
			
			return true;
		default:
			break;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	private void initBT() {
		Bundle bundle = getIntent().getExtras();
		videoName = bundle.getString("videoName");
		device = (BluetoothDevice) getIntent().getExtras().getParcelable("bluetooth.device");
		mode = (MODE) bundle.getSerializable("Mode");
		
		ipcam_status = (IPCAM_STATUS) bundle.getSerializable("IPCAM_MODE");
		if(ipcam_status == null) {
			ipcam_status = IPCAM_STATUS.CLOSE;
		}
		
		user = (User) bundle.getSerializable("bluetooth.user");
		BluActivity.getBtHandle().setUser(user); 
	}

	private void findView() {
		raceStartGantryImg = (ImageView) findViewById(R.id.raceStartGantryImg);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		
		vlcSurface = (SurfaceView) findViewById(R.id.vlcSurface);
		
		DATA_LL = (LinearLayout)findViewById(R.id.DATA_LL);
		
		txv_cal = (TextView) findViewById(R.id.txv_cal);
		txv_step = (TextView) findViewById(R.id.txv_step);
		txv_dis = (TextView) findViewById(R.id.txv_dis);
		
		DATA_DEMO = (LinearLayout) findViewById(R.id.DATA_DEMO);
		abs_game = (AbsoluteLayout) findViewById(R.id.abs_game);
		img_map = (ImageView) findViewById(R.id.img_map);
		img_player1 = (ImageView) findViewById(R.id.img_player1);
		img_player2 = (ImageView) findViewById(R.id.img_player2);
		
		img_player_icon = (ImageView) findViewById(R.id.img_player_icon);
		tv_player1_name = (TextView) findViewById(R.id.tv_player1_name);
		tv_player2_name = (TextView) findViewById(R.id.tv_player2_name);
		
		tv_game_msg = (TextView) findViewById(R.id.tv_game_msg);
		iv_game_msg_icon = (ImageView) findViewById(R.id.iv_game_msg_icon);
		
		btn_ipcam = (Button) findViewById(R.id.btn_ipcam);
		
		rl_map_info = (RelativeLayout) findViewById(R.id.rl_map_info);
		img_info_logo = (ImageView) findViewById(R.id.img_info_logo);
		tv_info_content = (TextView) findViewById(R.id.tv_info_content);
		img_info_icon = (ImageView) findViewById(R.id.img_info_icon);
	}

	private void setWidget() {
		switch (mode) {
		case FREE:
			btn_ipcam.setVisibility(View.GONE);
			abs_game.setVisibility(View.INVISIBLE);
			tv_game_msg.setVisibility(View.GONE);
			DATA_DEMO.setBackgroundResource(R.drawable.c27_demo);
			break;
		case GAME:
			btn_ipcam.setVisibility(View.VISIBLE);
			abs_game.setVisibility(View.VISIBLE);
			tv_game_msg.setVisibility(View.VISIBLE);
			DATA_DEMO.setBackgroundResource(R.drawable.c28);
			break;
		default:
			break;
		}
		
		GlobalVar.setSize(raceStartGantryImg, -1, -1);
		
		////
		
		GlobalVar.setSize(vlcSurface, 280, 200);
		vlcSurface.setVisibility(View.INVISIBLE);
		
		GlobalVar.setMargin(DATA_LL, 0, 250, 0, 0);
		GlobalVar.setSize(DATA_LL, 150, 300);
		
		GlobalVar.setWidth(txv_step, 150);
		GlobalVar.setWidth(txv_dis, 150);
		GlobalVar.setWidth(txv_cal, 150);
		GlobalVar.setMargin(txv_step, 0, 10, 0, 0);
		GlobalVar.setMargin(txv_dis, 0, 10, 0, 0);
		GlobalVar.setMargin(txv_cal, 0, 10, 0, 0);

		txv_step.setTextSize(26);
		txv_dis.setTextSize(26);
		txv_cal.setTextSize(26);
		
		GlobalVar.setSize(img_map, 281, 201);
		GlobalVar.setSize(img_player1, 17, 17);
		GlobalVar.setSize(img_player2, 17, 17);
		GlobalVar.setSize(img_player_icon, 281, 40);
		GlobalVar.setSize(tv_player1_name, 105, 40);
		GlobalVar.setSize(tv_player2_name, 105, 40);
		GlobalVar.setSize(rl_map_info, 710, 90);
		GlobalVar.setSize(img_info_logo, 56, 75);
		GlobalVar.setSize(tv_info_content, 389, 90);
		GlobalVar.setSize(img_info_icon, 250, 75);

		setViewLocation(img_player_icon, 0, 201);
		setViewLocation(tv_player1_name, 35, 201);
		setViewLocation(tv_player2_name, 175, 201);
		setViewLocation(rl_map_info, 286, 0);
		
		GlobalVar.setPadding(tv_game_msg, 90, 0, 0, 0);
		GlobalVar.setSize(tv_game_msg, 850, 90);
		GlobalVar.setSize(iv_game_msg_icon, 250, 175);
		GlobalVar.setSize(btn_ipcam, 170, 90);
		GlobalVar.setMargin(btn_ipcam, 5, 0, 5, 0);
		GlobalVar.setMargin(img_info_logo, 15, 5, 0, 0);
		GlobalVar.setPadding(tv_info_content, 5, 5, 5, 5);
		GlobalVar.setMargin(img_info_icon, 0, 5, 0, 0);
		
		tv_player1_name.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				16 * Math.min(GlobalVar.scale_height, GlobalVar.scale_width));
		tv_player2_name.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				16 * Math.min(GlobalVar.scale_height, GlobalVar.scale_width));
		tv_info_content.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				16 * Math.min(GlobalVar.scale_height, GlobalVar.scale_width));
		tv_game_msg.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				24 * Math.min(GlobalVar.scale_height, GlobalVar.scale_width));
		
		btn_ipcam.setOnClickListener(mClickListener);
		btn_ipcam.setOnLongClickListener(mLongClickListener);
		
		if(mode.equals(MODE.GAME) && ipcam_status.equals(IPCAM_STATUS.OPEN)) {
			//如果是競賽模式已開ipcam,重新競賽就把ipcam打開
			startVlcVideo();
		}
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mode.equals(MODE.GAME)) {
				String ipcamUrl = GlobalVar.getConfig("webcam", "http://admin:admin@210.242.155.15/video.cgi");
				if (ipcamUrl != null && ipcamUrl.length() > 0) {
					startVlcVideo();
				} else {
					ipcamSetupDialog = new IpcamSetupDialog(context);
			    	ipcamSetupDialog.show();
				}
			}
		}
	};

	private OnLongClickListener mLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if(mode.equals(MODE.GAME)) {
				ipcamSetupDialog = new IpcamSetupDialog(context);
		    	ipcamSetupDialog.show();
			}
			return true;
		}
	};

	private void init() {
		resetInitDataStatus();
		
		surfaceView.getHolder().addCallback(this); // <--surfaceview加上	

		dialogHandler = new Handler();
		
		
		/**************************/
		/**************************/
		setAnimation();
		//(mark) musicDialog();
		
		initMusic(R.raw.boysrun_musicdemo); //(demo)
		switch(mode){
		case FREE:
			break;
		case GAME:
			handler.sendEmptyMessage(1);
			setPlayerLocationAndInfo();
			handler.sendEmptyMessageDelayed(6, 5000);
			break;
		default:
			break;
		}
	}
	
	private void initVlcPlayer() {
		/****************************************************************/
		vlcPlayer = new VLCMediaPlayer(context);
		vlcPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				
			}
		});
		vlcPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				
			}
		});
		vlcPlayer.setOnInfoListener(new OnInfoListener() {
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				return false;
			}
		});
		vlcPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return false;
			}
		});
		/****************************************************************/
	}
	
	private void musicDialog() {
//		musicDialog = new MusicDialog(context);
//		musicDialog.show();
	}
	
	private void setVlcSurFace() {
		vlcSurface.getHolder().setFormat(PixelFormat.RGBX_8888);
		vlcSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				try {
					vlcPlayer.stop();
				} catch(Exception e){}
			}
			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				
			}
			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
			}
		});		
	}
	
	@Override
	public void startVlcVideo() {
		ipcam_status = IPCAM_STATUS.OPEN;
		
		String videoPath = GlobalVar.getConfig("webcam", "http://admin:admin@210.242.155.15/video.cgi");
		
		vlcSurface.setVisibility(View.VISIBLE);
		setVlcSurFace();
		
		if(vlcPlayer == null) {
			initVlcPlayer();
		}
		
		vlcPlayer.setDisplay(vlcSurface.getHolder());
		try {
			vlcPlayer.setDataSource(videoPath);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		vlcPlayer.prepareAsync();
	}
	
	@Override
	public void initMusic(final int rawId) {
		MiscMusic.getInstance().setMusicUrl(rawId); 
		AsyncTaskStarter.startAsyncTask(new MusicTask());
		
		//initVlc();
	}
	
	private void setAnimation() {
		raceStartGantryImg.setVisibility(View.VISIBLE);
		raceStartAnimation = (AnimationDrawable) raceStartGantryImg.getBackground();
		raceStartAnimation.start();

		checkIfAnimationDone(raceStartAnimation);
	}
	
	private void checkIfAnimationDone(final AnimationDrawable anim){
	    int timeBetweenChecks = 100;
	    Handler h = new Handler();
	    h.postDelayed(new Runnable(){
	        public void run(){
	            if (anim.getCurrent() != anim.getFrame(anim.getNumberOfFrames() - 1)){
	                checkIfAnimationDone(anim);
	            } else{
	            	raceStartGantryImg.setVisibility(View.GONE);
	            	resetInitDataStatus();
	            }
	        }
	    }, timeBetweenChecks);
	}

	public Thread mThread = new Thread() {
        @Override
        public void run() {
            try {
            	Log.d(TAG, "thread run!");
                while(threadFlag) {
                    sleep(2000); // 休息兩秒, BLE的資料每一筆之間會有休息時間
                    BluActivity.getBtHandle().getData();
                }   
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     *  更新顯示txv
     *  run:(1)onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
     *      (2) 
     * 
     */
    @Override
	public void updateDisplayValues(int steps) {
    	if(!isDataActivityForeground) {
    		// DataActivity不在前景, updateDisplayValues() 不作用
    		return;
    	}
    	
    	Log.i(TAG,"updateDisplayValues");
		if (isGameOver) {
			return;
		}
		
		if (step_val == steps - initStep) {
			noRunningCount++;
			if (noRunningCount >= 500) {
				noRunning = true;
			}
		} else {
			noRunningCount = 0;
			noRunning = false;
		}
		if (initStep < 0) {
    		initStep = steps;
    	}
    	
        /**/
        step_val = steps - initStep;
        
        distance_val = (float) (step_val * 0.45); // 公尺 *0.45
		calorie_val = step_val / 3;
		/**/
		
		distance_val = (float) (step_val * 6);
		
        txv_cal.setText(Misc.genStrFloatByDigit(calorie_val, 3) + "\n" +str_cal);
		txv_step.setText(Math.round(step_val) + "\n" + genStrStepUnit());	//步數取整數
		txv_dis.setText(Misc.genStrFloatByDigit(distance_val / 1000, 3) + "\n" + str_dis);	//公尺轉公里
		
		if (mPlayerThread != null) {
			if (mPlayerThread.quit) {
				isGameOver = true;
				mediaPercent = 100;
				sendData();
				handler.removeMessages(6);
				handler.sendEmptyMessageDelayed(5, 1500);
			} else {
				MediaExtractor mediaExtractor = mPlayerThread.getMediaExtractor();
				if (mediaExtractor != null) {
					try {
						android.media.MediaFormat format=mediaExtractor.getTrackFormat(0);
						long duration = format.containsKey(android.media.MediaFormat.KEY_DURATION) ? format.getLong(android.media.MediaFormat.KEY_DURATION) : -11/*TrackRenderer.UNKNOWN_TIME_US*/;
						long currentPosition = mediaExtractor.getSampleTime();
						if (duration <= 0) {
							new Exception();
						}
						mediaPercent = (long) ((currentPosition * 100) / duration);
//						Log.i("ricky", "duration:" + duration);
//						Log.i("ricky", "currentPosition:" + currentPosition);
//						Log.i("ricky", "percent:" + mediaPercent);
						sendData();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
	@Override
	public void onCharacteristicWrite() {
		threadFlag = true;
		mThread.start();
	}
	
	@Override
	public void changeUpdateval(boolean updateFlag) {
		//mPlayerThread.setUpdateval(updateFlag);
	}
	
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void finishDataActivity() {
		finishVlc();
		
		switch(mode){
		case FREE:
			break;
		case GAME:
			
			sendDisConnect();
			break;
		default:
			break;
		}
		finish();	
	}
	
    /**
     * 釋放vlc
     */
    private void finishVlc() {
    	if(vlcPlayer != null) {
        	vlcPlayer.pause();
        	vlcPlayer.stop();
        	vlcPlayer.release();
        	vlcPlayer = null;
        }
    }
	
	private Bundle geneBundle() {
		Bundle inbundle = getIntent().getExtras();
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("Mode", mode);
		
		if(mode.equals(MODE.FREE)) {
			ipcam_status = IPCAM_STATUS.CLOSE;
		}
		bundle.putSerializable("IPCAM_MODE", ipcam_status);
		
		bundle.putSerializable("bluetooth.user", inbundle.getSerializable("bluetooth.user"));
		bundle.putParcelable("bluetooth.device", (BluetoothDevice) inbundle.getParcelable("bluetooth.device"));
		bundle.putInt("bluetooth.rssi", inbundle.getInt("bluetooth.rssi"));
		
		//競賽名次
		bundle.putInt("bluetooth.finishRanking", finishRanking);
		
		bundle.putString("videoName", videoName);
		
		return bundle;
	}
	
	@Override
	public void gotoActivity(final Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(context, cls);
		intent.putExtras(geneBundle());
		startActivity(intent);
	}
	
	@Override
	public void gotoResultDialog() {
		resultDialog = new ResultDialog(context, mode, device, user, finishRanking);
		resultDialog.show();
	}
    
	/**
	 * 清除txv
	 */
	private void clearDisplayValues() {
		txv_cal.setText(str_null + "\n" + str_cal);
		txv_step.setText(str_null + "\n" + genStrStepUnit());
		txv_dis.setText(str_null + "\n" + str_dis);
	}
	
	//---surface override-----------------------------------------
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(TAG, "surfaceChanged");
		Log.d(TAG_DATA_DEBUG, "surfaceChanged");
		if (mPlayerThread == null) {
			mPlayerThread = new PlayerThread(context, holder.getSurface(), BluActivity.getBtHandle(), videoName, true, mode);
			mPlayerThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG_DATA_DEBUG, "surfaceDestroyed");
		if (mPlayerThread != null) {
			Log.d(TAG_DATA_DEBUG, "這個地方必須中斷mPlayerThread");
			mPlayerThread.setPlayerThreadFlag(false);
		}
	}
	//---surface override-----------------------------------------
	
	
	private void createSocket(final boolean isOnCreate) {
		try {
			if (socket != null) {
				socket.shutdownInput();
				socket.shutdownOutput();

				InputStream in = socket.getInputStream();
				OutputStream ou = socket.getOutputStream();
				try {
					in.close();
					ou.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (createSocketThread != null && createSocketThread.isAlive()) {
			createSocketThread.interrupt();
		}
		createSocketThread = new Thread(new Runnable() {
			@Override
			public void run() {
				int port = 4444;
				try {
					//針對到時候的ip
					String serverIp = GlobalVar.getConfig("serverIp", null);
					socket = new Socket(serverIp, port);
					socket.setKeepAlive(true);
					Message msg = new Message();
					msg.what = 3;
					if (isOnCreate) {
						msg.arg1 = 0;
					} else {
						msg.arg1 = 1;
					}
					handler.sendMessage(msg);
					startReceiveThread();
					sendNameData();
				} catch (UnknownHostException e) {
					Log.i("ricky","server未啟動1");
					handler.sendEmptyMessage(4);
					e.printStackTrace();
				} catch (IOException e) {
					Log.i("ricky","server未啟動2");
					handler.sendEmptyMessage(4);
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		createSocketThread.start();
	}
	
	private void sendNameData() {
		if (sendNameDataThread != null && sendNameDataThread.isAlive()) {
			sendNameDataThread.interrupt();
		}
		sendNameDataThread = new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("action", "name");
					jsonObject.put("name", socket.getLocalAddress().toString());
					jsonObject.put("accountname", user.getName());
					String sendMessage = jsonObject.toString() + "\n";
					OutputStream os = socket.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					bw.write(sendMessage);
					bw.flush();
//					Log.d("ricky", "first Message sent to the server : " + sendMessage);
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		sendNameDataThread.start();
	}
	
	private void sendData() {
		sendDataThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("action", "data");
					jsonObject.put("name", socket.getLocalAddress().toString());
					jsonObject.put("step", mediaPercent);
					jsonObject.put("accountname", user.getName());
					String sendMessage = jsonObject.toString() + "\n";
					OutputStream os = socket.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					bw.write(sendMessage);
					bw.flush();
//					Log.d("ricky", "Message sent to the server : "
//							+ sendMessage);
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
//					handler.sendEmptyMessageDelayed(2,1000);
					Log.i("ricky","應該斷了1");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		sendexecutor.execute(sendDataThread);
	}
	
	private void sendDisConnect() {
		Log.i(TAG,"DisConnect()");
		sendDisconnectThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("action", "disconnect");
					String sendMessage = jsonObject.toString() + "\n";
					OutputStream os = socket.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					bw.write(sendMessage);
					bw.flush();
//					Log.d("ricky", "Message sent to the server : "
//							+ sendMessage);
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					Log.i("ricky","應該斷了2");
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("ricky","爛了");
				}
				try {
					if (socket != null) {
						socket.shutdownInput();
						socket.shutdownOutput();

						InputStream in = socket.getInputStream();
						OutputStream ou = socket.getOutputStream();
						try {
							in.close();
							ou.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		sendexecutor.execute(sendDisconnectThread);
		if (createSocketThread != null) {
			createSocketThread.interrupt();
		}
		if (sendDataThread != null) {
			sendDataThread.interrupt();
		}
		if (receiveThread != null) {
			receiveThread.interrupt();
		}
		if (sendNameDataThread != null) {
			sendNameDataThread.interrupt();
		}
//		dataCallBack = null;
//		mPlayerThread = null;
		handler.removeMessages(0);
		handler.removeMessages(1);
		handler.removeMessages(2);
		handler.removeMessages(3);
		handler.removeMessages(4);
		handler.removeMessages(5);
		handler.removeMessages(6);
	}
	
	private void startReceiveThread(){
		if (receiveThread != null && receiveThread.isAlive()) {
			receiveThread.interrupt();
		}
		receiveThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				InputStream is;
				try {
					is = socket.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String message = null;
					try {
						while ((message = br.readLine()) != null) {
							Log.d("ricky", "Message received from the server : " + message);
							handler.removeMessages(2);
							handler.sendEmptyMessageDelayed(2,15*1000);
							JSONArray jsonArray = new JSONArray(message);
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String name = jsonObject.optString("name");
								int step = jsonObject.optInt("step");
								int num = jsonObject.optInt("num");
								String myName = socket.getLocalAddress().toString();
								if (myName.equalsIgnoreCase(name)) {
									myNum = num;
									finishRanking = jsonObject.optInt("mySort");
								} else {
								}
								switch (num) {
								case 0:
									player1Percent = step;
									player1Name = jsonObject.optString("accountname");
									break;
								case 1:
									player2Percent = step;
									player2Name = jsonObject.optString("accountname");
									break;
								}
								handler.sendEmptyMessage(0);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		receiveThread.start();
	}

	private void setPlayerLocationAndInfo() {
		if (isGameOver) {
			switch (myNum) {
			case 0:
				player1Percent = 100;
				setViewLocation(img_player1, xArray[player1Percent],
						yArray[player1Percent]);
				break;
			case 1:
				player2Percent = 100;
				setViewLocation(img_player2, xArray[player2Percent],
						yArray[player2Percent]);
				break;
			}
			return;
		}
		switch (myNum) {
		case 0:
			//輸贏字串和圖
			if (mediaPercent <= 6) {
				tv_game_msg.setText(getString(R.string.msg_game_start));
				iv_game_msg_icon.setImageResource(R.drawable.g05);
			} else if (mediaPercent > 6 && mediaPercent <= 84) {
				if (noRunning) {
					tv_game_msg.setText(getString(R.string.msg_game_norun_hint));
					iv_game_msg_icon.setImageResource(R.drawable.g04);
				} else {
					if (Math.abs(player1Percent - player2Percent) <= 2) {
						tv_game_msg.setText(getString(R.string.msg_game_halfway_tie));
						iv_game_msg_icon.setImageResource(R.drawable.g03);
					} else if (player1Percent > player2Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_halfway_win));
						iv_game_msg_icon.setImageResource(R.drawable.g01);
					} else if (player1Percent < player2Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_halfway_lose));
						iv_game_msg_icon.setImageResource(R.drawable.g02);
					}
				}
			} else if (mediaPercent > 84) {
				if (noRunning) {
					tv_game_msg.setText(getString(R.string.msg_game_norun_hint));
					iv_game_msg_icon.setImageResource(R.drawable.g04);
				} else {
					if (Math.abs(player1Percent - player2Percent) <= 2) {
						if (player1Percent > player2Percent) {
							tv_game_msg.setText(getString(R.string.msg_game_nearend_tie_win));
							iv_game_msg_icon.setImageResource(R.drawable.g01);
						} else if (player1Percent < player2Percent) {
							tv_game_msg.setText(getString(R.string.msg_game_nearend_tie_lose));
							iv_game_msg_icon.setImageResource(R.drawable.g02);
						} else {
							tv_game_msg.setText(getString(R.string.msg_game_nearend_tie_tie));
							iv_game_msg_icon.setImageResource(R.drawable.g03);
						}
					} else if (player1Percent < player2Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_nearend_lose));
						iv_game_msg_icon.setImageResource(R.drawable.g02);
					} else if (player1Percent > player2Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_nearend_win));
						iv_game_msg_icon.setImageResource(R.drawable.g01);
					}
				}
			}
			break;
		case 1:
			if (mediaPercent <= 6) {
				tv_game_msg.setText(getString(R.string.msg_game_start));
				iv_game_msg_icon.setImageResource(R.drawable.g05);
			} else if (mediaPercent > 6 && mediaPercent <= 84) {
				if (noRunning) {
					tv_game_msg.setText(getString(R.string.msg_game_norun_hint));
					iv_game_msg_icon.setImageResource(R.drawable.g04);
				} else {
					if (Math.abs(player2Percent - player1Percent) <= 2) {
						tv_game_msg.setText(getString(R.string.msg_game_halfway_tie));
						iv_game_msg_icon.setImageResource(R.drawable.g03);
					} else if (player2Percent > player1Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_halfway_win));
						iv_game_msg_icon.setImageResource(R.drawable.g01);
					} else if (player2Percent < player1Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_halfway_lose));
						iv_game_msg_icon.setImageResource(R.drawable.g02);
					}
				}
			} else if (mediaPercent > 84) {
				if (noRunning) {
					tv_game_msg.setText(getString(R.string.msg_game_norun_hint));
					iv_game_msg_icon.setImageResource(R.drawable.g04);
				} else {
					if (Math.abs(player2Percent - player1Percent) <= 2) {
						if (player2Percent > player1Percent) {
							tv_game_msg.setText(getString(R.string.msg_game_nearend_tie_win));
							iv_game_msg_icon.setImageResource(R.drawable.g01);
						} else if (player2Percent < player1Percent) {
							tv_game_msg.setText(getString(R.string.msg_game_nearend_tie_lose));
							iv_game_msg_icon.setImageResource(R.drawable.g02);
						} else {
							tv_game_msg.setText(getString(R.string.msg_game_nearend_tie_tie));
							iv_game_msg_icon.setImageResource(R.drawable.g03);
						}
					} else if (player2Percent < player1Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_nearend_lose));
						iv_game_msg_icon.setImageResource(R.drawable.g02);
					} else if (player2Percent > player1Percent) {
						tv_game_msg.setText(getString(R.string.msg_game_nearend_win));
						iv_game_msg_icon.setImageResource(R.drawable.g01);
					}
				}
			}
			break;
		default:
			break;
		}
		if (player1Name != null) {
			tv_player1_name.setText(player1Name);
		}
		if (player2Name != null) {
			tv_player2_name.setText(player2Name);
		}
		setViewLocation(img_player1, xArray[player1Percent],
				yArray[player1Percent]);
		setViewLocation(img_player2, xArray[player2Percent],
				yArray[player2Percent]);
	}
	
	@SuppressWarnings("deprecation")
	public void setViewLocation(View v, int x, int y) {
		if (v != null) {
			AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
			lp.x = (int) (GlobalVar.scale_width * x);
			lp.y = (int) (GlobalVar.scale_height * y);
			v.setLayoutParams(lp);
		}
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				removeMessages(msg.what);
				setPlayerLocationAndInfo();
				break;
			case 1:
				removeMessages(msg.what);
				createSocket(true);
				break;
			case 2:
				Log.i("ricky","10秒無收到資料，應該斷線，建立新的連線");
				removeMessages(msg.what);
				if (context != null) {
					createSocket(false);
				}
//				Toast.makeText(context, getString(R.string.toast_disconnect_retry), Toast.LENGTH_SHORT).show();
				break;
			case 3:
				removeMessages(msg.what);
				if (msg.arg1 == 0) {
					Toast.makeText(context, getString(R.string.toast_connect_success), Toast.LENGTH_SHORT).show();
				}
				break;
			case 4:
				removeMessages(msg.what);
				Toast.makeText(context, getString(R.string.toast_connect_fail), Toast.LENGTH_SHORT).show();
				break;
			case 5:
				removeMessages(msg.what);
				//gotoActivity(R-esultActivity.class);
				gotoResultDialog();
				break;
			case 6:
				removeMessages(msg.what);
				if (!rl_map_info.isShown()) {
					rl_map_info.setVisibility(View.VISIBLE);
				}
				img_info_icon.setImageResource(mapInfoImage[mapInfoCount%mapInfoImage.length]);
				tv_info_content.setText(mapInfoText[mapInfoCount%mapInfoImage.length]);
				mapInfoCount++; 
				sendEmptyMessageDelayed(6, 20*1000);
				break;
			}
		}
	};

}
