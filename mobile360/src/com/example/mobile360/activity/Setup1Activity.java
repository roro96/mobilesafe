package com.example.mobile360.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mobile360.R;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	protected void showNextPage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.anim_in_after, R.anim.anim_out_after);
	}

	@Override
	protected void showPrePage() {

	}
}
