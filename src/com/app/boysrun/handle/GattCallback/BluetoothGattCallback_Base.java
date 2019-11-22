package com.app.boysrun.handle.GattCallback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

import com.app.boysrun.ormdb.data.User;

/**
 *  BluetoothGattCallback基底物件
 */
public abstract class BluetoothGattCallback_Base extends BluetoothGattCallback {

	public abstract BluetoothGatt getBluetoothGatt();
	
	public abstract void setUser(User user);
	
	public abstract void getData();
	
	
	/**
	 * @return
	 */
	public abstract long getAcquireBtTime();
	
	/**
	 * @return 步數資料
	 */
	public abstract int getStepsVal();
	
	/**
	 * @return 距離資料
	 */
	public abstract float getDistanceVal();
	
	
}
