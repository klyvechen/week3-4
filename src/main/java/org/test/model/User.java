package org.test.model;

import java.io.Serializable;

public class User implements Serializable {
	private Integer userid = 0; // primary key
	private String username;
	private String password;

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userId) {
		this.userid = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
