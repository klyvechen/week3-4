package org.test.mvvm;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.Article;
import org.test.model.Tag;
import org.test.model.TagDetail;
import org.test.model.User;
import org.test.model.group.ArticleComparator;
import org.test.model.group.ArticleGroupModel;
import org.test.model.mytree.ArticleDataUtil;
import org.test.model.mytree.ArticleTreeModel;
import org.test.model.mytree.ArticleTreeNode;
import org.test.model.service.ArticleService;
import org.test.model.service.TagDetailService;
import org.test.model.service.TagService;
import org.test.model.service.UserService;
import org.test.myevent.SampleExecutorHolder;
import org.test.thread.insertAfter5Sec;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.TreeNode;

public class MyViewModel {
	final static Logger logger = Logger.getLogger(MyViewModel.class);
	private org.zkoss.zk.ui.event.EventQueue<Event> insertAfter5 = EventQueues.lookup("insertAfter5",
			EventQueues.APPLICATION, true);

	private Desktop desktop = Executions.getCurrent().getDesktop();
	private List<Article> lastest10UserArticle, lastest10Article, lastest10Reply, allArticles,listModel;
	private List<Tag> allTagList;
	private List<Tag> selectedTagList;
	private ArticleGroupModel groupModel;
	private ArticleTreeModel myArticleTreeModel;
	private ArticleTreeNode pickedTreeItem;
	private ArticleTreeNode rootArticle;
	private Article pickedListItem;
	private Article pickedArticle;
	private Article tempArticle = new Article();
	private User theUser;
	private Session sess;
	private ExecutorService executorService;
	private boolean undoFlag = false;
	private Popup popup1, popup2;
	private Hlayout checkboxlist;
	private String selectedArticleContent = "please select content";
	private String action;
	Button undo;
	Popup ckarticleEditor;
	Popup waitFor10Sec;
	private UserService us = new UserService();
	private ArticleService as = new ArticleService();
	private TagService ts = new TagService();
	private TagDetailService tds = new TagDetailService();
	private ListModelList chosenboxModel = new ListModelList<Tag>(ts.getAllTag());
	private Set<Tag> selectedTag;
	private Chosenbox chosenbox;
	private Set pickedChosenboxItem;
	private boolean author, mainArticle;

	@Init
	public void init() {
		// SampleExecutorHolder seh = new SampleExecutorHolder();
		SampleExecutorHolder seh = new SampleExecutorHolder();
		sess = Sessions.getCurrent();
		theUser = (User) sess.getAttribute("sessionUser");
		if (theUser == null || theUser.getUserid() == null) {
			Executions.sendRedirect("/login.zul");
		} else {
			logger.warn(theUser);
			HibernateUtil.getSessionFactory();
			List<Article> atary = as.getAllArticles();
			Article[] ary = new Article[atary.size()];
			ary = atary.toArray(ary);
			this.groupModel = new ArticleGroupModel(ary, new ArticleComparator());
			this.setLastest10Article(as.getLastest10Article());
			this.setLastest10Reply(as.getLastest10Reply());
			this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
			this.setAllArticles(as.getAllArticles());
			this.setAllTagList(ts.getAllTag());
			executorService = SampleExecutorHolder.getExecutor();
			this.insertAfter5.subscribe(new EventListener() {
				public void onEvent(Event evt) {
					logger.debug(evt.getName());
					if (evt.getData() == getSess()) {
						insertArticle();
					}
					notifiyToAll();
				}
			});
			this.rootArticle = ArticleDataUtil.getRoot();
			this.myArticleTreeModel = new ArticleTreeModel(this.rootArticle);
			this.setPickedTreeItem(this.rootArticle);
		}
	}

	public Article getPickedListItem() {
		return pickedListItem;
	}

	public void setPickedListItem(Article pickedListItem) {
		this.pickedListItem = pickedListItem;
		this.setPickedArticle(this.pickedListItem);
	}
	
	
	public ArticleTreeNode getPickedTreeItem() {
		return pickedTreeItem;
	}

	public void setPickedTreeItem(ArticleTreeNode pickedTreeItem) {
		logger.debug("the pickedTreeItem is " + pickedTreeItem);
		logger.debug("in the setPickedArticle");
		this.pickedTreeItem = pickedTreeItem;
		this.setPickedArticle(this.pickedTreeItem.getData());		 
	}
	
	public Article getPickedArticle() {
		return pickedArticle;
	}
	
	@NotifyChange({"tempArticle","author","mainArticle","listModel","selectedTagList"})
	public void setPickedArticle(Article pickedArticle) {
		logger.debug("in the setPickedArticle");
		this.pickedArticle = pickedArticle;
		this.tempArticle.setArticleId(this.pickedArticle.getArticleId());
		this.tempArticle.setTitle(this.pickedArticle.getTitle());
		this.tempArticle.setContent(this.pickedArticle.getContent());
		this.author =  (this.getPickedArticle() != null)
				? (this.getPickedArticle().getUserId() == theUser.getUserid()) : false;
		this.mainArticle = (this.pickedArticle != null)?(this.pickedArticle.getParentId() == null):false;
		this.listModel = as.getArticleDFSResult(pickedArticle);
		this.selectedTagList = ts.getTagsByArticleId(this.pickedArticle.getArticleId());
		logger.debug(this.listModel.size());
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "author");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "mainArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "listModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "selectedTagList");
	}
	

	public List<Article> getListModel() {
		return listModel;
	}

	public void setListModel(List<Article> listModel) {
		this.listModel = listModel;
	}

	public boolean isMainArticle() {
		return mainArticle;
	}

	public void setMainArticle(boolean mainArticle) {
		this.mainArticle = mainArticle;
	}

	/**** start to get datas ***/
	public Session getSess() {
		return sess;
	}

	public void setSess(Session sess) {
		this.sess = sess;
	}

	public ListModelList getChosenboxModel() {
		return chosenboxModel;
	}

	public void setChosenboxModel(ListModelList chosenboxModel) {
		this.chosenboxModel = chosenboxModel;
	}

	public Set getPickedChosenboxItem() {
		logger.debug("get the pickedTreeItem is " + pickedTreeItem);
		return pickedChosenboxItem;
	}

	public void setPickedChosenboxItem(Set pickedChosenboxItem) {
		logger.debug("set the pickedTreeItem is " + pickedTreeItem);
		this.pickedChosenboxItem = pickedChosenboxItem;
	}
	public List<Tag> getSelectedTagList() {
		return selectedTagList;
	}

	public void setSelectedTagList(List<Tag> selectedTagList) {
		this.selectedTagList = selectedTagList;
	}
	public Chosenbox getChosenbox() {
		return chosenbox;
	}

	public void setChosenbox(Chosenbox chosenbox) {
		this.chosenbox = chosenbox;
	}

	public String getSelectedArticleContent() {
		return selectedArticleContent;
	}

	public void setSelectedArticleContent(String selectedArticleContent) {
		this.selectedArticleContent = selectedArticleContent;
	}

	public ArticleTreeModel getMyArticleTreeModel() {
		return myArticleTreeModel;
	}

	public void setMyArticleTreeModel(ArticleTreeModel myArticleTreeModel) {
		this.myArticleTreeModel = myArticleTreeModel;
	}

	public List<Article> getAllArticles() {
		return allArticles;
	}

	public void setAllArticles(List<Article> allArticles) {
		this.allArticles = allArticles;
	}

	public List<Tag> getAllTagList() {
		return allTagList;
	}

	public void setAllTagList(List<Tag> allTagList) {
		this.allTagList = allTagList;
	}

	@NotifyChange({ "lastest10Article" })
	public void setLastest10Article(List<Article> lastest10Article) {
		System.out.print("do lastest10Article");
		this.lastest10Article = lastest10Article;
	}

	public List<Article> getLastest10Article() {
		return this.lastest10Article;
	}

	@NotifyChange({ "lastest10Reply" })
	public void setLastest10Reply(List<Article> lastest10Article) {
		System.out.print("do lastest10Reply");
		this.lastest10Reply = lastest10Article;
	}

	public List<Article> getLastest10Reply() {
		return this.lastest10Reply;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@NotifyChange
	public void setLastest10UserArticle(List<Article> articles) {
		System.out.print("do lastest10UserArticle");
		this.lastest10UserArticle = articles;
	}

	public List<Article> getLastest10UserArticle() {
		return this.lastest10UserArticle;
	}

	public ArticleGroupModel getGroupModel() {
		return this.groupModel;
	}

	public Article getTempArticle() {
		return tempArticle;
	}

	public void setTempArticle(Article tempArticle) {
		this.tempArticle = tempArticle;
	}

	@NotifyChange({ "groupModel" })
	public void setGroupModel(ArticleGroupModel agm) {
		this.groupModel = agm;
	}

	@Command
	public void logout() {
		sess.setAttribute("sessionUser", null);
		Executions.sendRedirect("/login.zul");
	}

	/*** start to insert update data ***/

	@Command
	public void savePopupComp(@BindingParam("popup1") Popup popup1, @BindingParam("popup2") Popup popup2,
			@BindingParam("chbox") Chosenbox chosenbox, @BindingParam("action") Integer action) {
		this.popup1 = popup1;
		this.popup2 = popup2;
		this.chosenbox = chosenbox;
		System.out.println(this.pickedTreeItem);
		if (action == 0) {
			newArticle();
		} else if (action == 1) {
			setChosenboxSelectItem();
			reply();
		} else if (action == 2) {
			setChosenboxSelectItem();
			edit();
		}
	}

	public void newArticle() {
		logger.debug("new Article dosomething");
		this.action = "insert";
		this.tempArticle.setParentId(null);
		this.tempArticle.setRootId(null);
		this.tempArticle.setUserId(this.theUser.getUserid());
	}

	@NotifyChange({ "tempArticle" })
	public void reply() {

		this.action = "insert";
		this.tempArticle.setTitle("Re:" + this.pickedArticle.getTitle());
		this.tempArticle.setContent("");
		this.tempArticle.setParentId(this.pickedArticle.getArticleId());
		this.tempArticle.setRootId(this.pickedArticle.getRootId());
		this.tempArticle.setUserId(this.theUser.getUserid());
		
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
	}

	@NotifyChange({ "tempArticle" })
	public void edit() {
		if (this.pickedArticle.getParentId() != null) {
			logger.warn(this.pickedArticle.getContent());
			System.out.println(this.pickedArticle.getContent());
			this.action = "update";
			this.tempArticle.setArticleId(this.pickedArticle.getArticleId());
			this.tempArticle.setTitle(this.pickedArticle.getTitle());
			this.tempArticle.setContent(this.pickedArticle.getContent());
			this.tempArticle.setParentId(this.pickedArticle.getParentId());
			this.tempArticle.setRootId(this.pickedArticle.getRootId());
			this.tempArticle.setUserId(this.pickedArticle.getUserId());			

		} else {
			Messagebox.show("only reply can be edited");
			popup1.close();
		}
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
	}
	
	private void setChosenboxSelectItem(){
		List<Tag> chosneboxSelectedList = new LinkedList<Tag>();
		for (int i = 0; i < chosenboxModel.size(); i++) {
			for (int j = 0; j < selectedTagList.size(); j++) {
				if (((Tag) chosenboxModel.get(i)).getTagId() == selectedTagList.get(j).getTagId()) {
					chosneboxSelectedList.add((Tag) chosenboxModel.get(i));
					break;
				}
			}
		}
		chosenbox.setSelectedObjects(chosneboxSelectedList);
	}



	@Command
	public void delete() {
		as.deleteArticleAndChildren(this.pickedArticle);
		resetTempArticle();
		notifiyToAll();
	}

	@Command
	public void confirmArticle() {
		logger.debug(chosenbox.getSelectedObjects().size());
		logger.warn(chosenbox.getSelectedObjects().size());
		this.executorService.execute(new insertAfter5Sec(this.insertAfter5, this.desktop, this.sess));
	}

	public boolean getAuthor() {
		boolean isAuthor = (this.pickedArticle != null)
				? (this.pickedArticle.getUserId() == theUser.getUserid()) : false;
		logger.debug(isAuthor);
		System.out.println(isAuthor);
		return this.author;
	}
	public void setAuthor( boolean author) {
	//	this.author = (this.getPickedTreeItem() != null)
	//			? (this.getPickedTreeItem().getData().getUserId() == theUser.getUserid()) : false;
	}

	private void insertArticle() {
		if (this.undoFlag == false) {
			this.selectedTag = this.chosenbox.getSelectedObjects();
			dealArticle();
			this.popup2.close();
			this.popup1.close();
		} else {
			this.undoFlag = false;
		}
	}

	@Command
	public void changeUndoFlag() {
		logger.debug("undo inser article");
		this.undoFlag = true;
		this.popup2.close();
	}

	public void dealArticle() {
		logger.debug("start to deal article");
		Set<Tag> tags = this.selectedTag;
		if (this.action.equals("insert")) {
			as.insertNewArticle(this.tempArticle);
		} else if (this.action.equals("update")) {
			as.updateArticle(this.tempArticle);
			tds.deleteTagDetailByArticleId(this.tempArticle.getArticleId());
		}
		TagDetail td = new TagDetail();
		td.setArticleId(tempArticle.getArticleId());
		for (Iterator itr = tags.iterator(); itr.hasNext();) {
			td.setTagId(((Tag) itr.next()).getTagId());
			tds.insertTagDetail(td);
		}
	}

	// @NotifyChange({"lastest10UserArticle","lastest10Article","lastest10Replay","groupModel"})
	// not working in zk eventQueue
	// https://stackoverflow.com/questions/18382760/zk-eventqueue-working-but-data-not-refreshing
	@NotifyChange({"tempArticle","author","mainArticle","listModel","selectedTagList","myArticleTreeModel","groupModel","lastest10Reply", "lastest10Article","lastest10UserArticle","allArticles"})
	public void notifiyToAll() {
		logger.debug("in the que event listener");
		this.renewGroupModel();
		this.renewTreeModel();
		this.renewListModel();
		this.setLastest10Article(as.getLastest10Article());
		this.setLastest10Reply(as.getLastest10Reply());
		this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
		//
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10UserArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Article");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Reply");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "groupModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "myArticleTreeModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "author");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "mainArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "listModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "selectedTagList");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "allArticles");
	}

	public void renewTreeModel(){
		Article a = getThisArticle();		
		ArticleDataUtil.addArticleToTree(a,myArticleTreeModel);
	}
	
	public void renewListModel(){
		Article a = getThisArticle();		
		this.allArticles.add(a);
		int lastChildIndex = 0;
		for(int i = 0; i<this.listModel.size();i++){			
			if(this.listModel.get(i).getParentId()==a.getParentId()){
				lastChildIndex = i;
			}			
		}
		((LinkedList)this.listModel).add(lastChildIndex+1, a);
	}
	public Article getThisArticle(){
		Article a = as.getArticleById(tempArticle.getArticleId());
		a.setGeneration(as.getArticleGeneration(this.tempArticle));
		return a;
	}
	
	
	public void renewTreeModel2() {
		this.myArticleTreeModel = null;
		this.rootArticle = ArticleDataUtil.getRoot();
		this.myArticleTreeModel = new ArticleTreeModel(this.rootArticle);
		this.setPickedTreeItem(this.rootArticle);
	}

	@NotifyChange({ "groupModel", "lastest10Article" })
	public void renewGroupModel() {
		this.groupModel = null;
		// this method is not good, think a better method again;
		List<Article> atary = as.getAllArticles();
		Article[] ary = new Article[atary.size()];
		ary = atary.toArray(ary);
		this.groupModel = new ArticleGroupModel(ary, new ArticleComparator());
		this.setGroupModel(this.groupModel);
		logger.debug("renew done");
	}

	private void resetTempArticle() {
		this.tempArticle.setTitle("");
		this.tempArticle.setContent("");
		this.tempArticle.setParentId(null);
		this.tempArticle.setArticleId(null);
		this.chosenbox.clearSelection();
	}

	// @Command
	// public void dealArticle(){
	// Article article = (Article)sess.getAttribute("theArticle");
	// if(sess.getAttribute("Action").equals("Reply")){
	// Article newArticle = new Article();
	// newArticle.setTitle(tempArticle.getTitle());
	// newArticle.setContent(tempArticle.getContent());
	// newArticle.setUserId(theUser.getUserid());
	// newArticle.setParentId(article.getArticleId());
	// newArticle.setRootId(article.getRootId());
	// as.insertNewArticle(newArticle);
	//
	// }else if(sess.getAttribute("Action").equals("Edit")){
	// article.setTitle(tempArticle.getTitle());
	// article.setContent(tempArticle.getContent());
	// }
	// }
	//

}
