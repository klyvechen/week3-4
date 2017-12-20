package org.test.mvvm;

import org.test.model.Article;
import org.test.model.group.ArticleGroupModel;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ArticleListGroupRender implements ListitemRenderer<Object>{
		public void render(Listitem listitem, Object obj, int index) throws Exception {
	 
	        if (listitem instanceof Listgroup) {        	
	            ArticleGroupModel.ArticleGroupInfo groupInfo = (ArticleGroupModel.ArticleGroupInfo) obj;
	            Article article = groupInfo.getFirstChild();
	            String groupTxt;
	            switch (groupInfo.getColIndex()) {
	            case 0:
	                groupTxt = article.getTitle();
	                break;
	            case 1:
	                groupTxt = article.getContent();
	                break;	            
	            default:
	                groupTxt = article.getTitle();
	            }
	            listitem.appendChild(new Listcell(groupTxt));
	            listitem.setValue(obj);
	        } else if (listitem instanceof Listgroupfoot) {
	            Listcell cell = new Listcell();
	            cell.setSclass("foodFooter");
	            cell.setSpan(2);
	            cell.appendChild(new Label("Total " + obj + " Items"));
	            listitem.appendChild(cell);
	        } else {
	        	Article data = (Article) obj;
	            listitem.appendChild(new Listcell(data.getTitle()));
	            listitem.appendChild(new Listcell(data.getContent()));	          
	            listitem.setValue(data);
	        }
	 
	    }
}
