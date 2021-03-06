package org.test.mvvm;

import java.util.ArrayList;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Window;

public class MyViewModel {
	final static Logger logger = Logger.getLogger(MyViewModel.class);
	private org.zkoss.zk.ui.event.EventQueue<Event> insertAfter5 = EventQueues.lookup("insertAfter5",
			EventQueues.APPLICATION, true);
	private org.zkoss.zk.ui.event.EventQueue<Event> notifyallEventQueue = EventQueues.lookup("deleteEventQueue",
			EventQueues.APPLICATION, true);
	private ArticleDataUtil articleDataUtil = new ArticleDataUtil();
	private Desktop desktop = Executions.getCurrent().getDesktop();
	private List<Article> lastest10UserArticle, lastest10Article, lastest10Reply, allArticles, listModel, deleteList;
	private List<Tag> allTagList;
	private List<Tag> selectedTagList;
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
	private Window popup1, popup2;
	private String selectedArticleContent = "please select content";
	Button undo;
	Popup ckarticleEditor;
	Popup waitFor10Sec;
	private UserService us = new UserService();
	private ArticleService as = new ArticleService();
	private TagService ts = new TagService();
	private TagDetailService tds = new TagDetailService();
	private ListModelList chosenboxModel = new ListModelList<Tag>(ts.getAllTag());
	private Set<Tag> selectedTag;
	private Set pickedChosenboxItem;
	private boolean author, mainArticle;
	private static boolean renderflag = false;
	private Article shareArticle;
	private static Integer removeId;
	private boolean hasChildren;
	private boolean window1Visible = false;
	private boolean window2Visible = false;
	private boolean window3Visible = false;
	private boolean editing = false;
	private volatile static String action = "";
	private static Integer newArticleId;

	@Init
	public void init() {
		HibernateUtil.getSessionFactory();
		SampleExecutorHolder seh = new SampleExecutorHolder();
		sess = Sessions.getCurrent();
		theUser = (User) sess.getAttribute("sessionUser");
		this.setLastest10Article(as.getLastest10Article());
		this.setLastest10Reply(as.getLastest10Reply());
		this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
		this.setAllArticles(as.getAllArticles());
		this.setAllTagList(ts.getAllTag());
		executorService = SampleExecutorHolder.getExecutor();
		this.insertAfter5.subscribe(new EventListener() {
			public void onEvent(Event evt) {
				List obj = (List) evt.getData();
				ArticleDataUtil.setArticleAasB(shareArticle, (Article) obj.get(1));
				if ((Session) obj.get(0) == getSess()) {
					if (isUndoFlag() == false) {
						window2Visible = false;
						window1Visible = false;
						BindUtils.postNotifyChange(null, null, MyViewModel.this, "window2Visible");
						BindUtils.postNotifyChange(null, null, MyViewModel.this, "window1Visible");
						insertOrUpdateArticle();
						listModel = as.getArticleDFSResult(shareArticle);
						notifyallEventQueuePublish();
						setUndoFlag(false);
					}
					editing = false;
				}
			}
		});
		this.notifyallEventQueue.subscribe(new EventListener() {
			public void onEvent(Event evt) {
				setDeleteList((List) evt.getData());
				notifiyToAll();
			}
		});
		this.rootArticle = articleDataUtil.getRoot();
		this.myArticleTreeModel = new ArticleTreeModel(this.rootArticle);
		this.setPickedTreeItem(this.rootArticle);
		shareArticle = new Article();
		newArticleId = ArticleService.tableIdentity + 1;
		deleteList = new LinkedList();

	}

	public void notifyallEventQueuePublish() {
		logger.warn("in function");
		this.notifyallEventQueue.publish(new Event("notifyall", null, this.deleteList));
	}

	@NotifyChange({ "tempArticle", "author", "mainArticle", "listModel", "selectedTagList", "hasChildren" })
	public void setPickedArticle(Article pickedArticle) {
		logger.warn("in function");
		if (pickedArticle != null) {
			this.pickedArticle = pickedArticle;
		} else {
			this.pickedArticle = this.allArticles.get(1);
		}
		this.tempArticle.setArticleId(this.pickedArticle.getArticleId());
		this.tempArticle.setTitle(this.pickedArticle.getTitle());
		this.tempArticle.setContent(this.pickedArticle.getContent());
		this.author = (this.getPickedArticle() != null) ? (this.getPickedArticle().getUserId() == theUser.getUserid())
				: false;
		this.mainArticle = (this.pickedArticle != null) ? (this.pickedArticle.getParentId() == null) : false;
		this.listModel = as.getArticleDFSResult(pickedArticle);
		this.selectedTagList = ts.getTagsByArticleId(this.pickedArticle.getArticleId());
		this.hasChildren = as.hasChildren(this.pickedArticle.getArticleId());
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "author");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "mainArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "listModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "selectedTagList");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "hasChildren");
	}

	/*** getter and setter ***/

	public boolean isWindow1Visible() {
		return window1Visible;
	}

	public void setWindow1Visible(boolean window1Visible) {
		this.window1Visible = window1Visible;
	}

	public boolean isWindow2Visible() {
		return window2Visible;
	}

	public void setWindow2Visible(boolean window2Visible) {
		this.window2Visible = window2Visible;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
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
		this.pickedTreeItem = pickedTreeItem;
		if (this.pickedTreeItem.getData() != null) {

			this.setPickedArticle(this.pickedTreeItem.getData());
		}
	}

	public List<Article> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List<Article> deleteList) {
		this.deleteList = new LinkedList();
		for (int i = 0; i < deleteList.size(); i++) {
			this.deleteList.add(deleteList.get(i));
		}
	}

	public Article getPickedArticle() {
		return pickedArticle;
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
		return pickedChosenboxItem;
	}

	public void setPickedChosenboxItem(Set pickedChosenboxItem) {
		this.pickedChosenboxItem = pickedChosenboxItem;
	}

	public List<Tag> getSelectedTagList() {
		return selectedTagList;
	}

	public void setSelectedTagList(List<Tag> selectedTagList) {
		this.selectedTagList = selectedTagList;
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

	public void setLastest10Article(List<Article> lastest10Article) {
		logger.warn("do lastest10Article");
		this.lastest10Article = lastest10Article;
	}

	public List<Article> getLastest10Article() {
		return this.lastest10Article;
	}

	public void setLastest10Reply(List<Article> lastest10Article) {
		this.lastest10Reply = lastest10Article;
	}

	public List<Article> getLastest10Reply() {
		return this.lastest10Reply;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isUndoFlag() {
		return undoFlag;
	}

	public void setUndoFlag(boolean undoFlag) {
		this.undoFlag = undoFlag;
	}

	@NotifyChange
	public void setLastest10UserArticle(List<Article> articles) {
		this.lastest10UserArticle = articles;
	}

	public List<Article> getLastest10UserArticle() {
		return this.lastest10UserArticle;
	}

	public Article getTempArticle() {
		return tempArticle;
	}

	public void setTempArticle(Article tempArticle) {
		this.tempArticle = tempArticle;
	}

	public boolean isWindow3Visible() {
		return window3Visible;
	}

	public void setWindow3Visible(boolean window3Visible) {
		this.window3Visible = window3Visible;
	}
	
	public boolean getAuthor() {
		boolean isAuthor = (this.pickedArticle != null) ? (this.pickedArticle.getUserId() == theUser.getUserid())
				: false;
		return this.author;
	}

	/*** command zone ***/
	@Command
	public void logout() {
		sess.setAttribute("sessionUser", null);
		Executions.sendRedirect("/login.zul");
	}

	@NotifyChange({ "window3Visible" })
	@Command
	public void closeWindow3() {
		this.window3Visible = false;
	}

	/*** start to data from ckeditor ***/

	@NotifyChange("window1Visible")
	@Command("savePopupComp")
	public void savePopupCompfunc(@BindingParam("action") Integer action) {
		if (editing == false) {
			editing = true;
			this.window1Visible = true;
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
	}

	@NotifyChange({ "window1Visible", "window2Visible" })
	@Command
	public void popupOff() {
		editing = false;
		this.window1Visible = false;
		this.window2Visible = false;
	}

	@NotifyChange({ "tempArticle" })
	public void newArticle() {
		this.action = "insert";
		this.tempArticle.setContent("");
		this.tempArticle.setTitle("");
		this.tempArticle.setParentId(null);
		this.tempArticle.setRootId(null);
		this.tempArticle.setUserId(this.theUser.getUserid());
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
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
			this.action = "update";
			this.tempArticle.setArticleId(this.pickedArticle.getArticleId());
			this.tempArticle.setTitle(this.pickedArticle.getTitle());
			this.tempArticle.setContent(this.pickedArticle.getContent());
			this.tempArticle.setParentId(this.pickedArticle.getParentId());
			this.tempArticle.setRootId(this.pickedArticle.getRootId());
			this.tempArticle.setUserId(this.pickedArticle.getUserId());
		} else {
			Messagebox.show("only reply can be edited");
			this.window1Visible = false;
		}
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
	}

	private void setChosenboxSelectItem() {
		List<Tag> chosneboxSelectedList = new LinkedList<Tag>();
		for (int i = 0; i < chosenboxModel.size(); i++) {
			for (int j = 0; j < selectedTagList.size(); j++) {
				if (((Tag) chosenboxModel.get(i)).getTagId() == selectedTagList.get(j).getTagId()) {
					chosneboxSelectedList.add((Tag) chosenboxModel.get(i));
					break;
				}
			}
		}
		chosenboxModel.setSelection(chosneboxSelectedList);
	}

	@Command
	public void delete() {
		action = "delete";
		deleteList = as.deleteArticleAndChildren(this.pickedArticle);
		removeId = this.pickedArticle.getArticleId();
		notifyallEventQueuePublish();
	}

	@NotifyChange("window2Visible")
	@Command
	public void confirmArticle() {
		editing = false;
		this.window2Visible = true;
		this.undoFlag = false;
		ArticleDataUtil.setArticleAasB(shareArticle, this.tempArticle);
		List al = new ArrayList();
		al.add(this.sess);
		al.add(shareArticle);
		newArticleId = ArticleService.tableIdentity + 1;
		this.executorService.execute(new insertAfter5Sec(this.insertAfter5, this.desktop, al));
	}


	private void insertOrUpdateArticle() {
		if (this.undoFlag == false) {
			this.selectedTag = this.chosenboxModel.getSelection();
			dealArticle();
			this.window1Visible = false;
			this.window2Visible = false;
		} else {
			this.undoFlag = false;
		}
	}

	@NotifyChange({ "window2Visible", "window3Visible" })
	@Command
	public void changeUndoFlag() {
		editing = false;
		this.undoFlag = true;
		this.window2Visible = false;
		this.window3Visible = true;
	}

	public void dealArticle() {
		this.tempArticle = shareArticle;
		Set<Tag> tags = this.selectedTag;
		if (this.action.equals("insert")) {
			as.insertNewArticle(shareArticle);
		} else if (this.action.equals("update")) {
			as.updateArticle(shareArticle);
			tds.deleteTagDetailByArticleId(shareArticle.getArticleId());
		}
		TagDetail td = new TagDetail();
		td.setArticleId(shareArticle.getArticleId());
		for (Iterator itr = tags.iterator(); itr.hasNext();) {
			td.setTagId(((Tag) itr.next()).getTagId());
			tds.insertTagDetail(td);
		}
	}

	
	/***the total model notifier***/
	// not working in zk eventQueue
	// https://stackoverflow.com/questions/18382760/zk-eventqueue-working-but-data-not-refreshing
	@NotifyChange({ "tempArticle", "author", "mainArticle", "listModel", "selectedTagList", "myArticleTreeModel",
			"lastest10Reply", "lastest10Article", "lastest10UserArticle", "allArticles" })
	public void notifiyToAll() {
		setLastest10Article(as.getLastest10Article());
		setLastest10Reply(as.getLastest10Reply());
		setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
		setShareArticle();
		renewTreeModel();
		renewAllModel();
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10UserArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Article");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Reply");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "myArticleTreeModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "author");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "mainArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "listModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "selectedTagList");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "allArticles");
	}

	/***the total model renewer***/
	public void renewTreeModel() {
		Article a = new Article();
		ArticleDataUtil.setArticleAasB(a, shareArticle);
		if (action.equals("insert")) {
			ArticleDataUtil.addArticleToTree(a, myArticleTreeModel);
		} else if (action.equals("update")) {
			ArticleDataUtil.editArticleFromTree(a, (TreeNode) myArticleTreeModel.getRoot());
		} else {
			ArticleDataUtil.removeArticleFromTree(removeId, (TreeNode) myArticleTreeModel.getRoot());
		}
	}

	public void renewAllModel() {
		Article a = new Article();
		ArticleDataUtil.setArticleAasB(a, shareArticle);
		if (action.equals("insert")) {
			this.allArticles.add(a);
		} else if (action.equals("update")) {
			for (int i = 0; i < this.allArticles.size(); i++) {
				Article allArticleChildi = this.allArticles.get(i);
				if (allArticleChildi.getArticleId() == a.getArticleId()) {
					allArticleChildi.setContent(a.getContent());
					allArticleChildi.setTitle(a.getTitle());
				}
			}

		} else {
			List<Article> removeIndexList = new LinkedList();
			int k = 0;
			for (int i = 0; i < allArticles.size(); i++) {
				Article article = allArticles.get(i);
				for (int j = 0; j < this.deleteList.size(); j++) {
					if (article.getArticleId().equals(deleteList.get(j).getArticleId())) {
						logger.warn("k = " + k++);

						removeIndexList.add(article);
					}
				}
			}
			for (int i = 0; i < removeIndexList.size(); i++) {
				allArticles.remove(removeIndexList.get(i));
			}
		}
	}

	public void renewListModel() {
		Article a = new Article();
		ArticleDataUtil.setArticleAasB(a, shareArticle);
		if (action.equals("insert")) {
			int lastChildIndex = 0;
			for (int i = 0; i < this.listModel.size(); i++) {
				if (this.listModel.get(i).getParentId() == a.getParentId()) {
					lastChildIndex = i;
				}
			}
			((LinkedList) this.listModel).add(lastChildIndex + 1, a);
		} else if (action.equals("update")) {
			for (int i = 0; i < this.listModel.size(); i++) {
				Article listchild = listModel.get(i);
				if (listchild.getArticleId() == a.getArticleId()) {
					listchild.setContent(a.getContent());
					listchild.setTitle(a.getTitle());
				}
			}
		} else {
			for (int i = 0; i < listModel.size(); i++) {
				for (int j = 0; j < this.deleteList.size(); j++) {
					Article listchild = listModel.get(i);
					if (listchild.getArticleId() == deleteList.get(j).getArticleId()) {
						listModel.remove(listchild);
					}
				}
			}
		}
	}

	public void setShareArticle() {
		if (action.equals("insert")) {
			shareArticle.setArticleId(newArticleId);
		}
		Article a = as.getArticleById(shareArticle.getArticleId());
		if (a != null) {
			shareArticle.setDate(as.getArticleById(shareArticle.getArticleId()).getDate());
			shareArticle.setTime(as.getArticleById(shareArticle.getArticleId()).getTime());
		}
	}

	public void setArticleAasB(Article a, Article b) {
		a.setArticleId(b.getArticleId());
		a.setContent(b.getContent());
		a.setDate(b.getDate());
		a.setGeneration(b.getGeneration());
		a.setParentId(b.getParentId());
		a.setRootId(b.getRootId());
		a.setTime(b.getTime());
		a.setTitle(b.getTitle());
		a.setUserId(b.getUserId());
	}

	private void resetTempArticle() {
		this.tempArticle.setTitle("");
		this.tempArticle.setContent("");
		this.tempArticle.setParentId(null);
	}
}
