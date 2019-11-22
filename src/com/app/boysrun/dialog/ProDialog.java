package com.app.boysrun.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.boysrun.R;
import com.app.boysrun.misc.GlobalVar;

public class ProDialog extends AlertDialog {
	Context context;
	
	//widget
	public ProgressBar progressBar1;
	private TextView msgtxv;
	//widget
	
	private OnCancelListener oncancellistener;

	public TextView getMsgTxv() {
		return msgtxv;
	}
	
	public void setDlgMsg(String msg) {
		msgtxv.setText(msg);
	}

	public ProDialog(Context c) {
		super(c);
		context = c;
	}
	
	@Override
	public void setMessage(CharSequence message) {
		super.setMessage(message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		LinearLayout proLin = (LinearLayout) findViewById(R.id.proLin);
		proLin.getLayoutParams().width = GlobalVar.width;

		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		msgtxv = (TextView) findViewById(R.id.loadingtext);
	}

	public void setMyIndeterminate(int resId) {
		//R.drawable.my_progress_indeterminate
		progressBar1.setIndeterminateDrawable(context.getResources().getDrawable(resId));		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (oncancellistener != null) {
				oncancellistener.onCancel(this);
			}
		}
		return true;
	}

	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		oncancellistener = listener;
	}
}
