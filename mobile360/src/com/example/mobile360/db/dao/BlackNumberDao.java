package com.example.mobile360.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile360.bean.BlackNumberInfo;
import com.example.mobile360.db.BlackNumberOpenHelper;
import com.example.mobile360.utils.ConstantValue;

public class BlackNumberDao {
	private BlackNumberOpenHelper blackNumberOpenHelper;

	private BlackNumberDao(Context context) {
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}

	public static BlackNumberDao blackNumberDao = null;
	private int mode;

	public static BlackNumberDao getInstance(Context context) {
		if (blackNumberDao == null) {
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}

	public void insert(String phone, String mode) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert(ConstantValue.BLACKNUMBER, null, values);
		db.close();
	}

	public void delete(String phone) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		db.delete(ConstantValue.BLACKNUMBER, "phone = ?",
				new String[] { phone });
		db.close();
	}

	public void update(String phone, String mode) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update(ConstantValue.BLACKNUMBER, values, "phone = ?",
				new String[] { phone });
		db.close();
	}

	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(ConstantValue.BLACKNUMBER, new String[] {
				"phone", "mode" }, null, null, null, null, "_id desc");
		ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.phone = cursor.getString(0);
			info.mode = cursor.getString(1);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}

	public List<BlackNumberInfo> find(int index) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select phone,mode from blacknumber order by _id desc limit ?,20;",
						new String[] { index + "" });
		ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.phone = cursor.getString(0);
			info.mode = cursor.getString(1);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}

	public int getCount() {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int count = 0;
		Cursor cursor = db.rawQuery("select count (*)from blacknumber;", null);
		while (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	public int getMode(String phone) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(ConstantValue.BLACKNUMBER,
				new String[] { "mode" }, "phone = ?", new String[] { phone },
				null, null, null);
		while (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
}
