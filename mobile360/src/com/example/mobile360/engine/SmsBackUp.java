package com.example.mobile360.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackUp {
	private static int index = 0;
	
	public static void backup(Context ctx,String path,CallBack callBack) {
		FileOutputStream fos = null;
		Cursor cursor = null;
		try {
			//需要用到的对象上下文环境,备份文件夹路径,进度条所在的对话框对象用于备份过程中进度的更新
			//1,获取备份短信写入的文件
			File file = new File(path);
			//2,获取内容解析器,获取短信数据库中数据
			cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"), 
					new String[]{"address","date","type","body"}, null, null, null);
			//3,文件相应的输出流
			fos = new FileOutputStream(file);
			//4,序列化数据库中读取的数据,放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			//5,给次xml做相应设置
			newSerializer.setOutput(fos, "utf-8");
			//DTD(xml规范)
			newSerializer.startDocument("utf-8", true);
			
			newSerializer.startTag(null, "smss");
			
			//6,备份短信总数指定
			if(callBack!=null){
				callBack.setMax(cursor.getCount());
			}
			
			//7,读取数据库中的每一行的数据写入到xml中
			while(cursor.moveToNext()){
				newSerializer.startTag(null, "sms");
				
				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");
				
				newSerializer.startTag(null, "date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");
				
				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");
				
				newSerializer.startTag(null, "body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null, "body");
				
				newSerializer.endTag(null, "sms");
				
				//8,每循环一次就需要去让进度条叠加
				index++;
				Thread.sleep(500);
				if(callBack!=null){
					callBack.setProgress(index);
				}
			}
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(cursor!=null && fos!=null){
					cursor.close();
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public interface CallBack{
		public void setMax(int max);
		public void setProgress(int index);
	}
}
