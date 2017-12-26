package org.test.model.mytree;

import org.test.model.Article;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeNode;

public class ArticleTreeModel extends DefaultTreeModel {	
	public ArticleTreeModel(TreeNode<Article> root){
		super(root);		
	}	
}
