package org.test.mvvm;

import org.test.model.Article;
import org.test.model.User;
import org.test.model.group.ArticleGroupModel;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ArticleListGroupRender implements ListitemRenderer<Object> {
	public void render(Listitem listitem, Object obj, int index) throws Exception {
		User theUser = (User) Sessions.getCurrent().getAttribute("sessionUser");
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
			cell.setSpan(4);
			cell.appendChild(new Label("Total " + obj + " Items"));
			listitem.appendChild(cell);
		} else {
			Article data = (Article) obj;
			listitem.setClientAttribute("rootId", String.valueOf(data.getRootId()));
			listitem.setClientAttribute("parentId", String.valueOf(data.getParentId()));
			listitem.setClientAttribute("articleId", String.valueOf(data.getArticleId()));
			listitem.appendChild(new Listcell(data.getTitle()));
			listitem.appendChild(new Listcell(data.getContent()));
			if (theUser != null) {
				Listcell cell1 = new Listcell();
				Button btn1 = new Button();
				btn1.setLabel("Reply");				
				btn1.setPopup("replyEditor");
				btn1.setAttribute("theData",data);
				btn1.addEventListener("onClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						sessionAddAttribute(event);	
					}
				});

				cell1.appendChild(btn1);
				listitem.appendChild(cell1);

				if (theUser.getUserid() == data.getUserId()) {
					Listcell cell2 = new Listcell();
					Button btn2 = new Button();
					btn2.setLabel("Edit");
					btn2.setAttribute("theData",data);
					btn2.addEventListener("onClick", new EventListener() {
						public void onEvent(Event event) throws Exception {
							sessionAddAttribute(event);						
						}
					});
					cell2.appendChild(btn2);
					listitem.appendChild(cell2);
					if(data.getParentId()!=null){
						Listcell cell3 = new Listcell();
						Button btn3 = new Button();
						btn3.setLabel("Delete");
						btn3.setAttribute("theData",data);
						btn3.addEventListener("onClick", new EventListener() {
							public void onEvent(Event event) throws Exception {
								sessionAddAttribute(event);						
							}
						});
						cell3.appendChild(btn3);
						listitem.appendChild(cell3);
					}
				}
			}
			listitem.setValue(data);
		}

	}
	private void sessionAddAttribute(Event e){
		Sessions.getCurrent().setAttribute("Action",((Button)e.getTarget()).getLabel());
		Sessions.getCurrent().setAttribute("theArticle", e.getTarget().getAttribute("theData"));
	}
}
