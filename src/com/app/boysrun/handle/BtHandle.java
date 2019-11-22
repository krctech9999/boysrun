package com.app.boysrun.handle;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;

import com.app.boysrun.handle.GattCallback.BluetoothGattCallback_Base;
import com.app.boysrun.handle.GattCallback.BluetoothGattCallback_Bicycle;
import com.app.boysrun.handle.GattCallback.BluetoothGattCallback_Bracelet;
import com.app.boysrun.ormdb.data.User;

public class BtHandle {
	
	//-------------------------------------------------------
	/*腳踏車專用*/
	private float speed_bicycle = 0;
	
	public void setBicycleSpeed(float speed) {
		speed_bicycle = speed;
	}
	
	public float getBicycleSpeed() {
		return speed_bicycle;
	}
	
	//-------------------------------------------------------
	
	public static final String DEVICE_NAME_M04 = "M04";   //天天手環
	public static final String DEVICE_NAME_UMHD = "UMHD"; //腳踏車  "UMHD-1502"
	
    private BluetoothGattCallback_Base mGattCallback;
	private BluetoothGatt mBluetoothGatt;   // <-----------------------------------------------
	
	private BluetoothDevice device;
	
	private int gattStatus = -1;
	
	//-------------------------------------------------------
    
	public BtHandle() {
		
	}
	
	public void setUser(User user) {
		mGattCallback.setUser(user);
	}
	
	public BluetoothDevice getDevice() {
		return this.device;
	}
	
	public void startGatt(Context context, int pairPosition, BluetoothDevice device) {
		
		this.device = device; 
		
		if(device.getName().contains(DEVICE_NAME_UMHD)) {
			mGattCallback = new BluetoothGattCallback_Bicycle(context, device, pairPosition);
			mBluetoothGatt = mGattCallback.getBluetoothGatt();			
		} else if(device.getName().contains(DEVICE_NAME_M04)) {
			mGattCallback = new BluetoothGattCallback_Bracelet(context, device, pairPosition);
			mBluetoothGatt = mGattCallback.getBluetoothGatt();	
		}
		
		//mBluetoothGatt.getDevicesMatchingConnectionStates(states)
	}
	
	public void closeGatt() {
		//Disconnect from any active tag connection
        if (mBluetoothGatt != null) {
        	mBluetoothGatt.disconnect();
        	mBluetoothGatt.close();
        }
	}
	
	public void nullGatt() {
		if(mBluetoothGatt != null) {
			mBluetoothGatt = null;
		}
	}
	
	public long getAcquireBtTime() {
		return mGattCallback.getAcquireBtTime();
	}
	
	public int getGattStatus() {
		return gattStatus;
	}
	
	public int getStepsVal() {
		return mGattCallback.getStepsVal();
	}
	
	public float getDistanceVal() {
		return mGattCallback.getDistanceVal();
	}
	
    //-------------------------------------------------------
    
    /**
     * 採取polling的方式向藍芽手環請求資料回傳
     */
    public void getData() { 
    	mGattCallback.getData();
    }
}
