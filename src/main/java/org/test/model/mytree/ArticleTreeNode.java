package org.test.model.mytree;

import java.util.LinkedList;

import org.test.model.Article;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class ArticleTreeNode extends DefaultTreeNode<Article> {
    private static final long serialVersionUID = 1L;
    int count;
 
    public ArticleTreeNode(Article article, int count) {
        super(article, new LinkedList<TreeNode<Article>>()); // assume not a leaf-node
        this.count = count;
    }
 
    public String getDescription() {
        return getData().getDescription();
    }
 
    public int getCount() {
        return count;
    }
 
    public boolean isLeaf() {
        return getData() != null && getData().getChildren().isEmpty();
    }

}
