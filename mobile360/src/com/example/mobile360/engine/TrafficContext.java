package com.example.mobile360.engine;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

public class TrafficContext extends ContextWrapper {

	public TrafficContext(Context base) {
		super(base);
	}

	@Override
	public File getDatabasePath(String name) {
		// 判断是否存在sd卡
		boolean sdExist = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
		if (sdExist) {
			// 获取sd卡路径
			String dbDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			// 数据库所在目录
			dbDir += "/database";
			// 数据库路径
			String dbPath = dbDir + "/" + name;

			File file = new File(dbDir);
			if (!file.exists())
				file.mkdir();
			boolean isFileCreateSuccess = false;
			File dbFile = new File(dbPath);
			if (dbFile.exists()) {
				isFileCreateSuccess = true;
				if (isFileCreateSuccess) {
					return dbFile;
				}
			} else {
				try {
					// 创建文件
					isFileCreateSuccess = dbFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			return null;
		}
		return null;
	}

	/*
	 * 调用此方法获取数据库
	 * 
	 * @see
	 * android.content.ContextWrapper#openOrCreateDatabase(java.lang.String,
	 * int, android.database.sqlite.SQLiteDatabase.CursorFactory,
	 * android.database.DatabaseErrorHandler)
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
				getDatabasePath(name), null);
		return result;
	}

}
