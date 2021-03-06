package org.test.model.mytree;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.test.model.Article;
import org.test.model.service.ArticleService;
import org.test.model.service.UserService;
import org.test.mvvm.LoginViewModel;
import org.zkoss.zul.DefaultTreeNode;

public class ArticleTreeNode extends DefaultTreeNode<Article> {
	final static Logger logger = Logger.getLogger(LoginViewModel.class);
	private static final long serialVersionUID = 1L;
	private ArticleService as = new ArticleService();
	private UserService us = new UserService();
	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");

	public ArticleTreeNode(Article article, List<ArticleTreeNode> nulllist) {
		super(article, new LinkedList<ArticleTreeNode>()); // assume not a
															// leaf-node
	}

	public ArticleTreeNode(Article article) {
		super(article);
	}

	public ArticleTreeNode(Article article, List<ArticleTreeNode> children, boolean buildBySelf) {
		super(article, children);
	}

	public String getTitle() {
		return getData().getTitle();
	}

	public String getDate() {
		if (getData().getDate() == null)
			getData().setDate(new Date(Calendar.getInstance().getTime().getTime()));
		return sdFormat.format(getData().getDate());
	}

	public String getUsername() {
		return getData().getUsername();
	}

}
