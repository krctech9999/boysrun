/**
 * 
 * step same problem
 * 
 * 從新設定影片  initMovie() 
 *  
 */

package com.app.boysrun.handle;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.app.boysrun.BluActivity;
import com.app.boysrun.DataActivity.MODE;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.misc.MiscTmp;

public class PlayerThread extends Thread {
	
	private static final String TAG = "PlayerThread"; 
	private static final String TAG_DATA_DEBUG = "DATA_DEBUG";
	private Context context;
	
	private int delay = 0;
	private boolean pause = false;
	public boolean quit = false;
	
	private MediaExtractor mediaExtractor;
	private MediaCodec mediaCodec;
	private Surface surface;
	
	private float old_val ;
	private int old_step = -1;
	
   // private boolean updateval = false;
    private volatile boolean playerThreadFlag = false;

	//----------
    private String videoName;
	private BtHandle bthandle;
	private MODE mode;

	public PlayerThread(Context context, Surface surface, BtHandle bthandle,
			String videoName, boolean playerFlag, MODE mode) {
		this.context = context;
		this.old_val = -1;
		this.old_step = -1;
		this.surface = surface;
		this.bthandle = bthandle;
		this.videoName = videoName;
		this.mode = mode;
		setPlayerThreadFlag(playerFlag);
	}
	
	public MediaExtractor getMediaExtractor() {
		return mediaExtractor;
	}

	public String generateVideoPath(String videoName) {
		// 先取得資料夾路徑
		String videoPath = Misc.getBoysRunMoviePath(context) + videoName;
		
		Log.d(TAG, "videoPath = " + videoPath);
		return videoPath;
	}
	
	public void setPlayerThreadFlag(boolean playerThreadFlag) {
		this.playerThreadFlag = playerThreadFlag;
	}
	
//	public void setUpdateval(boolean updateval) {
//		Log.d("VVV", "set updateval = " + updateval);
//		this.updateval = updateval;
//	}
	
	/**
	 *  測試用(albert) 
	 */
//	private void getDelay_test1() {
//		if(bthandle.distance_val <= 0) {
//			pause = true;
//			delay = 10;
//			return;
//		}
		
//		if(old_val == -1) {
//			old_val = bthandle.distance_val;
//			pause = true;
//			delay = 10;
//			//Log.d(TAG, "paused by started!");
//			return;
//		}
//		if(old_step == -1) {
//			old_step = bthandle.getStepsVal();
//			pause = true;
//			delay = 10;
//			//Log.d(TAG, "paused by started!");
//			return;
//		}
		
		
//		int diff = 0;
//		float frame_dist = 2.8f; // 9.28 cm / frame
//		
//		if(updateval) {
//			updateval = false;
//		
//			diff = bthandle.getStepsVal() - old_step;
//			old_step = bthandle.getStepsVal();
//			
//			Log.d(TAG, "diff = " + diff);
//			
//			if(diff > 5) {
//				delay = (int) (frame_dist * 30 / (diff * 0.6));
//			}
//			
//			if (delay < 0) {
//				delay = 0;
//			}
//			
//			if(diff == 0) {
//				pause = true;
//				Log.d(TAG, "@@@@ pause = true");
//				delay = 0;
//			} else {
//				Log.d(TAG, "#### pause = false");
//				pause = false;
//			}
//			
//			delay = delay / 2;
//			
//			Log.i(TAG, "diff = " + diff);
//			Log.i(TAG, "Delay Time = " + delay);
//		}
		
		///////////////////////////----------------------
//	}
	
	private int pause_count = 0;
	
	private long previous_acquire_time = 0;
	private int previous_step = 0;
	
	
	private void getDelay() {
		BluetoothDevice device = bthandle.getDevice();

		try {
			if(device.getName().contains(BtHandle.DEVICE_NAME_UMHD)) {
				//腳踏車
				getDelay_Bicycle();
			} else {
				//手環
				getDelay_Brace();
			}
		} catch (Exception e) {}
	}
	
	private void getDelay_Bicycle() {
		delay = 0;
		
		float bicycleSpeed = BluActivity.getBtHandle().getBicycleSpeed();
		
		if(bicycleSpeed == 0) {
			pause = true;
			delay = 10;
			return;
		} else {
			pause = false;
			delay = (int) (33.3 * (3.0 / bicycleSpeed));
			Log.d(TAG, "bicycle delay = " + delay);
		}
	}
		
	/**
	 * 延遲處理
	 */
	private void getDelay_Brace() {
		delay = 0;
		
///      (勿使用,不work  don't use this, avoid onLeScan() twice)   
///		if(bthandle.getGattStatus() != BluetoothGatt.GATT_SUCCESS) {
///			bthandle.reStart();
///			pause = true;
///			return;
///		}
		
		if(bthandle.getStepsVal() == 0) {
			pause = true;
			return;
		}
		
		if(previous_acquire_time == 0) {
			previous_acquire_time = bthandle.getAcquireBtTime();
		}
		
		//Log.d("WWW", "diff=" + (System.currentTimeMillis() - previous_acquire_time));
		if(System.currentTimeMillis() - previous_acquire_time >= 300 && bthandle.getStepsVal() > previous_step) {
			
			int variation = bthandle.getStepsVal() - previous_step;  //變化量
			//Log.d("WWW", "advance = " + advance);
			
			/*速度=步數除時間*/
			try {
				long timeVariation = bthandle.getAcquireBtTime() - previous_acquire_time;
				float speed = (float)(variation*10000) / (float)timeVariation;
				
				delay = (int)(timeVariation / (8 * variation));
				
				Log.d("SPEED", "time vari = " + timeVariation);
				Log.d("SPEED", "speed = " + speed);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			previous_acquire_time = bthandle.getAcquireBtTime();
			previous_step = bthandle.getStepsVal();
			
//			if(variation < 3) {
//				delay = 50;
//				Log.d("DELAY", "add delay " + delay);
//			}
		}
		
		if(System.currentTimeMillis() - bthandle.getAcquireBtTime() < 2000 && bthandle.getAcquireBtTime() != 0) {
			pause = false;
			//Log.d("WWW", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		} else {
			if(pause == false) {
				Log.d("DELAY", "switch pause pause");
			}
			
			pause = true;
		}
	}
	
	/**
	 *  original method
	 */
//	private void getDelay_test2() {
//		Log.d("WWW", "getDelay");
//		
//		if(bthandle.getDistanceVal() <= 0) {
//			pause = true;
//			delay = 10;
//			return;
//		}
//		if(old_val == -1) {
//			old_val = bthandle.getDistanceVal();
//			pause = true;
//			delay = 10;
//			//Log.d(TAG, "paused by started!");
//			return;
//		}
//		
//		float diff = 0;
//		float frame_dist = 2.8f; // 9.28 cm / frame
//		
//		Log.d("XXXX", "updateval = " + updateval);
//		if(updateval){
//			diff = (float) (bthandle.getDistanceVal() - old_val);
//			if(diff > 0.1) {
//				delay = (int) ((frame_dist * 30 / diff) / 5);
//			}
//			
//			if (delay < 0) {
//				delay = 0;
//			}
//			
//			old_val = bthandle.getDistanceVal();
//			updateval = false;
//			
//			Log.d(TAG, "diff = " + diff);
//			if((bthandle.getDistanceVal() == -1) || diff < 0.05) {
//				pause = true;
//				delay = 0;
//			} else {
//				Log.d(TAG, "Delay Time = " + delay);
//				pause = false;
//			}
//		} else {
//			if(pause_count < 25) {
//				pause_count++;
//			} else {
//				pause = true;
//				Log.d("VVV", "pause變換");
//				pause_count = 0;
//			}
//		}
//		
//		Log.d(TAG, "pause = " + pause);
//	}

	/**
	 * 從新設定影片
	 */
	private void initMovie() {
		if(mediaCodec != null) {
			mediaCodec.stop();
			mediaCodec.release();
			mediaCodec = null;
		}
		
		if(mediaExtractor != null) {
			mediaExtractor.release();
			mediaExtractor = null;
		}
		
		mediaExtractor = new MediaExtractor();
		try {
			/*tmp raw video file*/
			Log.d(TAG, "tmp videoname = " + videoName);
			
			if(MiscTmp.checkFileExistRaw(context, videoName)) {
				/*tmp raw video file*/
				/*tmp raw video file*/
				final AssetFileDescriptor afd = context.getResources().openRawResourceFd(MiscTmp.tmpVidonameToVideo(videoName));
				mediaExtractor.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				/*tmp raw video file*/
				/*tmp raw video file*/
			} else {
				mediaExtractor.setDataSource(generateVideoPath(videoName));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
		}

		for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
			MediaFormat format = mediaExtractor.getTrackFormat(i);
			String mime = format.getString(MediaFormat.KEY_MIME);
			if (mime.startsWith("video/")) {
				Log.d("Decoder", "mime type:" + mime);
				mediaExtractor.selectTrack(i);
				mediaCodec = MediaCodec.createDecoderByType(mime);
				mediaCodec.configure(format, surface, null, 0);
				break;
			}
		}

		if (mediaCodec == null) {
			Log.e("DecodeActivity", "Can't find video info!");
			return;
		}
		
		try {
			mediaCodec.start();
		} catch(Exception e) {
			//Android裝置須要重新開機
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		initMovie();
		
		try {
			ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
		
			//(mark) ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
		
			BufferInfo info = new BufferInfo();
			boolean isEOS = false;
			long startMs = System.currentTimeMillis();
		
			mediaExtractor.seekTo(5 * 1000000, 0); // shift 5 30secs for start
			while (playerThreadFlag) {
				
				if (!isEOS) {
					int inIndex = -1;
				
					try {
						inIndex = mediaCodec.dequeueInputBuffer(10000);
						// Log.d(TAG_DATA_DEV, "inIndex = " + inIndex);
					} catch(Exception e) {
						e.printStackTrace();
						inIndex = -1;
					}
					
					if (inIndex >= 0) {
						ByteBuffer buffer = inputBuffers[inIndex];
						int sampleSize = mediaExtractor.readSampleData(buffer, 0);
						// Log.d(TAG_DATA_DEV, "sampleSize = " + sampleSize);
					
						if (sampleSize >= 0) {
							mediaCodec.queueInputBuffer(inIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
							mediaExtractor.advance();
						} else {
							// We shouldn't stop the playback at this point, just pass the EOS
							// flag to decoder, we will get it again from the
							// dequeueOutputBuffer
							Log.d(TAG /*"DecodeActivity"*/, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
							mediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
							isEOS = true;
						}
					}
				} else {
					/*****/
					
					/*
					Log.d(TAG_DATA_DEBUG, "seek to");
					mediaExtractor.seekTo(5 * 1000000, 0); // shift 5 30secs for start
					 */
					
					isEOS = false;
					switch(mode){
					case FREE:
						initMovie();		
						break;
					case GAME:
						quit = true;
						break;
					}
					/*****/
				}

				int outIndex = mediaCodec.dequeueOutputBuffer(info, 10000);
				//if(run==0) pause = true; 
			
				switch (outIndex) {
				case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
					///Log.d(TreadmillActivity, "INFO_OUTPUT_BUFFERS_CHANGED");
					//(mark) outputBuffers = decoder.getOutputBuffers();
				
					break;
				case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
					///Log.d(TreadmillActivity, "New format " + mediaCodec.getOutputFormat());
					break;
				case MediaCodec.INFO_TRY_AGAIN_LATER:
					///Log.d(TreadmillActivity, "dequeueOutputBuffer timed out!");
					break;
				default:
					//(mark)ByteBuffer buffer = outputBuffers[outIndex];
				
					//Log.v(TreadmillActivity, "We can't use this buffer but render it due to the API limit, " + buffer);
					
					getDelay();
					
					///Log.d(TreadmillActivity, "delay=" + delay);
					///Log.d(TreadmillActivity, "pause=" + pause);
					
					try {
						sleep(delay) ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
					try {
						mediaCodec.releaseOutputBuffer(outIndex, true);
					} catch(Exception e){}
				
					break;
				}

				// All decoded frames have been rendered, we can stop playing now
				if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
					///Log.d(TreadmillActivity, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
					//-- break;
				}

				if(quit) {
					break;
				}
				
				/****(dev dev dev) 不延遲播放mark起來  **/
				while(playerThreadFlag && pause) {
					try {
						sleep(100);  //100
						getDelay();
						//Log.d(TreadmillActivity, "while pause");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				/****(dev dev dev) 不延遲播放mark起來  **/
			}
		} catch(Exception e) {
			e.printStackTrace();
			Log.e(TAG_DATA_DEBUG, ">>>>> " + e.toString());
		}

		Log.d(TAG_DATA_DEBUG, ">>>>> decoder release");	
		
		try {
			if(mediaCodec != null) {
				mediaCodec.stop();
				mediaCodec.release();
				mediaExtractor.release();
			}
		}catch(Exception e) {
			
		}
	}

}
