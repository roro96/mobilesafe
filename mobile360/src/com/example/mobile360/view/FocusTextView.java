package com.example.mobile360.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 能够获取焦点的自定义TextView
 * @author xxx
 *
 */
public class FocusTextView extends TextView {

	public FocusTextView(Context context) {
		this(context, null);
	}

	public FocusTextView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//重写获取焦点的方法,由系统调用,调用的时候默认就能获取焦点
	@Override
	public boolean isFocused() {
		return true;
	}
}
