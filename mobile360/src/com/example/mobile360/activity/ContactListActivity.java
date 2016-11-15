package com.example.mobile360.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.bean.ContactInfo;
import com.example.mobile360.db.dao.ContactDao;

public class ContactListActivity extends Activity {
	private List<ContactInfo> infos;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		infos = ContactDao.findAll(this);
		lv = (ListView) findViewById(R.id.lv_contact);
		lv.setAdapter(new ContactsAdapter());
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent data = new Intent();
				data.putExtra("phone", infos.get(position).getPhone());
				setResult(0, data);
				finish();
			}
		});
	}

	private class ContactsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.contact_item_listview, null);
			} else {
				view = convertView;
			}
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view
					.findViewById(R.id.tv_phone);
			tv_name.setText(infos.get(position).getName());
			tv_phone.setText(infos.get(position).getPhone());
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

}
