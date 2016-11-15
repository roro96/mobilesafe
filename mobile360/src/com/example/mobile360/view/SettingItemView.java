package com.example.mobile360.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;

public class SettingItemView extends RelativeLayout {

	private CheckBox cb_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(getContext(), R.layout.setting_item_view, this);

		TextView tv_setting_title = (TextView) findViewById(R.id.tv_setting_title);
		tv_des = (TextView) findViewById(R.id.tv_setting_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);

		initAttrs(attrs);

		tv_setting_title.setText(mDestitle);
	}

	private void initAttrs(AttributeSet attrs) {
		mDestitle = attrs
				.getAttributeValue(ConstantValue.NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(ConstantValue.NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(ConstantValue.NAMESPACE, "deson");
	}

	public boolean isCheck() {
		return cb_box.isChecked();
	}

	public void setCheck(boolean isCheck) {
		cb_box.setChecked(isCheck);
		if (isCheck) {
			tv_des.setText(mDeson);
		} else {
			tv_des.setText(mDesoff);
		}
	}
}
