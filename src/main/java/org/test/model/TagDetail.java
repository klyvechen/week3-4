package org.test.model;

import java.io.Serializable;

public class TagDetail implements Serializable {
	private Integer tagId;
	private Integer articleId;

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public Integer getArticleId() {
		return articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
}
