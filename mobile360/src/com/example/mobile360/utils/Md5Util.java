package com.example.mobile360.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

	public static String encoder(String pwd) {
		try {
			//加盐处理
			pwd = pwd + "mobilesafe";
			//1,指定加密算法类型
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//2,将需要加密的字符串中转换成byte类型的数组,然后进行随机哈希过程
			byte[] bs = digest.digest(pwd.getBytes());
			System.out.println(bs.length);
			//3,循环遍历bs,然后让其生成32位字符串,固定写法
			//4,拼接字符串过程
			StringBuffer buffer = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				System.out.println(hexString);
				if (hexString.length()<2) {
					hexString = "0"+hexString;
				}
				buffer.append(hexString);
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return pwd;
	}
}
