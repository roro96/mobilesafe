package com.example.mobile360.bean;

public class ContactInfo {

	private String id;
	private String phone;
	private String name;

	@Override
	public String toString() {
		return "ContactInfo [id=" + id + ", phone=" + phone + ", name=" + name
				+ "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
