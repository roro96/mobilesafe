package com.example.mobile360.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonnumDao {
	public String path = "data/data/com.example.mobile360/files/commonnum.db";

	public List<Group> getGroup() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("classlist", new String[] { "name", "idx" },
				null, null, null, null, null, null);
		List<Group> groupList = new ArrayList<Group>();
		while (cursor.moveToNext()) {
			Group group = new Group();
			group.name = cursor.getString(0);
			group.idx = cursor.getString(1);
			group.childList = getChild(group.idx);
			groupList.add(group);
		}
		cursor.close();
		db.close();
		return groupList;
	}

	public List<Child> getChild(String idx) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
		List<Child> childList = new ArrayList<Child>();
		while (cursor.moveToNext()) {
			Child child = new Child();
			child._id = cursor.getString(0);
			child.number = cursor.getString(1);
			child.name = cursor.getString(2);
			childList.add(child);
		}
		cursor.close();
		db.close();
		return childList;
	}

	public class Child {
		public String _id;
		public String number;
		public String name;
	}

	public class Group {
		public String name;
		public String idx;
		public List<Child> childList;
	}
}
