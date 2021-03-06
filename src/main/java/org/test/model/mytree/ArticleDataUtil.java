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
	private static ArticleService as = new ArticleService();
	private List<Article> articleList;

	public ArticleTreeNode getRoot() {
		return buildArticleTree();
	}

	public ArticleTreeNode buildArticleTree() {
		Article rootArticle = new Article();
		rootArticle.setArticleId(null);
		ArticleTreeNode root = new ArticleTreeNode(rootArticle, new LinkedList());
		List<ArticleTreeNode> atnList = new ArrayList<ArticleTreeNode>();
		atnList.add(root);
		for (int i = 0; i < atnList.size(); i++) {
			ArticleTreeNode thisArticleTreeNode = atnList.get(i);
			List<Article> thisChildren = as.getChildrenByParentIdAsc(thisArticleTreeNode.getData().getArticleId());
			for (int j = 0; j < thisChildren.size(); j++) {
				ArticleTreeNode child = new ArticleTreeNode(thisChildren.get(j), new LinkedList());
				thisArticleTreeNode.add(child);
				atnList.add(child);
			}
			thisArticleTreeNode.getData().setChildren(thisChildren);
		}
		return root;
	}

	public static void addArticleToTree(Article a, ArticleTreeModel treeModel) {

		ArticleTreeNode parentArticle = (ArticleTreeNode) findArticleInTree(a.getParentId(),
				(TreeNode) treeModel.getRoot());
		if (parentArticle != null) {
			parentArticle.add(new ArticleTreeNode(a, new LinkedList()));
		} else {

			((TreeNode) treeModel.getRoot()).add(new ArticleTreeNode(a, new LinkedList()));
		}
	}

	public static void removeArticleFromTree(Integer removeId, TreeNode<Article> root) {
		TreeNode<Article> parentNode = null;
		List<TreeNode<Article>> atnList = new LinkedList();
		atnList.add(root);
		boolean findFlag = false;
		for (int i = 0; i < atnList.size(); i++) {
			TreeNode<Article> thisArticleTreeNode = atnList.get(i);
			List<TreeNode<Article>> thisChildren = thisArticleTreeNode.getChildren();
			for (int j = 0; j < thisChildren.size(); j++) {
				if (thisChildren.get(j).getData().getArticleId() == removeId) {
					thisArticleTreeNode.remove(j);
					findFlag = true;
					break;
				}
			}
			if (findFlag == true) {
				break;
			}
			atnList.addAll(thisChildren);
		}
	}

	public static void editArticleFromTree(Article a, TreeNode<Article> root) {
		TreeNode<Article> parentNode = null;
		List<TreeNode<Article>> atnList = new LinkedList();
		atnList.add(root);
		boolean findFlag = false;
		for (int i = 0; i < atnList.size(); i++) {
			TreeNode<Article> thisArticleTreeNode = atnList.get(i);
			List<TreeNode<Article>> thisChildren = thisArticleTreeNode.getChildren();
			for (int j = 0; j < thisChildren.size(); j++) {
				if (thisChildren.get(j).getData().getArticleId() == a.getArticleId()) {
					thisChildren.get(j).getData().setContent(a.getContent());
					thisChildren.get(j).getData().setTitle(a.getTitle());
					findFlag = true;
					break;
				}
			}
			if (findFlag == true) {
				break;
			}
			atnList.addAll(thisChildren);
		}
	}

	private static TreeNode<Article> findArticleInTree(Integer parentId, TreeNode<Article> root) {
		TreeNode<Article> parentNode = null;
		List<TreeNode<Article>> atnList = new LinkedList();
		atnList.add(root);
		boolean findFlag = false;
		for (int i = 0; i < atnList.size(); i++) {
			TreeNode<Article> thisArticleTreeNode = atnList.get(i);
			List<TreeNode<Article>> thisChildren = thisArticleTreeNode.getChildren();
			logger.debug(thisChildren.size());
			for (int j = 0; j < thisChildren.size(); j++) {
				if (thisChildren.get(j).getData().getArticleId() == parentId) {
					parentNode = thisChildren.get(j);
					findFlag = true;
					break;
				}
			}
			if (findFlag == true) {
				break;
			}
			atnList.addAll(thisChildren);
		}
		return parentNode;
	}

	public static void setArticleAasB(Article a, Article b) {
		a.setArticleId(b.getArticleId());
		a.setContent(b.getContent());
		a.setDate(b.getDate());
		a.setGeneration(b.getGeneration());
		a.setParentId(b.getParentId());
		a.setRootId(b.getRootId());
		a.setTime(b.getTime());
		a.setTitle(b.getTitle());
		a.setUserId(b.getUserId());
	}

}
