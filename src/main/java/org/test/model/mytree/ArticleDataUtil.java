package org.test.model.mytree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.test.model.Article;
import org.test.model.service.ArticleService;

public class ArticleDataUtil {
	private static ArticleTreeNode root;
	private static ArticleService as = new ArticleService();
	private List<Article> articleList;
	static{
		buildArticleTree();
	}
	//the method is not efficient but worth to try //selection's  time by hibernate is too much
	//how can we selected hibernate only by one time??
	private static void buildArticleTree2(){
		Article rootArticle = new Article();
		rootArticle.setArticleId(null);
		root = new ArticleTreeNode(rootArticle,new LinkedList<ArticleTreeNode>());
		System.out.println("no of root children "+ root.getChildCount());		
	}
	
	public static ArticleTreeNode getRoot(){
		return root;
	}
	
	public static void buildArticleTree(){
		Article rootArticle = new Article();
		rootArticle.setArticleId(null);
		root = new ArticleTreeNode(rootArticle,new LinkedList());
		//List<Article> appendToSelect= as.getChildrenByParentId(null);
		List<ArticleTreeNode> atnList = new ArrayList<ArticleTreeNode>();
		atnList.add(root);
		for(int i = 0; i<atnList.size();i++){
			ArticleTreeNode thisArticleTreeNode = atnList.get(i);			
			List<Article> thisChildren = as.getChildrenByParentId(thisArticleTreeNode.getData().getArticleId());
			System.out.println(thisChildren.size());
			for(int j = 0; j< thisChildren.size();j++){				
				ArticleTreeNode child = new ArticleTreeNode(thisChildren.get(j),new LinkedList());
				thisArticleTreeNode.add(child);
				atnList.add(child);
			}			
			thisArticleTreeNode.getData().setChildren(thisChildren);
			System.out.println("i = " + i);
		}
	}
//	private List<Article> selectChildren(Article thisArticle){
//		List<Article> children = as.getChildrenByParentId(thisArticle.getArticleId());
//		return children;
//	}
	
}
