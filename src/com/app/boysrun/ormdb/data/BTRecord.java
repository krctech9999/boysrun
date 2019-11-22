package com.app.boysrun.ormdb.data;

import java.util.Date;

import com.app.boysrun.misc.Misc;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class BTRecord implements Comparable<BTRecord> {

	@DatabaseField(generatedId = true)
	private int key;
	
	/**
	 * user name
	 */
	@DatabaseField(canBeNull = false, id = false)
	private String userName;
	
	/**
	 * bluetooth device
	 */
	@DatabaseField(canBeNull = false, id = false)
	private String deviceName;
	
	/**
	 * record date
	 */
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false, id = false)
	private Date date = null;
	
	/**
	 * steps
	 */
	@DatabaseField(canBeNull = false, id = false)
	private int step;
	
	/**
	 * distance
	 */
	@DatabaseField(canBeNull = false, id = false)
	private float distance;
	
	/**
	 * calorie
	 */
	@DatabaseField(canBeNull = false, id = false)
	private float calorie;

//------------------------------------------------------		
	@Override
	public int compareTo(BTRecord another) {
		long thisTime = this.date.getTime();
		long anotherTime = another.date.getTime();
		
		if (thisTime > anotherTime) {
			return 1;
		} else if (thisTime < anotherTime) {
			return -1;
		} else {
			return 0;
		}
	}
//------------------------------------------------------	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = Misc.resetDate(date);
	}
	
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getCalorie() {
		return calorie;
	}

	public void setCalorie(float calorie) {
		this.calorie = calorie;
	}

}
