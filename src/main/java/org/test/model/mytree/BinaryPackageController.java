package org.test.model.mytree;

import org.test.model.Article;
import org.test.model.service.ArticleService;
import org.test.model.tree.PackageDataUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class BinaryPackageController extends SelectorComposer<Component> {
	 
    private static final long serialVersionUID = 43014628867656917L;
    private ArticleService as = new ArticleService();  
    
    
    public TreeModel<TreeNode<Article>> getTreeModel() {
    	System.out.println("bpc return tree node");
        return new DefaultTreeModel<Article>(ArticleDataUtil.root);
    }
}