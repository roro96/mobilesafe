package com.example.mobile360.test;

import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.example.mobile360.bean.BlackNumberInfo;
import com.example.mobile360.db.dao.BlackNumberDao;

public class Test extends AndroidTestCase{

	public void insert(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		for (int i = 0; i < 100; i++) {
			if (i<10) {
				dao.insert("1234567890" + i, 1 + new Random().nextInt(3) + "");
			}else {
				dao.insert("123456789" + i, 1 + new Random().nextInt(3) + "");
			}
		}
	}
	
	public void delete(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.delete("110");
	}
	public void update(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.update("110", "2");
	}
	
	public void findAll(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		List<BlackNumberInfo> blackNumberInfoList = dao.findAll();
	}
}
