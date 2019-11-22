package com.app.boysrun.ormdb.data;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.boysrun.misc.GlobalVar;
import com.j256.ormlite.field.DatabaseField;

/**
 * @author albert
 * 使用者
 */
public class User implements Serializable {
	private static final long serialVersionUID = -7634525714507553307L;

	@DatabaseField(canBeNull = false, id = true)
	private String name;

	@DatabaseField(canBeNull = false, id = false)
	private int photoIdx;
	
	public User() {
	}

	public User(String name, int photoIdx) {
		setName(name);
		setPhotoIdx(photoIdx);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPhotoIdx() {
		return photoIdx;
	}

	public void setPhotoIdx(int photoIdx) {
		this.photoIdx = photoIdx;
	}
	
	public int getLevel() {
		int totalKm = getDisKM();
		
		if(totalKm <= 10) {
			return totalKm;
		}
		
		if(totalKm <= 30) {
			return 10 + (totalKm - 10) / 2;
		}
		
		if(totalKm <= 70) {
			return 20 + (totalKm - 30) / 4;
		}
		
		if(totalKm <= 150) {
			return 30 + (totalKm - 70) / 8;
		}
		
		int level = 40 + (totalKm - 150) / 16;
				
		if(level > 100) {
			level = 100;
		}
		
		return level;
	}
	
	/**
	 * 
	 * @return 取公里數
	 */
	public int getDisKM() {
		List<BTRecord> rcdList = new ArrayList<BTRecord>();
		int totalDistance = 0;
		int totalKm = 0;
		
		try {
			rcdList = GlobalVar.recordDao.queryBuilder().
					where().eq("userName", name).query();
			for(BTRecord record : rcdList) {
				totalDistance += record.getDistance();
			}
			totalKm = totalDistance / 1000;
		} catch (SQLException e) {
			e.printStackTrace();
			totalKm = 0;
		} catch (Exception e) {
			e.printStackTrace();
			totalKm = 0;
		}
		return totalKm;
	}

}
