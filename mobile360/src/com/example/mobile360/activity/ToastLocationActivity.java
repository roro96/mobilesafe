package com.example.mobile360.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;

public class ToastLocationActivity extends Activity {

	private ImageView iv_drag;
	private TextView tv_top;
	private TextView tv_bottom;
	private int screenWidth;
	private int screenHeight;
	private long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);

		initUI();
	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		tv_top = (TextView) findViewById(R.id.tv_top);
		tv_bottom = (TextView) findViewById(R.id.tv_bottom);
		iv_drag = (ImageView) findViewById(R.id.iv_drag);

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();

		int locaionX = SpUtil.getInt(this, ConstantValue.LOCATION_X, 0);
		int locaionY = SpUtil.getInt(this, ConstantValue.LOCATION_Y, 0);

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_drag
				.getLayoutParams();
		layoutParams.leftMargin = locaionX;
		layoutParams.topMargin = locaionY;
		iv_drag.setLayoutParams(layoutParams);

		if (locaionY > screenHeight / 2) {
			tv_bottom.setVisibility(View.INVISIBLE);
			tv_top.setVisibility(View.VISIBLE);
		} else {
			tv_bottom.setVisibility(View.VISIBLE);
			tv_top.setVisibility(View.INVISIBLE);
		}

		iv_drag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length -1] = SystemClock.uptimeMillis();
				if (mHits[mHits.length - 1] - mHits[0] < 500) {
					int left = screenWidth/2 - iv_drag.getWidth()/2;
					int top = screenHeight/2 - iv_drag.getHeight()/2;
					int right = screenWidth/2 + iv_drag.getWidth()/2;
					int bottom = screenHeight/2 + iv_drag.getHeight()/2;
					
					iv_drag.layout(left, top, right, bottom);
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
				}
			}
		});
		
		iv_drag.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					int dx = endX - startX;
					int dy = endY - startY;

					int left = iv_drag.getLeft() + dx;
					int right = iv_drag.getRight() + dx;
					int top = iv_drag.getTop() + dy;
					int bottom = iv_drag.getBottom() + dy;

					if (left < 0 || right > screenWidth || top < 0
							|| bottom > screenHeight - 22) {
						return true;
					}
					if (top < screenHeight / 2) {
						tv_bottom.setVisibility(View.VISIBLE);
						tv_top.setVisibility(View.INVISIBLE);
					} else {
						tv_bottom.setVisibility(View.INVISIBLE);
						tv_top.setVisibility(View.VISIBLE);
					}

					iv_drag.layout(left, top, right, bottom);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
					break;
				}
				return false;
			}
		});
	}
}
