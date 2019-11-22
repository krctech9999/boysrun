package com.app.boysrun.misc.asynctask;

import android.os.AsyncTask;

import com.app.boysrun.misc.MiscMusic;

public class StopMusicTask extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... params) {
		MiscMusic.getInstance().releaseMusic();
		return null;
	}
}
