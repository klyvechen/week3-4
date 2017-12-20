package org.test.model.group;

import java.io.Serializable;
import java.util.Comparator;

import org.test.model.Article;
import org.zkoss.zul.GroupComparator;

public class ArticleComparator implements Comparator<Article>, GroupComparator<Article>, Serializable {
	private static final long serialVersionUID = 1L;

	public int compareGroup(Article o1, Article o2) {
	
		Integer result = o1.getRootId()-o2.getRootId();
		System.out.println(result);
		return result;
	
	}

	public int compare(Article o1, Article o2) {
		if(o1.getRootId()-o2.getRootId() == 0){
			System.out.println(o1+" , "+o2);
			System.out.println(o1.getRootId()+" , "+o2.getRootId());
			System.out.println("return 0");
			return 0;
		}else{
			System.out.println("return 1");
			return 1;
		}
	}
}
