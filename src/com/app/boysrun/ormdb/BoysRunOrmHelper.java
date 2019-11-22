package com.app.boysrun.ormdb;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.app.boysrun.ormdb.data.BTRecord;
import com.app.boysrun.ormdb.data.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class BoysRunOrmHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "BoysRunOrmlite.db";
	private static final int DATABASE_VERSION = 1;

	private Dao<BTRecord, Integer> recordDao = null;
	private Dao<User, String> userDao = null;

	public BoysRunOrmHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(connectionSource, BTRecord.class);
			TableUtils.createTable(connectionSource, User.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
	}

	@Override
	public void close() {
		super.close();
		recordDao = null;
		userDao = null;
	}

	/**
	 * 手環紀錄Dao
	 * @return
	 * @throws SQLException
	 */
	public Dao<BTRecord, Integer> getRecordDao() throws SQLException {
		if (recordDao == null) {
			recordDao = getDao(BTRecord.class);
		}
		return recordDao;
	}
	
	/**
	 * 使用者Dao
	 * @return
	 * @throws SQLException
	 */
	public Dao<User, String> getUserDao() throws SQLException {
		if(userDao == null) {
			userDao = getDao(User.class);			
		}
		return userDao;
	}

}

