package com.example.newapp.entity;

import java.io.Serializable;

public class User implements Serializable {
	
	private static final long serialVersionUID = 4058651010420214072L;
	private String id;
	private String nickname;// 用户名
	private String phoneNum;// 手机电话

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}


}
