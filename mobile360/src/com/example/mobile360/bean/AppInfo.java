package com.example.mobile360.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {

	//名称,包名,图标,(内存,sd卡),(系统,用户)
	public String name;
	public String packageName;
	public Drawable icon;
	public boolean isSystem;
	public boolean isSdCard;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public boolean isSdCard() {
		return isSdCard;
	}
	public void setSdCard(boolean isSdCard) {
		this.isSdCard = isSdCard;
	}
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packageName=" + packageName
				+ ", icon=" + icon + ", isSystem=" + isSystem + ", isSdCard="
				+ isSdCard + "]";
	}
	
	
}
