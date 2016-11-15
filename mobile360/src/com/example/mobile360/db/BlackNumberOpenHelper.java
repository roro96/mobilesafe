package com.example.mobile360.db;

import com.example.mobile360.utils.ConstantValue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	public BlackNumberOpenHelper(Context context) {
		super(context, ConstantValue.BLACKNUMBER_DB, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blacknumber " +
				"(_id integer primary key autoincrement , phone varchar(20), mode varchar(5));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
