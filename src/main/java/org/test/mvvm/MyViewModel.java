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
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class MyViewModel{
	private List<Article> lastest10UserArticle;
	private List<Article> lastest10Article;
	private List<Article> lastest10Reply;
	private ArticleGroupModel groupModel;
	private Article temparticle= new Article();
	private String tempTitle = "default title";
	private String tempContent = "default content";

	private int index = 5;
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	
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
    	ary = atary.toArray(ary);
    	this.groupModel = new ArticleGroupModel(ary, new ArticleComparator());
    	this.temparticle = new Article();
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
	
	public Article getTemparticle() {
		return temparticle;
	}

	public void setTemparticle(Article tempArticle) {
		this.temparticle = tempArticle;
	}

	
	@Command
	@NotifyChange("tempArticle")
	public void newArticle() throws CloneNotSupportedException{
		System.out.println("notify show result");
		Article newArticle = this.temparticle.clone();
		
		
	}

	@Command
	public void save(){
	
	}
	
	
	@Command("selectGroup")
    public void selectGroup(@BindingParam("data") Object data) {
        if(data instanceof ArticleGroupModel.ArticleGroupInfo) {
            ArticleGroupModel.ArticleGroupInfo groupInfo = (ArticleGroupModel.ArticleGroupInfo)data;
            int groupIndex = groupInfo.getGroupIndex() ;
            int childCount = groupModel.getChildCount(groupIndex);
            boolean added = groupModel.isSelected(groupInfo);
            for(int childIndex = 0; childIndex < childCount; childIndex++) {
                Article article = groupModel.getChild(groupIndex, childIndex);
                if(added) {
                    groupModel.addToSelection(article);
                } else {
                    groupModel.removeFromSelection(article);
                }
            }
        }
    }
	 
	
	
	
	
	/***start to insert update data***/
	
	public void insertArticle(Article article){	
		as.insertNewArticle(article);
	}
	
	
	public TreeModel<TreeNode<PackageData>> getTreeModel() {
        return new DefaultTreeModel<PackageData>(PackageDataUtil.getRoot());
    }
	
}
