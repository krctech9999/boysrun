package com.app.boysrun.misc;

import java.sql.SQLException;
import java.util.Date;

import android.util.Log;

import com.app.boysrun.ormdb.data.BTRecord;
import com.app.boysrun.ormdb.data.User;

public class DevTest {
	
	//---------------------------
	//-- FOR DEV-----------------
	//---------------------------
	public static void forDebug(User user, String deviceName) {
		
		try {
			if (GlobalVar.recordDao.queryBuilder().
					where().eq("deviceName", deviceName).and().
					eq("userName", user.getName()).query().size() <= 1) {
				for (int i = -60; i <= 0; i++) {
					int randomStep = Misc.randInt(0, 15);

					BTRecord dummyRecord = new BTRecord();
					dummyRecord.setDeviceName(deviceName);
					dummyRecord.setUserName(user.getName());

					dummyRecord.setStep(300 + (i % 2 == 0 ? 25 : 0) + 25 * randomStep);
					dummyRecord.setCalorie(dummyRecord.getStep() / 20);
					dummyRecord.setDistance(dummyRecord.getStep() * 1000 / 1350); // *1000 (公尺)

					final Date today = Misc.resetDate(new Date(System.currentTimeMillis()));
					dummyRecord.setDate(Misc.resetDate(Misc.addDays(today, i)));
					GlobalVar.recordDao.create(dummyRecord);
				}
			}
		} catch (SQLException e) {
			Log.e("TAG", "SQLException");
			e.printStackTrace();
		} 
	}
	
	
	//---------------------------
	//-- FOR DEV-----------------
	//---------------------------
}
