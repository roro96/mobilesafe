package com.example.mobile360.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	public static String path = "data/data/com.example.mobile360/files/address.db";
	private static String mAddress = "未知号码";

	public static String getAddress(String phone) {
		mAddress = "未知号码";
		String regularExpression = "^1[3-8]\\d{9}";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (phone.matches(regularExpression)) {
			phone = phone.substring(0, 7);
			Cursor cursor = db.query("data1", new String[] { "outkey" },
					"id = ?", new String[] { phone }, null, null, null);
			if (cursor.moveToNext()) {
				String outkey = cursor.getString(0);
				Cursor indexCursor = db.query("data2",
						new String[] { "location" }, "id = ?",
						new String[] { outkey }, null, null, null);
				if (indexCursor.moveToNext()) {
					mAddress = indexCursor.getString(0);
				}
			} else {
				mAddress = "未知号码";
			}
		} else {
			int length = phone.length();
			switch (length) {
			case 3:
				mAddress = "报警电话";
				break;
			case 4:
				mAddress = "模拟器";
				break;
			case 5:
				mAddress = "服务电话";
				break;
			case 7:
				mAddress = "本地电话";
				break;
			case 8:
				mAddress = "本地电话";
				break;
			case 11:
				String area = phone.substring(1, 3);
				Cursor cursor = db.query("data2", new String[] { "location" },
						"area = ?", new String[] { area }, null, null, null);
				if (cursor.moveToNext()) {
					mAddress = cursor.getString(0);
				} else {
					mAddress = "未知号码";
				}
				break;
			case 12:
				String area1 = phone.substring(1, 4);
				Cursor cursor2 = db.query("data2", new String[] { "location" },
						"area = ?", new String[] { area1 }, null, null, null);
				if (cursor2.moveToNext()) {
					mAddress = cursor2.getString(0);
				} else {
					mAddress = "未知号码";
				}
				break;
			}
		}

		return mAddress;

	}
}
