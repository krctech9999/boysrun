package com.app.boysrun.handle;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class UUID_HANDLE {
	private final static String TAG = "UUID_HANDLE"; 
	
	private final int Bracelet = 0;
	private final int M04 = 1;
	
	private BluetoothDevice device;
	
	private static final UUID UUID_RSC_SERVICE_2 = getUUID_TI("ff00");     //服務UUID 
	private static final UUID UUID_RSC_CHARACTERIC_6 = getUUID_TI("ff06"); //第0 個特徵值
	
	//**************
	
	/***  英成UUID  */
	/**
	 * 服務UUID
	 */
	private static final UUID UUID_EN_SERVICE = genUUID("000055ff-0000-1000-8000-00805f9b34fb"); 
	/**
	 * 第0 個特徵值
	 */
	private static final UUID UUID_EN_CHARC0 = genUUID("000033f1-0000-1000-8000-00805f9b34fb");
	/**
	 * 第1 個特徵值  (許可權:通知,最大資料長度20byte，用於藍牙端返回資料到手機apk 端)
	 */
	private static final UUID UUID_EN_CHARC1 = genUUID("000033f2-0000-1000-8000-00805f9b34fb");
	
	private static final UUID UUID_DESCRIPTOR = genUUID("00002902-0000-1000-8000-00805f9b34fb"); // 定義本機的UUID
	
	
	/**************************************/
	private static final UUID getUUID_TI(String u) {
		return UUID.fromString("f000" + u + "-0451-4000-b000-000000000000"); 
	}
	
	private static final UUID genUUID(String id) {
		return UUID.fromString(id);
	}
	/**************************************/

	
	/**
	 * device name 
	 * 	"Bracelet" : 原始藍色  0
	 * 	"M04"      : 天天          1
	 */
	int type = M04; //預設是天天 
	
	public UUID_HANDLE(BluetoothDevice device) {
		this.device = device;
		
		String devName = device.getName();
		if(devName.contains("Bracelet")) {
			type = Bracelet;
			Log.i(TAG, "type = Bracelet");
		} else if(devName.contains("M04")){
			type = M04;
			Log.i(TAG, "type = 天天");
		}
	}
	
	public UUID getServiceUUID() {
		if(device.getName().contains("Bracelet")) {
			return UUID_RSC_SERVICE_2;
		} else {
			return UUID_EN_SERVICE;
		}
	}
	
	/**
	 * 第0 個特徵值
	 * 只寫:最大資料長度20byte，用於手機端寫命令到藍牙端
	 * @return
	 */
	public UUID getCharcWriteUUID() {
		switch(type) {
		case Bracelet:
			return UUID_RSC_CHARACTERIC_6;
		case M04:
			return UUID_EN_CHARC0;
		default:
			return UUID_EN_CHARC0;
		}
	}
	
	/**
	 * 第1 個特徵值
	 * 讀出:最大資料長度20byte，用於藍牙端返回資料到手機apk 端。
	 * @return
	 */
	public UUID getCharcReadUUID() {
		switch(type) {
		case Bracelet:
			return UUID_RSC_CHARACTERIC_6;
		case M04:
			return UUID_EN_CHARC1;
		default:
			return UUID_EN_CHARC1;	
		}
	}
	
	public UUID getDescriptorUUID() {
		return UUID_DESCRIPTOR;
	}
	
	public byte[] getCmdBytes() {
		byte[] code;
		
		switch(type) {
		case Bracelet:
			code = new byte[] { 0x65, 0x74, 0x2D, 0x37 };
			break;
		case M04:
			code = new byte[] { (byte) 0xA4, (byte) 0xB1, (byte) 0xB2};
			break;
		default:
			code = new byte[] { (byte) 0xA4, (byte) 0xB1, (byte) 0xB2};
			break;
		}
		
		return code;
	}
}
