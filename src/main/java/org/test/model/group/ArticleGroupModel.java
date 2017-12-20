package org.test.model.group;

import java.util.Comparator;

import org.test.model.Article;
import org.zkoss.zul.GroupsModelArray;

public class ArticleGroupModel extends GroupsModelArray<Article,ArticleGroupModel.ArticleGroupInfo,Object, Object > {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ArticleGroupModel(Article[] data, Comparator<Article> cmpr) {
		super(data, cmpr);
		
	}
	
	protected ArticleGroupInfo createGroupHead(Article[] data, int index, int col){
		return new ArticleGroupInfo(data[0], index, col);
	}
	
	protected Object createGroupFoot(Article[] data, int index, int col){
		return data.length;
	}
	

	public static class ArticleGroupInfo{
		private Article firstChild;
		private int groupIndex;
		private int colIndex;
		
		public ArticleGroupInfo(Article firstChild, int groupIndex, int colIndex){
			super();
			this.firstChild = firstChild;
			this.groupIndex = groupIndex;
			this.colIndex = colIndex;
		}
		public Article getFirstChild(){
			return this.firstChild;
		}
		public int getGroupIndex(){
			return this.groupIndex;
		}
		public int getColIndex(){
			return this.colIndex;
		}
	}
}
