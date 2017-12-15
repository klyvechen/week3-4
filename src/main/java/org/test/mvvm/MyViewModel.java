package org.test.mvvm;

import java.util.List;

import org.test.model.Article;
import org.test.model.PackageData;
import org.test.model.PackageDataUtil;
import org.test.model.service.ArticleService;
import org.test.model.service.TagDetailService;
import org.test.model.service.TagService;
import org.test.model.service.UserService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class MyViewModel {
	List<Article> articleWritenByUser;
	List<Article> lastest10Article;
	List<Article> lastest10RepliedArticle;
	List<Article> lastest10ArticleTaggedUser;
	
	UserService us = new UserService();
	ArticleService as = new ArticleService();
	TagService ts = new TagService();
	TagDetailService tds = new  TagDetailService();
	
	public void insertArticle(Article article){
		
		
		as.insertNewArticle(article);
	}
	
	public List<Article> getArticleWritenbyUser(){
		
		return null;
	}
	
	public TreeModel<TreeNode<PackageData>> getTreeModel() {
        return new DefaultTreeModel<PackageData>(PackageDataUtil.getRoot());
    }
	
	

	private int count;

	@Init
	public void init() {
		count = 100;
	}

	@Command
	@NotifyChange("count")
	public void cmd() {
		++count;
	}

	public int getCount() {
		return count;
	}
}
