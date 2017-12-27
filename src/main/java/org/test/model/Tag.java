package org.test.model;

import java.io.Serializable;

public class Tag implements Serializable{
	Integer tagId;
	String tagContent;
	boolean isChecked = true;
	public boolean getIsChecked() {
		return isChecked;
	}
	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public Integer getTagId() {
		return tagId;
	}
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	public String getTagContent() {
		return tagContent;
	}
	public void setTagContent(String tagContent) {
		this.tagContent = tagContent;
	}
	public String toString(){
		return this.tagContent;
	}
}
