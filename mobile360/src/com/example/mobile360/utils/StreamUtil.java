package com.example.mobile360.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	public static String stream2String(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while ((len = is.read()) != -1) {
				bos.write(buffer, 0, len);
			}
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
