package com.example.mobile360.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;

public class SettingClickView extends RelativeLayout {

	private TextView tv_setting_title;
	private TextView tv_setting_des;

	public SettingClickView(Context context) {
		this(context, null);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View view = View.inflate(getContext(), R.layout.setting_click_view,
				this);

		tv_setting_title = (TextView) view.findViewById(R.id.tv_setting_title);
		tv_setting_des = (TextView) view.findViewById(R.id.tv_setting_des);
	}
	
	public void setTitle(String title) {
		tv_setting_title.setText(title);
	}

	public void setDes(String des) {
		tv_setting_des.setText(des);
	}
}
