package org.test.model;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class BinaryPackageController extends SelectorComposer<Component> {
	 
    private static final long serialVersionUID = 43014628867656917L;
     
    public TreeModel<TreeNode<PackageData>> getTreeModel() {
    	System.out.println("bpc return tree node");
        return new DefaultTreeModel<PackageData>(new PackageDataUtil().getRoot());
    }
}