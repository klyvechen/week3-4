package org.test.model.mytree;

import java.util.LinkedList;
import java.util.List;

import org.test.model.Article;
import org.test.model.service.ArticleService;
import org.zkoss.zul.DefaultTreeNode;

public class ArticleTreeNode extends DefaultTreeNode<Article> {
    private static final long serialVersionUID = 1L;
    private ArticleService as = new ArticleService();
    
    public ArticleTreeNode(Article article, List<ArticleTreeNode> nulllist) {
    	super(article, new LinkedList<ArticleTreeNode>()); // assume not a leaf-node
    }
    
    public ArticleTreeNode(Article article){
    	super(article);
    }
    
    public ArticleTreeNode(Article article, List<ArticleTreeNode>children, boolean buildBySelf){    	
    	super(article, children);
    }
    
    public String getTitle() {
        return getData().getTitle();
    }


}
