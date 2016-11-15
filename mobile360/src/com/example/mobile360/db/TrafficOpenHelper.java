package com.example.mobile360.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrafficOpenHelper extends SQLiteOpenHelper {

	public TrafficOpenHelper(Context context) {
		super(context, TrafficDBConstants.DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		db.execSQL(TrafficDBConstants.CREATE_TRAFFIC_TABLE_SQL.toString());
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		db.execSQL(TrafficDBConstants.DELETE_TRAFFIC_TABLE_SQL.toString());
		db.execSQL(TrafficDBConstants.CREATE_TRAFFIC_TABLE_SQL.toString());
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}
