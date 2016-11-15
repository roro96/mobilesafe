package com.example.mobile360.db.dao;

import java.util.HashMap;
import java.util.Map;

import com.example.mobile360.db.TrafficDBConstants;
import com.example.mobile360.db.TrafficOpenHelper;
import com.example.mobile360.engine.TrafficContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrafficDao {

	private TrafficOpenHelper dbHelper;
	private SQLiteDatabase wDb;
	private SQLiteDatabase rDb;

	public TrafficDao(Context context) {
		dbHelper = new TrafficOpenHelper(new TrafficContext(context));
		wDb = dbHelper.getWritableDatabase();
		rDb = dbHelper.getReadableDatabase();
	}

	public void close() {
		wDb.close();
		rDb.close();
		dbHelper.close();
	}

	public void insertStart(String pacakgeName, String appName, long startTime,
			String networkType, long rx, long tx) {
		ContentValues value = new ContentValues();
		value.put(TrafficDBConstants.COLUMN_PACKAGE_NAME, pacakgeName);
		value.put(TrafficDBConstants.COLUMN_APP_NAME, appName);
		value.put(TrafficDBConstants.COLUMN_START_TIME, startTime);
		value.put(TrafficDBConstants.COLUMN_NETWORK_TYPE, networkType);
		value.put(TrafficDBConstants.COLUMN_RX, rx);
		value.put(TrafficDBConstants.COLUMN_TX, tx);
		wDb.insert(TrafficDBConstants.TABLE_NAME_TRAFFIC, null, value);
	}

	public void updateEnd(String pacakgeName, long endTime, long rx, long tx) {
		// 查询最新的一条记录
		Cursor start = rDb.query(TrafficDBConstants.TABLE_NAME_TRAFFIC, null,
				TrafficDBConstants.COLUMN_PACKAGE_NAME + " = '" + pacakgeName
						+ "' and " + TrafficDBConstants.COLUMN_END_TIME + " is null",
				null, null, null, TrafficDBConstants.COLUMN_START_TIME + " desc", "1");
		if (!start.moveToFirst()) {
			return;
		}
		ContentValues value = new ContentValues();
		value.put(TrafficDBConstants.COLUMN_END_TIME, endTime);
		value.put(
				TrafficDBConstants.COLUMN_RX,
				rx
						- start.getLong(start
								.getColumnIndexOrThrow(TrafficDBConstants.COLUMN_RX)));
		value.put(
				TrafficDBConstants.COLUMN_TX,
				tx
						- start.getLong(start
								.getColumnIndexOrThrow(TrafficDBConstants.COLUMN_TX)));

		wDb.update(
				TrafficDBConstants.TABLE_NAME_TRAFFIC,
				value,
				TrafficDBConstants.COLUMN_ID
						+ "="
						+ start.getInt(start
								.getColumnIndexOrThrow(TrafficDBConstants.COLUMN_ID)),
				null);
		start.close();
	}

	public Map<String, TrafficInfo> queryTotal() {
		Cursor c = rDb.rawQuery("select " + TrafficDBConstants.COLUMN_PACKAGE_NAME
				+ "," + TrafficDBConstants.COLUMN_APP_NAME + ","
				+ TrafficDBConstants.COLUMN_NETWORK_TYPE + ",sum("
				+ TrafficDBConstants.COLUMN_RX + "),sum(" + TrafficDBConstants.COLUMN_TX
				+ ") from " + TrafficDBConstants.TABLE_NAME_TRAFFIC + " where "
				+ TrafficDBConstants.COLUMN_END_TIME + " is not null group by "
				+ TrafficDBConstants.COLUMN_PACKAGE_NAME + ","
				+ TrafficDBConstants.COLUMN_APP_NAME + ","
				+ TrafficDBConstants.COLUMN_NETWORK_TYPE, null);
		Map<String, TrafficInfo> map = new HashMap<String, TrafficInfo>();
		while (c.moveToNext()) {
			String packageName = c.getString(0);
			TrafficInfo item = null;
			if (!map.containsKey(packageName)) {
				item = new TrafficInfo();
				item.packageName = packageName;
				item.appName = c.getString(1);
				map.put(packageName, item);
			} else {
				item = map.get(packageName);
			}
			String networkType = c.getString(2);
			if (networkType.equals(TrafficDBConstants.NETWORK_TYPE_MOBILE)) {
				item.mobileRx = c.getLong(3);
				item.mobileTx = c.getLong(4);
			} else if (networkType.equals(TrafficDBConstants.NETWORK_TYPE_WIFI)) {
				item.wifiRx = c.getLong(3);
				item.wifiTx = c.getLong(4);
			}
		}
		return map;
	}
	
	public class TrafficInfo{
		public String packageName;
		public String appName;
		public long mobileRx;
		public long mobileTx;
		public long wifiRx;
		public long wifiTx;
	}
}
