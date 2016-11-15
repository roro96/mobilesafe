package com.example.mobile360.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.mobile360.bean.ContactInfo;

public class ContactDao {

	public static List<ContactInfo> findAll(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		ArrayList<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = contentResolver.query(uri,
				new String[] { "contact_id" }, null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			if (id != null) {
				ContactInfo info = new ContactInfo();
				info.setId(id);
				Cursor dataCursor = contentResolver.query(datauri,
						new String[] { "data1", "mimetype" },
						"raw_contact_id=?", new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String data = dataCursor.getString(0);
					String type = dataCursor.getString(1);
					if ("vnd.android.cursor.item/name".equals(type)) {
						info.setName(data);
					} else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
						info.setPhone(data);
					}
				}
				contactInfoList.add(info);
				dataCursor.close();
			}
		}
		cursor.close();
		return contactInfoList;

	}
}
