package com.app.boysrun.misc;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class MiscMusic {
	private final static String TAG = "MiscMusic";
	
	private static MiscMusic miscMusic = null;
	
	private MediaPlayer mPlayer = new MediaPlayer();
	private int musicRawId;
	private boolean mediaPlayerPrepared = false;
	
	public static MiscMusic getInstance() {
		if(miscMusic == null) {
			miscMusic = new MiscMusic();
		}
		return miscMusic;
	}
	
	public void setMusicUrl(int rawId) {
		musicRawId = rawId;
	}
	
	/**
	 * 播放音樂,重新進入會seek到起點
	 * @param mPlayer
	 */
	public void startMusic() {
		try {
			if (mediaPlayerPrepared == true) {
				mPlayer.seekTo(0);
				mPlayer.start();
			} else {
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer = MediaPlayer.create(GlobalVar.context, musicRawId);
				//mPlayer.prepare();
				mPlayer.start();
				mPlayer.setLooping(true);
				
				mediaPlayerPrepared = true;
			}
			
			Log.d(TAG, "startMusic");
		} catch (Exception e) {
			e.printStackTrace();
			resetPlayer();
		}
	}
	
	/**
	 * 停止結束音樂
	 * 
	 * @param mPlayer
	 */
	public void releaseMusic() {
		try {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "play mp3 exception");
		} finally {
			resetPlayer();
		}
	}

	private void resetPlayer() {
		try { 
			mPlayer.reset();
		} catch(Exception e) {
			
		}
		mediaPlayerPrepared = false;
	}
}
