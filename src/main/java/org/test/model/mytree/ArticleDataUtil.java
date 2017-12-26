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
	
	public static ArticleTreeNode getRoot(){
		buildArticleTree();
		return root;
	}
	
	public static void buildArticleTree(){
		Article rootArticle = new Article();
		rootArticle.setArticleId(null);
		root = new ArticleTreeNode(rootArticle,new LinkedList());
		List<ArticleTreeNode> atnList = new ArrayList<ArticleTreeNode>();
		atnList.add(root);
		for(int i = 0; i<atnList.size();i++){
			ArticleTreeNode thisArticleTreeNode = atnList.get(i);			
			List<Article> thisChildren = as.getChildrenByParentId(thisArticleTreeNode.getData().getArticleId());
			for(int j = 0; j< thisChildren.size();j++){				
				ArticleTreeNode child = new ArticleTreeNode(thisChildren.get(j),new LinkedList());
				thisArticleTreeNode.add(child);
				atnList.add(child);
			}			
			thisArticleTreeNode.getData().setChildren(thisChildren);
		}
	}
//	private List<Article> selectChildren(Article thisArticle){
//		List<Article> children = as.getChildrenByParentId(thisArticle.getArticleId());
//		return children;
//	}
	
}
