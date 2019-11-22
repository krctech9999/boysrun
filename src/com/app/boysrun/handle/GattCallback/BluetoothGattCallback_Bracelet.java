package com.app.boysrun.handle.GattCallback;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

import com.app.boysrun.BluActivity;
import com.app.boysrun.DataActivity;
import com.app.boysrun.handle.UUID_HANDLE;
import com.app.boysrun.misc.GlobalVar;
import com.app.boysrun.misc.Misc;
import com.app.boysrun.ormdb.data.BTRecord;
import com.app.boysrun.ormdb.data.User;
import com.google.common.primitives.Ints;

/**
 * 
 * BluetoothGattCallback - 手環
 * 
 * In this callback, we've created a bit of a state machine to enforce that only
 * one characteristic be read or written at a time until all of our sensors
 * are enabled and we are registered to get notifications.
 * 
 */
public final class BluetoothGattCallback_Bracelet extends BluetoothGattCallback_Base {
	final static String TAG = "BtHandle"; 
	final static String BTXXX = "BTXXX";
	final static String YYYY = "YYYY";
	
	public BluetoothGatt mBluetoothGatt;
	
	private BluetoothDevice device;
	private User user = null;
	private int pairPosition = -1;
	private UUID_HANDLE uuid_handle = null;

    private List<BluetoothGattService> services;
    private List<BluetoothGattCharacteristic> charas;
    private boolean flag_callback = false;

    private int gattStatus = -1;
    
    /* State Machine Tracking */
    private int mState = 0;

	//-------------------------------------------------------
	
	private final int MSG__TIME_SYNC_UPDATE = 0;
	private final int MSG_DATA_UPDATE = 1;
	
	public int steps_val = 0; 
	public float distance_val = 0L; //公尺
	public float calorie_val = 0L;
	
	public volatile long acquireBtField_time1 = 0; //收bt訊息時間1
	//-------------------------------------------------------
    
    /**
     * 建構子
     * @param d
     */
    public BluetoothGattCallback_Bracelet(Context context, BluetoothDevice d, int position) {
    	this.device = d;
    	this.pairPosition = position;
    	
    	if(uuid_handle == null) {
			uuid_handle = new UUID_HANDLE(device);
		}
    	
    	mBluetoothGatt = device.connectGatt(context, false, BluetoothGattCallback_Bracelet.this);
    }
    
    @Override
    public BluetoothGatt getBluetoothGatt() {
    	return mBluetoothGatt;
    }
    
    @Override
    public void setUser(User user) {
    	this.user = user;
    }
    
    private void reset() { 
    	mState = 0; 
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, final int status, int newState) {
        Log.d(TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
        Log.d(BTXXX, "Connection State Change: " + status + " -> " + connectionState(newState));
        
        gattStatus = status;
        
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            /*
             * Once successfully connected, we must next discover all the services on the
             * device before we can read and write their characteristics.
             */
            gatt.discoverServices();
            Log.d(BTXXX, "discoverServices");
        } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
            /*
             * If at any point we disconnect, send a message to clear the weather values
             * out of the UI
             */
        	Log.d(BTXXX, "BluetoothProfile.STATE_DISCONNECTED");
        } else if (status != BluetoothGatt.GATT_SUCCESS) {
            /*
             * If there is a failure at any stage, simply disconnect
             */
            gatt.disconnect();
            Log.e(BTXXX, "gatt status = " + status);
            Log.e(BTXXX, "gatt disconnect");
        }
    }
    
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    	boolean isPairOK = false; //回傳是否配對成功       	
    	
        Log.d(TAG, "Services Discovered: " + status);
        //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
        /*
         * With services discovered, we are going to reset our state machine and start
         * working through the sensors we need to enable
         */
        reset();
        
        services = gatt.getServices();  // 取得藍芽手環GATT規範裏所有的service
        for(BluetoothGattService service : services) {
        	UUID uuid = service.getUuid();
        	Log.i(TAG, "onServicesDiscovered service uuid = " + uuid);
        	
        	charas = service.getCharacteristics(); // 取得藍芽手環GATT規範當中每一種service之中的所有characteristic
        	
        	for(BluetoothGattCharacteristic charac : charas) {
        		UUID uuid_chara = charac.getUuid();
        		
        		if(uuid_chara.equals(uuid_handle.getCharcReadUUID())) {
        			Log.i(TAG, "onServicesDiscovered characteristic uuid = " + uuid_chara);
        			Log.i(TAG, "UUID_RSC_CHARACTERIC_6 = " + uuid_handle.getCharcWriteUUID());
        		} else {
        			
        			// "000033f2-0000-1000-8000-00805f9b34fb"
        			Log.d(BTXXX, "uuid_chara === " + uuid_chara);
        		}
        		
        		//----test----
        		//----新方法(天天手環) 
        		if(uuid_chara.equals(uuid_handle.getCharcReadUUID())) {   // 原始: getCharcWriteUUID
        			Log.d(BTXXX, "Notification 綁定");
        			
        			boolean setCharacNotiResult = mBluetoothGatt.setCharacteristicNotification(charac, true);
        			Log.d(BTXXX, "setCharacteristicNotification() result = " + setCharacNotiResult);
        			
        			if(setCharacNotiResult == true) { //若Notification綁定  = true
        				
        				//(2015/06/26) http://goo.gl/WEQCX3
        				BluetoothGattDescriptor descriptor = charac.getDescriptor(uuid_handle.getDescriptorUUID());
        				
						if (descriptor != null) {
							
							//(wrong) descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
							//(problem solution)http://stackoverflow.com/questions/22676402/ble-device-disconnect-with-android-device-automatically-android-ble
							descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
							
							mBluetoothGatt.writeDescriptor(descriptor);
						} else {
							Log.d(BTXXX, "descriptor = null");
        				}
        				//(2015/06/26) 
						
						
						writeData();
						
						Log.d(BTXXX, "***************************");
        				writeData(new byte[] { (byte) 0xA4 });  //打開計步功能
        				writeData(new byte[] { (byte) 0xB1 });  //讀取當前小時的計步資料和累計到當前小時，今天內的資料
        				writeData(new byte[] { (byte) 0xA1 });  //讀取藍牙端版本號
        				writeData(new byte[] { (byte) 0xB2 });  //讀取7 天的所有資料
        				
        				Log.d("TTT", "onBluetoothPaired");
        				isPairOK = true;
        				Log.d("TTT", "onBluetoothPaired");
        			}
        		}
        		
        		//----test----
        		//----test----            		
        		
//        		BluetoothGattDescriptor gattDescriptor = chara.getDescriptor(
//        		        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//        		gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        		mBluetoothGatt.writeDescriptor(gattDescriptor);
        		
        		//----test----
        		//----test----
        		
        		
        		/**    
        		 *    pdf第一個特徵值
        		 *    UUID_RSC_CHARACTERIC_6
        		 */
        		if(uuid_chara.equals(uuid_handle.getCharcWriteUUID())) {  // 藍芽手環第二個service之中的第六個characteristic用來做綁定的功能, 需要先讀取再寫入
        			Log.v(TAG, "綁定");
        			Log.v(BTXXX, "綁定");
        			Log.v(BTXXX, "uuid_chara = " + uuid_chara);
        			
        			boolean readCharcResult = gatt.readCharacteristic(charac);
        			Log.v(BTXXX, "readCharacteristic result = " + readCharcResult);
        		} else {
        			Log.v(TAG, "無法綁定");
        			Log.v(TAG, "UUID_RSC_CHARACTERIC_6 = " + uuid_handle.getCharcWriteUUID().toString());
        			Log.v(TAG, "uuid_chara = " + uuid_chara.toString());
        		}
        	}
        }
        
//        if(!isPairOK) {
//        	pairPosition = -1; //配對失敗,pairPosition設為負值
//        }
//        Log.v("TTT", "pairPosition = " + pairPosition);
//        
//        BluActivity.blueActivityCallBack.onBluetoothPaired(pairPosition);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        //For each read, pass the data up to the UI thread to update the display
        Log.d(TAG, "onCharacteristicRead()");
        
        Log.d(BTXXX, "=======================");
        Log.d(BTXXX, "onCharacteristicRead()");
        Log.d(BTXXX, "=======================");
        
        Log.d(TAG, "=> characteristic.getUuid() = " + characteristic.getUuid());
        Log.d(BTXXX, "=> characteristic.getUuid() = " + characteristic.getUuid());
        
        // characteristic.getUuid() = 00002a00-0000-1000-8000-00805f9b34fb
        
        if(uuid_handle.getCharcReadUUID().equals(characteristic.getUuid())) { // 這個characteristic 會回傳16位元組的資料
        	try {
				acquireBtField(characteristic);
				//這裡不是英成
			} catch(Exception e) {
				e.printStackTrace();
			}
        }
        
        if(uuid_handle.getCharcWriteUUID().equals(characteristic.getUuid())) {
        	writeData();
        }
    }

    @Override
	public void onDescriptorRead(BluetoothGatt gatt,
			BluetoothGattDescriptor descriptor, int status) {
    	super.onDescriptorRead(gatt, descriptor, status);
    	Log.d(TAG, "onDescriptorRead");
    	Log.d(BTXXX, "onDescriptorRead");
	}

	@Override
	public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
		super.onReliableWriteCompleted(gatt, status);
		Log.d(TAG, "onReliableWriteCompleted");
		Log.d(BTXXX, "onReliableWriteCompleted");
	}

	@Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        //After writing the enable flag, next we read the initial value
        //readNextSensor(gatt);
		
		Log.d(BTXXX, "*********************");
		Log.d(BTXXX, "onCharacteristicWrite");  //英成有callback
		Log.d(BTXXX, "*********************");
		DataActivity.dataCallBack.onCharacteristicWrite();
    }

	/*google BLE sdk api*/
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        /*
         * After notifications are enabled, all updates from the device on characteristic
         * value changes will be posted here.  Similar to read, we hand these up to the
         * UI thread to update the display.
         */
    	Log.d(TAG, "onCharacteristicChanged");
    	Log.d(BTXXX, "onCharacteristicChanged");
        
    	if(uuid_handle.getCharcReadUUID().equals(characteristic.getUuid())) {  // 這個characteristic 會回傳16位元組的資料
        	acquireBtField(characteristic);
        	Log.d("ZZZ", "英成  => Z1");
    	} else if(uuid_handle.getCharcWriteUUID().equals(characteristic.getUuid())) { // 2nd
    		acquireBtField(characteristic);
        	Log.d("ZZZ", "Z2");
    	}
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        //Once notifications are enabled, we move to the next sensor and start over with enable
        //advance();
        //enableNextSensor(gatt);
    	Log.d(TAG, "onDescriptorWrite()");
    	Log.d(BTXXX, "*************************");
    	Log.d(BTXXX, "onDescriptorWrite()");
    	Log.d(BTXXX, "*************************");
    	getData();
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        Log.d(TAG, "Remote RSSI: " + rssi);
        Log.d(BTXXX, "Remote RSSI: " + rssi);
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
    

	/**
	 * 用來與藍芽手環綁定的方式, 未與藍芽手環綁定前無法讀出資料
	 * 
	 * (comment:寫入資料到藍芽)
	 */
	private void writeData() { 
		Log.d(TAG, "writeData()");
		Log.d(BTXXX, "writeData()");
		
		BluetoothGattCharacteristic chara;
		BluetoothGattDescriptor desc;
		BluetoothGattService service = mBluetoothGatt.getService(uuid_handle.getServiceUUID());
		
		Log.d(TAG, "get UUID_WRITE_CHARACTERIC");
		chara = service.getCharacteristic(uuid_handle.getCharcWriteUUID());
		
		//byte[] code = { 0x65, 0x74, 0x2D, 0x37 }; // 固定寫入的四位數組
		
		byte[] code = uuid_handle.getCmdBytes();
		chara.setValue(code);
		
		if (mBluetoothGatt.writeCharacteristic(chara)) {
			Log.d(TAG, "write code = " + code);
		}
	}
	
	/**
	 * 寫入code
	 * @param code
	 */
	private void writeData(byte[] code) {
		BluetoothGattService service = mBluetoothGatt.getService(uuid_handle.getServiceUUID());
		BluetoothGattCharacteristic chara = service.getCharacteristic(uuid_handle.getCharcWriteUUID());
		chara.setValue(code);
		
		if (mBluetoothGatt.writeCharacteristic(chara)) {
			Log.d(BTXXX, "write code = " + code);
		} else {
			Log.e(BTXXX, "writeCharacteristic 失敗");
		}
	}
		
    /**
     * 採取polling的方式向藍芽手環請求資料回傳
     */
	@Override
	public void getData() { 
    	Log.d(BTXXX, "getData()");
    	
        BluetoothGattCharacteristic chara;
        //(mark) BluetoothGattDescriptor desc ;
        
		try {
			BluetoothGattService service = mBluetoothGatt.getService(uuid_handle.getServiceUUID());
			Log.d(BTXXX, "get UUID_READ_CHARACTERIC : " + uuid_handle.getCharcReadUUID());
			chara = service.getCharacteristic(uuid_handle.getCharcReadUUID());
			mBluetoothGatt.readCharacteristic(chara);
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(BTXXX, "exception");
		}
		
		
		/****英成新方法****/
		/****英成新方法****/
		
		
		/****英成新方法****************************/
		//(2015/06/26) http://goo.gl/WEQCX3
		
		if(mBluetoothGatt == null) {
			return;
		}
		
		BluetoothGattService service = mBluetoothGatt.getService(uuid_handle.getServiceUUID());
		chara = service.getCharacteristic(uuid_handle.getCharcReadUUID());
		BluetoothGattDescriptor descriptor = chara.getDescriptor(uuid_handle.getDescriptorUUID());
		
		if (descriptor != null) {
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);

			
		} else {
			Log.e(BTXXX, "descriptor = null");
		}
		//(2015/06/26) 
		/****英成新方法****/
		
    }

	private void acquireBtField(BluetoothGattCharacteristic characteristic) {
		
		if (device.getName().contains("Brac")) {
			int field_step = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 4);
			float field_dis = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 8) / 10;
			float field_cal = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 12) / 10;

			steps_val = field_step;
			distance_val = field_dis; // 公尺
			calorie_val = field_cal;

			
		} else {
			//英成
			acquireBtField_time1 = System.currentTimeMillis();	
			
			final byte[] data = characteristic.getValue();
			
			int cmd_header = Ints.fromByteArray(new byte[] {0, 0, 0, data[0]});
			// Log.d("YYYY1", "cmd_header = " + cmd_header);
		
			switch(cmd_header) {
				case 177: /*B1*/
				steps_val = Ints.fromByteArray(new byte[] {0, 0, data[6], data[7]});
				distance_val = (float) (steps_val * 0.45); // 公尺 *0.45
				calorie_val = steps_val / 3;
				
				Log.d(YYYY, "#################################");
				Log.d(YYYY, "step = " + steps_val);
				Log.d(BTXXX, "field_step (unsigned int) steps = " + steps_val);
				Log.d(BTXXX, "distance_val  = " + distance_val + "公尺");
				break;
				
				case 178: /*B2*/
				break;
				
				default:
					steps_val = Ints.fromByteArray(new byte[] {0, 0, data[6], data[7]});
					distance_val = (float) (steps_val * 0.45); // 公尺 *0.45
					calorie_val = steps_val / 3;
					
					Log.d(YYYY, "#################################");
					Log.d(YYYY, "step = " + steps_val);
					Log.d(BTXXX, "field_step (unsigned int) steps = " + steps_val);
					Log.d(BTXXX, "distance_val  = " + distance_val + "公尺");
				break;	
			}
		}
		
		DataActivity.dataCallBack.changeUpdateval(true);
		mHandler.sendMessage(Message.obtain(null, MSG_DATA_UPDATE, characteristic));
	}
	
	private Handler mHandler = new Handler() {
		/* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         * 處理訊息
         */
        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
        	case MSG__TIME_SYNC_UPDATE:
        		Log.e(BTXXX, "MSG__TIME_SYNC_UPDATE");
        		DataActivity.dataCallBack.updateDisplayValues(steps_val);
        		if(user != null) {
        			updateDataBase(user, device, steps_val, distance_val, calorie_val);
        		}
        		break;
        	case MSG_DATA_UPDATE:
        		Log.e(BTXXX, "MSG_DATA_UPDATE");
        		DataActivity.dataCallBack.updateDisplayValues(steps_val);
        		if(user != null) {
        			updateDataBase(user, device, steps_val, distance_val, calorie_val);
        		}
       			break;
        	}
        }
    };
    
    
	//-------------------------------------------------------

	/**
	 * @param user
	 * @param device
	 * @param steps_val
	 * @param distance_val  (公尺)
	 * @param calorie_val
	 */
	private void updateDataBase(User user, BluetoothDevice device, float steps_val, float distance_val, float calorie_val) {
		try {
			BTRecord record = new BTRecord();
			record.setUserName(user.getName());
			record.setDeviceName(device.getName());
			record.setDate(Misc.resetDate(new Date(System.currentTimeMillis())));

			record.setStep(Math.round(steps_val));
			record.setDistance(distance_val);
			record.setCalorie(calorie_val);

			BTRecord existRecord = null;

			final Date today = Misc.resetDate(new Date(System.currentTimeMillis()));
			final Date tomorrow = Misc.resetDate(Misc.addDays(today, 1));
			List<BTRecord> todayRecordList = GlobalVar.recordDao.queryBuilder().where()
					.eq("deviceName", device.getName()).and().between("date", (Date) today, (Date) tomorrow).and()
					.eq("userName", user.getName()).query();

			try {
				if (todayRecordList.size() > 0) {
					existRecord = todayRecordList.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				existRecord = null;
			}

    		if(existRecord != null) {
    			if(existRecord.getStep() != record.getStep()) {
    				existRecord.setStep(record.getStep());
        			existRecord.setDistance(record.getDistance());
        			existRecord.setCalorie(record.getCalorie());
        			GlobalVar.recordDao.update(existRecord);
        			
        			Log.d(TAG, "ormlite update record");
        			Log.i(TAG, existRecord.getDate().toString()
        					+ "\nuser = " + existRecord.getUserName()
        				 	+ "\nsteps = " + existRecord.getStep() 
        					+ "\ndistance = " + existRecord.getDistance() 
        					+ "\ncalorie = " + existRecord.getCalorie());
    			}
    			
    		} else {
    			GlobalVar.recordDao.create(record);
    			Log.d(TAG, "ormlite create record");
    		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	@Override
	public long getAcquireBtTime() {
		// TODO Auto-generated method stub
		return acquireBtField_time1;
	}

	@Override
	public int getStepsVal() {
		// TODO Auto-generated method stub
		return steps_val;
	}

	@Override
	public float getDistanceVal() {
		// TODO Auto-generated method stub
		return distance_val;
	}
	
}
