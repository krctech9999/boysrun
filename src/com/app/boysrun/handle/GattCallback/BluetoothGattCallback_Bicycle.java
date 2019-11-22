package com.app.boysrun.handle.GattCallback;

import java.util.List;
import java.util.UUID;

import android.app.Instrumentation;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.app.boysrun.BluActivity;
import com.app.boysrun.DataActivity;
import com.app.boysrun.ormdb.data.User;

public class BluetoothGattCallback_Bicycle extends BluetoothGattCallback_Base {

	private final String TAG = "BluetoothGattCallback_Bicycle";
	private final String TAG2 = "TAG2";

	public BluetoothGatt mBluetoothGatt = null;

	private BluetoothDevice device = null;
	private User user = null;
	private int pairPosition = -1;
	
	// -----------------------------------------------------------------------------------
	private float speed_val = 0; 
	private float distance_val;   //公尺
	private float cadence_val;
	private float stride_val;
	
	// -----------------------------------------------------------------------------------
	private final static int MSG_DATA_UPDATE = 1;
	
	// -----------------------------------------------------------------------------------

	public BluetoothGattCallback_Bicycle(Context context, BluetoothDevice d, int position) {
		this.device = d;
		this.pairPosition = position;
		
		//(albert)  (context, true
		mBluetoothGatt = device.connectGatt(context, true, BluetoothGattCallback_Bicycle.this);
	}

	// -----------------------------------------------------------------------------------
	/* Client Configuration Descriptor */
	private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private static final UUID UUID_RUNNING_SPEED_AND_CADENCE = getUUID("1814");

	private static final UUID UUID_RSC_MEASUREMENT = getUUID("2a53");
	private static final UUID UUID_RSC_FEATURE = getUUID("2a54");
	private static final UUID UUID_SENSOR_LOCATION = getUUID("2a5d");
	
    private static final UUID UUID_UMEDIA_KEYPAD_SERVICE = getUUID("ffe0");
    private static final UUID UUID_UMEDIA_KEYPAD_CHARACTERISTICS = getUUID("ffe1");

	private static final int FLAG_RSC_MEASUREMENT_INSTANTANEOUS_STRIDE_LENGTH_PRESENT = 0x01;
	private static final int FLAG_RSC_MEASUREMENT_TOTAL_DISTANCE_PRESENT = 0x02;

	private int flagMeasurement;
	private boolean flagISLP;
	private boolean flagTDP;

	private static final int FLAG_RSC_FEATURE_INSTANTANEOUS_STRIDE_LENGTH_MEASUREMENT_SUPPORTED = 0x01;
	private static final int FLAG_RSC_FEATURE_TOTAL_DISTANCE_MEASUREMENT_SUPPORTED = 0x02;
	private static final int FLAG_RSC_FEATURE_WALKING_OR_RUNNING_STATUS_SUPPORTED = 0x04;
	private static final int FLAG_RSC_FEATURE_CALIBRATION_PROCEDURE_SUPPORTED = 0x08;
	private static final int FLAG_RSC_FEATURE_MULTIPLE_SENSOR_LOCATIONS_SUPPORTED = 0x10;
	
	 private int keyval = -1;
     private int notified = 0;

	enum sensor_loc {
		SENSOR_LOCATION_OTHER, SENSOR_LOCATION_TOP_OF_SHOE, SENSOR_LOCATION_IN_SHOE, SENSOR_LOCATION_HIP, SENSOR_LOCATION_FRONT_WHEEL, SENSOR_LOCATION_LEFT_CRANK, SENSOR_LOCATION_RIGHT_CRANK, SENSOR_LOCATION_LEFT_PEDAL, SENSOR_LOCATION_RIGHT_PEDAL, SENSOR_LOCATION_FRONT_HUB, // front
																																																																					// hub
		SENSOR_LOCATION_REAR_DROPOUT, // rear dropout
		SENSOR_LOCATION_CHAINSTAY, // Chainstay
		SENSOR_LOCATION_REAR_WHEEL, // Rear Wheel
		SENSOR_LOCATION_REAR_HUB, // Rear Hub
		SENSOR_LOCATION_CHEST, // chest
		SENSOR_LOCATION_RESERVED,
	};

	private sensor_loc sensor_location;

	private static final UUID getUUID(String u) {
		String str = "0000" + u + "-0000-1000-8000-00805f9b34fb";
		UUID uuid = UUID.fromString(str);
		return uuid;
	}

	// -----------------------------------------------------------------------------------

	/* State Machine Tracking */
	private int mState = 0;

	private void reset() {
		mState = 0;
	}

	private void advance() {
		mState++;
	}

	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		boolean isPairOK = false; //回傳是否配對成功 
		
		Log.d(TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
		if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
			/*
			 * Once successfully connected, we must next discover all the
			 * services on the device before we can read and write their
			 * characteristics.
			 */
			gatt.discoverServices();
			
			// 配對成功
			Log.d(TAG, "UMHD 配對成功");
			isPairOK = true;
			
		} else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
			/*
			 * If at any point we disconnect, send a message to clear the
			 * weather values out of the UI
			 */
			isPairOK = false;
			
			gatt.close();  ///<===============
			Log.d("TEST", "onConnectionStateChange gatt.close()");
						
		} else if (status != BluetoothGatt.GATT_SUCCESS) {
			/*
			 * If there is a failure at any stage, simply disconnect
			 */
			gatt.disconnect();
			isPairOK = false;
		}
		
		if(!isPairOK) {
        	pairPosition = -1; //配對失敗,pairPosition設為負值
        }
        Log.v(TAG, "pairPosition = " + pairPosition);
        BluActivity.blueActivityCallBack.onBluetoothPaired(pairPosition);
	}

	private boolean flag_callback = false;

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		Log.d(TAG, "Services Discovered: " + status);
		// mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS,
		// "Enabling Sensors..."));
		/*
		 * With services discovered, we are going to reset our state machine and
		 * start working through the sensors we need to enable
		 */
		reset();
		List<BluetoothGattService> services = gatt.getServices();
		for (BluetoothGattService service : services) {
			UUID uuid = service.getUuid();
			Log.d(TAG, "onServicesDiscovered service uuid=" + uuid);
			
			List<BluetoothGattCharacteristic> charas = service.getCharacteristics();
			for (BluetoothGattCharacteristic chara : charas) {
				UUID u = chara.getUuid();
				Log.d(TAG, "onServicesDiscovered characteristic uuid=" + u);
				if (u.equals(UUID_RSC_FEATURE)) {
					gatt.readCharacteristic(chara);
				}
			}
		}
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		// For each read, pass the data up to the UI thread to update the
		// display
		Log.d(TAG, "onCharacteristicRead");
		if (characteristic.getUuid().equals(UUID_RSC_FEATURE)) {
			int b = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			Log.d(TAG, "RSC Feature value = " + b);
			if ((b & FLAG_RSC_FEATURE_INSTANTANEOUS_STRIDE_LENGTH_MEASUREMENT_SUPPORTED) > 0) {
				Log.d(TAG, "RSC Feature : has instantaneous stride length measurement!");
			} else {
			}
			if ((b & FLAG_RSC_FEATURE_TOTAL_DISTANCE_MEASUREMENT_SUPPORTED) > 0) {
				Log.d(TAG, "RSC Feature : has total distance measurement!");
			} else {
			}
			if ((b & FLAG_RSC_FEATURE_WALKING_OR_RUNNING_STATUS_SUPPORTED) > 0) {
				Log.d(TAG, "RSC Feature : has walking or running status supported!");
			} else {
			}
			if ((b & FLAG_RSC_FEATURE_CALIBRATION_PROCEDURE_SUPPORTED) > 0) {
				Log.d(TAG, "RSC Feature : has calibration procedure supported!");
			} else {
			}
			if ((b & FLAG_RSC_FEATURE_MULTIPLE_SENSOR_LOCATIONS_SUPPORTED) > 0) {
				Log.d(TAG, "RSC Feature : has multiple sensor locations supported!");
				getSensorLocation();
				return;
			} else {
			}
			sendNotify();
			return;
		} // UUID_RSC_FEATURE

		if (characteristic.getUuid().equals(UUID_SENSOR_LOCATION)) {
			int b = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			Log.d(TAG, "RSC Sensor Location value = " + b);
			switch (b) {
			case 0:
				sensor_location = sensor_loc.SENSOR_LOCATION_OTHER;
				Log.d(TAG, "RSC Sensor Location = other");
				break;
			case 1:
				sensor_location = sensor_loc.SENSOR_LOCATION_TOP_OF_SHOE;
				Log.d(TAG, "RSC Sensor Location = top of shoe");
				break;
			case 2:
				sensor_location = sensor_loc.SENSOR_LOCATION_IN_SHOE;
				Log.d(TAG, "RSC Sensor Location = in shoe");
				break;
			case 3:
				sensor_location = sensor_loc.SENSOR_LOCATION_HIP;
				Log.d(TAG, "RSC Sensor Location = hip");
				break;
			case 4:
				sensor_location = sensor_loc.SENSOR_LOCATION_FRONT_WHEEL;
				Log.d(TAG, "RSC Sensor Location = front wheel");
				break;
			case 5:
				sensor_location = sensor_loc.SENSOR_LOCATION_LEFT_CRANK;
				Log.d(TAG, "RSC Sensor Location = left crank");
				break;
			case 6:
				sensor_location = sensor_loc.SENSOR_LOCATION_RIGHT_CRANK;
				Log.d(TAG, "RSC Sensor Location = right crank");
				break;
			case 7:
				sensor_location = sensor_loc.SENSOR_LOCATION_LEFT_PEDAL;
				Log.d(TAG, "RSC Sensor Location = left pedal");
				break;
			case 8:
				sensor_location = sensor_loc.SENSOR_LOCATION_RIGHT_PEDAL;
				Log.d(TAG, "RSC Sensor Location = right pedal");
				break;
			case 9:
				sensor_location = sensor_loc.SENSOR_LOCATION_FRONT_HUB;
				Log.d(TAG, "RSC Sensor Location = front hub");
				break;
			case 10:
				sensor_location = sensor_loc.SENSOR_LOCATION_REAR_DROPOUT;
				Log.d(TAG, "RSC Sensor Location = rear dropout");
				break;
			case 11:
				sensor_location = sensor_loc.SENSOR_LOCATION_CHAINSTAY;
				Log.d(TAG, "RSC Sensor Location = chainstay");
				break;
			case 12:
				sensor_location = sensor_loc.SENSOR_LOCATION_REAR_WHEEL;
				Log.d(TAG, "RSC Sensor Location = rear whell");
				break;
			case 13:
				sensor_location = sensor_loc.SENSOR_LOCATION_REAR_HUB;
				Log.d(TAG, "RSC Sensor Location = rear hub");
				break;
			case 14:
				sensor_location = sensor_loc.SENSOR_LOCATION_CHEST;
				Log.d(TAG, "RSC Sensor Location = rear chest");
				break;
			default:
				sensor_location = sensor_loc.SENSOR_LOCATION_RESERVED;
				Log.d(TAG, "RSC Sensor Location = reserved");
				break;
			}
			sendNotify();
			return;
		} // UUID_SENSOR_LOCATION

	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
	}
	
	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		/*
		 * After notifications are enabled, all updates from the device on
		 * characteristic value changes will be posted here. Similar to read, we
		 * hand these up to the UI thread to update the display.
		 */
		// Log.d(TAG, "onCharacteristicChanged :" + characteristic.getUuid());
		
		if(characteristic.getUuid().equals(UUID_UMEDIA_KEYPAD_CHARACTERISTICS)) {
			keyval = characteristic.getIntValue( BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    		Log.d(TAG2, "Umedia KeyPad Changed code = " + keyval);
    		
    		myInstrumentation(keyval);
    		
    	} // UUID_UMEDIA_KEYPAD_CHARACTERISTICS
		
		if (characteristic.getUuid().equals(UUID_RSC_MEASUREMENT)) {
			//Log.d(TAG, "RSC Measurement Changed");
			flagMeasurement = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			//Log.d(TAG, "RSC Measurement flag = " + Integer.toHexString(flagMeasurement));
			if ((flagMeasurement & FLAG_RSC_MEASUREMENT_INSTANTANEOUS_STRIDE_LENGTH_PRESENT) > 0) {
				flagISLP = true;
			} else {
				flagISLP = false;
			}
			if ((flagMeasurement & FLAG_RSC_MEASUREMENT_TOTAL_DISTANCE_PRESENT) > 0) {
				flagTDP = true;
			} else {
				flagTDP = false;
			}

			int speedcount = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
			speed_val = (float) (speedcount * 3.6 / 256.0);
			//Log.d(TAG, "RSC Measurement speedcount = " + speedcount);
			int cadence = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
			cadence_val = (float) (cadence);
			//Log.d(TAG, "RSC Measurement cadence = " + cadence);
			int strideLength = 0;
			int totalDistance = 0;

			if (flagISLP) {
				strideLength = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 4);
				//Log.d(TAG, "RSC Measurement strideLength = " + strideLength);
				stride_val = (float) (strideLength / 100.0);
				if (flagTDP) {
					totalDistance = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 6);
					distance_val = (float) (totalDistance / 10);
					//Log.d(TAG, "RSC Measurement totalDistance = " + totalDistance);
				}
			} else {
				if (flagTDP) {
					totalDistance = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 4);
					distance_val = (float) (totalDistance / 10.0);
					//Log.d(TAG, "RSC Measurement totalDistance = " + totalDistance);
				}
			}
			
			
			mHandler.sendMessage(Message.obtain(null, MSG_DATA_UPDATE, characteristic));
		} // UUID_RSC_MEASUREMENT
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		// Once notifications are enabled, we move to the next sensor and start
		// over with enable
		Log.d(TAG, "onCharacteristicWrite");
		
		if(notified == 1) { 
    		sendNotify2();
    	}
	}

	@Override
	public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		Log.d(TAG, "Remote RSSI: " + rssi);
	}

	private String connectionState(int status) {
		switch (status) {
		case BluetoothProfile.STATE_CONNECTED:
			return "Connected";
		case BluetoothProfile.STATE_DISCONNECTED:
			return "Disconnected";
		case BluetoothProfile.STATE_CONNECTING:
			return "Connecting";
		case BluetoothProfile.STATE_DISCONNECTING:
			return "Disconnecting";
		default:
			return String.valueOf(status);
		}
	}

	// -----------------------------------------------------------------------------------

	public void sendNotify() {
		BluetoothGattCharacteristic chara;
		BluetoothGattDescriptor desc;

		BluetoothGattService service = mBluetoothGatt.getService(UUID_RUNNING_SPEED_AND_CADENCE);     

        chara = service.getCharacteristic(UUID_RSC_MEASUREMENT);
        mBluetoothGatt.setCharacteristicNotification(chara, true);
        desc = chara.getDescriptor(CONFIG_DESCRIPTOR);
        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE );
        mBluetoothGatt.writeDescriptor(desc);
        notified = 1;
	}
	
    public void sendNotify2() {
        BluetoothGattCharacteristic chara1;
        BluetoothGattDescriptor desc1;

        BluetoothGattService service1 = mBluetoothGatt.getService(UUID_UMEDIA_KEYPAD_SERVICE);     
        
        chara1 = service1.getCharacteristic(UUID_UMEDIA_KEYPAD_CHARACTERISTICS);
        mBluetoothGatt.setCharacteristicNotification(chara1, true);
        desc1 = chara1.getDescriptor(CONFIG_DESCRIPTOR);
        desc1.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE );
        mBluetoothGatt.writeDescriptor(desc1);
        notified = 2;
    }

	public void getSensorLocation() {
		BluetoothGattCharacteristic chara;

		BluetoothGattService service = mBluetoothGatt.getService(UUID_RUNNING_SPEED_AND_CADENCE);
		chara = service.getCharacteristic(UUID_SENSOR_LOCATION);
		mBluetoothGatt.readCharacteristic(chara);
	}
	
	//--------------------------
	int steps_val = 0; 
	
	Instrumentation inst;
	
	private void myInstrumentation(final int keyNum) {
		if (inst == null) {
			inst = new Instrumentation();
		}

		Log.d("keyNum", "keyNum = " + keyNum);

		Thread t = new Thread(new Runnable() {
			public void run() {
				switch (keyNum) {
				case 1:
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
					break;
				case 2:
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
					break;
				case 3:
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
					break;
				case 4:
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
					break;
				case 5:
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
					break;
				}
			}
		});

		t.start();
	}
	
	/**
	 * 更新數據
	 */
	private Handler mHandler = new Handler() {
		/* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         * 處理訊息
         */
        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
  
        	case MSG_DATA_UPDATE:
        		// Log.d(TAG, "MSG_DATA_UPDATE");
        		
        		steps_val = (int) distance_val / 6;
        		
        		if(DataActivity.dataCallBack != null) {
        			DataActivity.dataCallBack.updateDisplayValues(steps_val);
        			
        			BluActivity.getBtHandle().setBicycleSpeed(speed_val * 36 / 256);
        		}
        		
        		if(user != null) {
        			//(demo mark) updateDataBase(user, device, steps_val, distance_val, calorie_val);
        		}
        		
       			break;
        	}
        }
    };

	// -----------------------------------------------------------------------------------

	@Override
	public BluetoothGatt getBluetoothGatt() {
		// TODO Auto-generated method stub
		return mBluetoothGatt;
	}

	@Override
	public void setUser(User user) {
		// TODO Auto-generated method stub
		this.user = user;
	}

	@Override
	public void getData() {
		// TODO Auto-generated method stub

	}

	@Override
	public long getAcquireBtTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStepsVal() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getDistanceVal() {
		// TODO Auto-generated method stub
		return 0;
	}

}
