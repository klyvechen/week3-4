package org.test.model.group;

import java.util.Comparator;
import java.util.List;

import org.test.model.Article;
import org.zkoss.zul.GroupsModelArray;

public class ArticleGroupingModel extends GroupsModelArray<Article, String, String, Object> {
	private static final long serialVersionUID = 1L;
	private static final String footerString = "";
	private boolean showGroup;

	public ArticleGroupingModel(List<Article> data, Comparator<Article> cmpr, boolean showGroup) {
		super(data.toArray(new Article[0]), cmpr);
		this.showGroup = showGroup;
	}

	protected String createGroupHead(Article[] groupdata, int index, int col) {
		String ret = "";
		if (groupdata.length > 0) {
			ret = groupdata[0].getTitle();
		}
		return ret;
	}
}
