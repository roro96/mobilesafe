package com.example.mobile360.activity;

import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.db.dao.TrafficDao;
import com.example.mobile360.db.dao.TrafficDao.TrafficInfo;
import com.example.mobile360.service.TrafficService;

public class TrafficActivity extends Activity {
	private TextView tv_count;
	private MyConn mConn;
	private TrafficDao mDao;
	private TrafficService trafficService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);

		Intent intent = new Intent(getApplicationContext(),
				TrafficService.class);
		mConn = new MyConn();
		bindService(intent, mConn, BIND_AUTO_CREATE);

		mDao = new TrafficDao(getApplicationContext());
		
		tv_count = (TextView) findViewById(R.id.tv_count);

	}

	public void click(View v) {
		if (trafficService == null) {
			tv_count.setText("服务未绑定");
		} else {
			trafficService.logRecord();
			Map<String, TrafficInfo> list = mDao.queryTotal();
			StringBuilder sb = new StringBuilder();
			for (TrafficInfo info : list.values()) {
				sb.append(info.appName + " - 流量信息:\r\n");
				sb.append(
						"移动网络接收的流量"
								+ Formatter.formatFileSize(TrafficActivity.this,
										info.mobileRx)).append("\r\n");
				sb.append(
						"移动网络发送的流量"
								+ Formatter.formatFileSize(TrafficActivity.this,
										info.mobileTx)).append("\r\n");
				sb.append(
						"WIFI接收的流量"
								+ Formatter.formatFileSize(TrafficActivity.this,
										info.wifiRx)).append("\r\n");
				sb.append(
						"WIFI发送的流量"
								+ Formatter.formatFileSize(TrafficActivity.this,
										info.wifiTx)).append("\r\n");
				sb.append("--------------------").append("\r\n");
				tv_count.setText(sb);
			}
		}
	}

	/**
	 * 用来监听服务的状态
	 * 
	 * @author xxx
	 * 
	 */
	private class MyConn implements ServiceConnection {

		/*
		 * 连接成功后调用
		 * 
		 * @see
		 * android.content.ServiceConnection#onServiceConnected(android.content
		 * .ComponentName, android.os.IBinder)
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			trafficService = ((TrafficService.MyBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			trafficService = null;
		}
	}

	@Override
	protected void onDestroy() {
		unbindService(mConn);
		super.onDestroy();
	}
}
