package org.test.model.group;

import java.io.Serializable;
import java.util.Comparator;

import org.test.model.Article;
import org.zkoss.zul.GroupComparator;

public class ArticleComparator implements Comparator<Article>, GroupComparator<Article>, Serializable {
	private static final long serialVersionUID = 1L;

	public int compareGroup(Article o1, Article o2) {
	
		Integer result = o1.getRootId()-o2.getRootId();
		return result;
	
	}

	public int compare(Article o1, Article o2) {
		if(o1.getRootId()-o2.getRootId() == 0){
			return 0;
		}else{
			return 1;
		}
	}
}
