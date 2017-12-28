package org.test.model.mytree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.test.model.Article;
import org.test.model.service.ArticleService;
import org.zkoss.zul.TreeNode;

public class ArticleDataUtil {
	final static Logger logger = Logger.getLogger(ArticleDataUtil.class);
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
	public static void addArticleToTree(Article a,ArticleTreeModel treeModel){
		
		ArticleTreeNode parentArticle = (ArticleTreeNode)findArticleInTree(a.getParentId(),(TreeNode)treeModel.getRoot());
		if(parentArticle!=null){
			parentArticle.add(new ArticleTreeNode(a,new LinkedList()));
		}else {
			((TreeNode)treeModel.getRoot()).add(new ArticleTreeNode(a,new LinkedList()));
		}
	}
	
	private static TreeNode<Article> findArticleInTree(Integer parentId,TreeNode<Article> root){
		TreeNode<Article> parentNode = null;
		List<TreeNode<Article>> atnList = new LinkedList();
		atnList.add(root);
		boolean findFlag = false;
		for(int i = 0; i<atnList.size();i++){
			TreeNode<Article> thisArticleTreeNode = atnList.get(i);			
			List<TreeNode<Article>> thisChildren = thisArticleTreeNode.getChildren();
			logger.debug(thisChildren.size());
			for(int j = 0; j< thisChildren.size();j++){				
				if(thisChildren.get(j).getData().getArticleId() == parentId){
					parentNode = thisChildren.get(j);
					findFlag = true;
					break;
				}				
			}			
			if(findFlag == true){
				break;
			}
			atnList.addAll(thisChildren);
		}
		
		return parentNode;
	}
//	private List<Article> selectChildren(Article thisArticle){
//		List<Article> children = as.getChildrenByParentId(thisArticle.getArticleId());
//		return children;
//	}
	
}
