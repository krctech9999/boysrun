package com.app.boysrun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.MiscNet;

public class InitActivity extends Activity {
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		context = this;
		GlobalVar.screenScale(getWindowManager());

		findView();

		thread.start();
	}

	private void findView() {
		
	}

	private Thread thread = new Thread() {
		@Override
		public void run() {
			try {
				sleep(3000);
			} catch (InterruptedException e) {
			}

			if (MiscNet.isConnected(context)) {
				gotoMainPage();
			} else {
				userWarning();
				finish();
			}
		}
	};

	private void gotoMainPage() {
		Intent intent = new Intent();
		intent.setClass(context, BluActivity.class);
		startActivity(intent);
		finish();
	}

	private void userWarning() {
		uiHandler.sendEmptyMessage(1111);
	}

	private final Handler uiHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1111:
				// "請先連接網路"
				myToast(getString(R.string.link_network_first));
				break;
			}
		}
	};

	private void myToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
}
