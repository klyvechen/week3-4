package org.test.mvvm;

import java.util.List;

import org.test.hibernate.util.HibernateUtil;
import org.test.model.Article;
import org.test.model.group.ArticleComparator;
import org.test.model.group.ArticleGroupModel;
import org.test.model.service.ArticleService;
import org.test.model.service.TagDetailService;
import org.test.model.service.TagService;
import org.test.model.service.UserService;
import org.test.model.tree.PackageData;
import org.test.model.tree.PackageDataUtil;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class MyViewModel{
	private List<Article> lastest10UserArticle;
	private List<Article> lastest10Article;
	private List<Article> lastest10Reply;
	private ArticleGroupModel groupModel;
	
	UserService us = new UserService();
	ArticleService as;
	TagService ts = new TagService();
	TagDetailService tds = new  TagDetailService();
	
    @Init
    public void init() {
    	HibernateUtil.getSessionFactory();
    	as = new ArticleService();
    	List<Article> atary = as.getAllArticles();
    	Article[] ary = new Article[atary.size()];
    	this.groupModel = new ArticleGroupModel(ary, new ArticleComparator());
    	//this.lastest10Article = as.getLastest10Article();    	        
    }
	
    /****start to get datas***/
	public List<Article> getLastest10Article(){
		return as.getLastest10Article();
	}
	
	public List<Article> getLastest10Reply(){
		return as.getLastest10Reply();
	}
	
	public List<Article> getLastest10UserArticle(){
		
		return as.getLastest10UserArticle(3);
	}
	
	public ArticleGroupModel getGroupModel(){
		return this.groupModel;
	}
	
	
	
	
	
	/***start to insert update data***/
	
	public void insertArticle(Article article){	
		as.insertNewArticle(article);
	}
	
	
	public TreeModel<TreeNode<PackageData>> getTreeModel() {
        return new DefaultTreeModel<PackageData>(PackageDataUtil.getRoot());
    }
	
}
