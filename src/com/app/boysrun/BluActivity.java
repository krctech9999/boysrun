package com.app.boysrun;

import java.util.ArrayList;
import java.util.List;

import krc.app.update.SoftwareChecker;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.boysrun.callback.BlueCallBack;
import com.app.boysrun.dialog.ProDialog;
import com.app.boysrun.handle.BtHandle;
import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;

public class BluActivity extends Activity implements BluetoothAdapter.LeScanCallback, BlueCallBack {
	private static final String TAG = "BluActivity";
	private Context context;
	public static BlueCallBack blueActivityCallBack;
	
	//private static final String DEVICE_NAME0 = "Bracelet"; //藍色手環
	private static final String DEVICE_NAME2 = "M04";   //天天手環
	private static final String DEVICE_NAME3 = "UMHD"; //腳踏車  "UMHD-1502"
	
	private ProDialog proDialog;
	
	//widget
	
	private ListView deviceListView;
	
	private Button btn_scan;
	private Button btn_ignore;
	private Button btn_done;
	
	//Adapter
	private DeviceAdapter deviceAdapter = null;
	
	//藍芽物件變數
	private int pairPosition = -1;
	
	private int selectedPosition = -1;
    private BluetoothAdapter mBluetoothAdapter;
	private SparseArray<BluetoothDevice> mDevices = new SparseArray<BluetoothDevice>();
	/**
	 * bluetooth訊號強度
	 */
	private SparseIntArray bluetoothRssi = new SparseIntArray();
	
	private static BtHandle bthandle = null;
	
	public static BtHandle getBtHandle() {
		return bthandle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.w(TAG, "onCreate");
		context = BluActivity.this;
		blueActivityCallBack = (BlueCallBack) this;
		
		setContentView(R.layout.activity_blu);
		
		proDialog = new ProDialog(context);
		
		findView();
		initView();
		init();
		blutoothInit();
		
		//軟體更新
		new SoftwareChecker(context, "https://www.krctech.com.tw/software/api/check").check();
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "onResume");
        
        /*
        if (mBluetoothAdapter == null) {
        	Log.e(TAG, "mBluetoothAdapter is null");
        	return;
        }
        */
        
        /* 未啟動bt
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            finish();
            return;
        }

        /*
         * 不支援LE 
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mStopRunnable);
        mBluetoothAdapter.stopLeScan(this);
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			//配對頁完全關閉時,藍芽close連結
			closeBtHandle();
			
			try {
				//等1秒關閉
				Thread.sleep(1000);
				
				if(bthandle != null) {
					bthandle.nullGatt();
				}
				
//				mBluetoothAdapter.disable();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			break;
		default:
			break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
    
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		Log.d(TAG, "Blu onStop");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void findView() {
		deviceListView = (ListView) findViewById(R.id.deviceListView);
		
		btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_ignore = (Button) findViewById(R.id.btn_ignore);
		btn_done = (Button) findViewById(R.id.btn_done);
		
	}
	
	private void initView() {
		GlobalVar.setSize(deviceListView, -1, 550);
		GlobalVar.setMargin(deviceListView, 10, 10, 10, 10); // deviceListView的margine邊界
		
		
		GlobalVar.setSize(btn_scan, 150, 60);
		GlobalVar.setSize(btn_ignore, 150, 60);
		GlobalVar.setSize(btn_done, 150, 60);
	}
	
	public void waitLoading(ProDialog mDialog, final boolean start, final String msg, Integer animationRes) {
		//是否可被cancel
		mDialog.setCancelable(false);
		if (start) {
			mDialog.show();
			mDialog.setDlgMsg(msg);
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
	
	private void myToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	private void init() {
		btn_scan.setTextSize(26);
		btn_ignore.setTextSize(26);
		btn_done.setTextSize(26);
	}

	private void blutoothInit() {
		mBluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
		
		deviceListView.setFocusable(true);
		deviceListView.setFocusableInTouchMode(true);
		
		deviceListView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedPosition = position;
				deviceAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		deviceListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				pairDevice(position);
			}
		});
		
		btn_scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startScan();
			}
		});
		
		btn_done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//final BluetoothDevice device = mDevices.valueAt(position);
		    	//final int btStrength = bluetoothRssi.valueAt(position);
		    	
				if(pairPosition < 0){
					myToast("尚未配对");
					return;
				}
				
		    	Bundle bundle = new Bundle();
				bundle.putParcelable("bluetooth.device", device);
				bundle.putInt("bluetooth.rssi", btStrength);
				
				Intent intent = new Intent();
				intent.setClass(context, EntryActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		startScan();
	}
	
	/**
     * 開始掃描
     */
    private void startScan() {
    	waitLoading(proDialog, true, "", null);
    	mDevices.clear();
    	if(deviceAdapter != null) {
    		deviceAdapter.notifyDataSetChanged();
    		
    	}
    	
        mBluetoothAdapter.startLeScan(this);
        mHandler.postDelayed(mStopRunnable, 5000); 
    }
    
    private Handler mHandler = new Handler() {
    	
    };
	
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    
    /**
     * 停止掃描
     */
    private void stopScan() {
    	waitLoading(proDialog, false, "", null);
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminateVisibility(false);
        
        if(mDevices.size() > 0) {
        	myToast("扫描蓝芽装置成功");
        } else {
        	myToast("无法扫描到任何蓝芽装置");
        }
        
        if(deviceAdapter == null) {
        	deviceAdapter = new DeviceAdapter();
        	deviceListView.setAdapter(deviceAdapter);
        	deviceListView.setSelector(R.drawable.a04_selector);
        } else {
        	deviceAdapter.notifyDataSetChanged();
        }
    }
    
    class ViewHolder {
    	public ViewHolder(View v) {
    		itemView = v;
    	}
    	
    	View itemView;
    	
		TextView deviceTxv; //裝置名稱
		TextView statusTxv; //連線狀態
		ImageView deviceStrengthImg; //訊號強度 
	}
    
    private class DeviceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.device_layout, null));
				
				holder.deviceTxv = (TextView) holder.itemView.findViewById(R.id.deviceTxv);
				holder.statusTxv = (TextView) holder.itemView.findViewById(R.id.statusTxv);
				holder.deviceStrengthImg = (ImageView) holder.itemView.findViewById(R.id.deviceStrengthImg);
				
				convertView = holder.itemView;
				convertView.setTag(holder);
			} else {
	        	holder = (ViewHolder) convertView.getTag();
	        }
			
			final BluetoothDevice device = mDevices.valueAt(position);
			
			GlobalVar.setPadding(holder.itemView, 0, 0, 0, 0);
			
			GlobalVar.setSize(holder.deviceTxv, 500, 80);
			GlobalVar.setMargin(holder.deviceTxv, 45, 0, 0, 0);
			
			GlobalVar.setSize(holder.statusTxv, 570, 80);
			GlobalVar.setSize(holder.deviceStrengthImg, 95, 50);
			
			
			holder.deviceTxv.setTextSize(30);
			holder.deviceTxv.setTextColor(Color.WHITE);
			
			holder.statusTxv.setTextSize(30);
			holder.statusTxv.setTextColor(Color.WHITE);
			
			holder.deviceTxv.setText(device.getName() + "(" + device.getAddress() + ")");
			holder.statusTxv.setText(pairStatus(position));
			
			final int btStrength = bluetoothRssi.valueAt(position);
			Log.d(TAG, "bt strength = " + btStrength + "db");
			holder.deviceStrengthImg.setImageResource(Misc.getStrengthImg(btStrength));
			
			return convertView;
		}
    }
    
    private String pairStatus(int position) {
    	if(pairPosition == position) {
    		return "配对成功";
    	} else {
    		return "连线状态";
    	}
    }
    
    private BluetoothDevice device = null;
    private int btStrength; 
    
    private void pairDevice(final int position) {
    	
    	device = mDevices.valueAt(position);
    	btStrength = bluetoothRssi.valueAt(position);
    	
//    	Bundle bundle = new Bundle();
//		bundle.putParcelable("bluetooth.device", device);
//		bundle.putInt("bluetooth.rssi", btStrength);
		
		startBtHandle(device, position);
    }

	/* (non-Javadoc)
	 * @see android.bluetooth.BluetoothAdapter.LeScanCallback#onLeScan(android.bluetooth.BluetoothDevice, int, byte[])
	 * scan result
	 * 
	 * 訊號強度(rssi = received signal strength indication 負值 db)
	 * http://stackoverflow.com/questions/17874852/what-is-meaning-of-negative-dbm-in-signal-strength
	 * http://osxdaily.com/2011/12/28/check-wireless-signal-strength-optimize-wifi-networks-mac-os-x/
	 */
	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		Log.i(TAG, "New LE Device(name): " + device.getName() + ", rssi = " + rssi);
		try {
			if (deviceNameCheck(device) && rssi != 0) {
				mDevices.put(device.hashCode(), device);
				bluetoothRssi.put(device.hashCode(), rssi);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean deviceNameCheck(BluetoothDevice device) {
		List<String> nameList = new ArrayList<String>();
		nameList.add(DEVICE_NAME2);
		nameList.add(DEVICE_NAME3);
		
		for(String name : nameList) {
			if(device.getName().contains(name)) {
				return true;
			}
		}
		return false;
	}

	/**************************************************/
	
	private void startBtHandle(BluetoothDevice device, int position) {
		if(bthandle == null) {
			//singleton
			bthandle = new BtHandle(/*context, position, device*/);
		}
		
		bthandle.startGatt(context, position, device);
	}
	
	private void closeBtHandle() {
		if (bthandle != null) {
			bthandle.closeGatt();
		}
		
		pairPosition = -1;
	}
	
	@Override
	public void onBluetoothPaired(int position) {
		// TODO Auto-generated method stub
		this.pairPosition = position;
		Log.d(TAG, "position = " + pairPosition);
		
		if(pairPosition < 0) {
			uiHandler.sendEmptyMessage(DEVICE_PAIR_FAIL);
			uiHandler.sendEmptyMessage(DEVICE_STATUS_CHANGE);
		} else {
    		uiHandler.sendEmptyMessage(DEVICE_PAIR_OK);
			uiHandler.sendEmptyMessage(DEVICE_STATUS_CHANGE);
		}
	}
	/**************************************************/
	private final int DEVICE_PAIR_FAIL = 111111;
	private final int DEVICE_PAIR_OK = 3333333;
	private final int DEVICE_STATUS_CHANGE = 112266;
	
	private final Handler uiHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "msg.what = " + msg.what);
			
			switch (msg.what) {
			case DEVICE_PAIR_OK:
				myToast("装置配对成功!");
				break;
			case DEVICE_PAIR_FAIL:
				myToast("装置配对失败!");
				break;
			case DEVICE_STATUS_CHANGE:
				deviceAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
}
