package org.test.model;

import java.util.Date;

public class Article {
	Integer articleId; //primary key
	Integer parentId;
	Integer userId;
	String title;
	String content;
	Integer TagDetailId;
	Date date;
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getTagDetailId() {
		return TagDetailId;
	}
	public void setTagDetailId(Integer tagDetailId) {
		TagDetailId = tagDetailId;
	}
}
