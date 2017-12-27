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

public class MyViewModel{
	final static Logger logger = Logger.getLogger(MyViewModel.class);
	private org.zkoss.zk.ui.event.EventQueue<Event> insertAfter5 = EventQueues.lookup("insertAfter5", EventQueues.APPLICATION,true);

	private Desktop desktop = Executions.getCurrent().getDesktop();
	private List<Article> lastest10UserArticle,lastest10Article,lastest10Reply,allArticles;
	private List<Tag> allTagList;
	private ArticleGroupModel groupModel;
	private ArticleTreeModel myArticleTreeModel;
	private ArticleTreeNode pickedTreeItem;
	private ArticleTreeNode rootArticle;
	private Article tempArticle= new Article();	
	private User theUser;
	private Session sess;
	private ExecutorService executorService;
	private boolean undoFlag = false;
	private Popup popup1 , popup2;
	private Hlayout  checkboxlist;
	private List<Checkbox> checkboxlis22t;
	private String selectedArticleContent = "please select content";
	private String action;
	Button undo;
	Popup ckarticleEditor;
	Popup waitFor10Sec;
	private UserService us = new UserService();
	private ArticleService as = new ArticleService();
	private TagService ts = new TagService();
	private TagDetailService tds = new  TagDetailService();
	private ListModelList chosenboxModel = new ListModelList<Tag>(ts.getAllTag());
	private Set<Tag> selectedTag;
	private Chosenbox  chosenbox;
	private Set pickedChosenboxItem;



	@Init
    public void init() {
    	//SampleExecutorHolder seh = new SampleExecutorHolder();
    	SampleExecutorHolder seh = new SampleExecutorHolder();
    	sess = Sessions.getCurrent();
    	theUser = (User)sess.getAttribute("sessionUser");
    	if(theUser==null||theUser.getUserid()==null){
			 Executions.sendRedirect("/login.zul");
    	}else{
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
    	this.setCheckboxlist( Arrays.asList(new Checkbox[allTagList.size()]));
    	executorService = SampleExecutorHolder.getExecutor();
		this.insertAfter5.subscribe(new EventListener(){
			public void onEvent(Event evt){
				logger.debug(evt.getName());
				if(evt.getData() == getSess()){					
					insertArticle();			
				}
				notifiyToAll();
			}
		});
		this.rootArticle = ArticleDataUtil.getRoot();
		this.myArticleTreeModel= new ArticleTreeModel(this.rootArticle);
		this.setPickedTreeItem(this.rootArticle);
    	}
    }
    

	public ArticleTreeNode getPickedTreeItem() {
		return pickedTreeItem;
	}
	@NotifyChange("tempArticle")
	public void setPickedTreeItem(ArticleTreeNode pickedTreeItem) {
		logger.debug("the pickedTreeItem is "+pickedTreeItem);
		this.pickedTreeItem = pickedTreeItem;
		this.tempArticle.setContent(pickedTreeItem.getData().getTitle());
		this.tempArticle.setContent(pickedTreeItem.getData().getContent());
	}
    /****start to get datas***/
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
		logger.debug("get the pickedTreeItem is "+pickedTreeItem);
		return pickedChosenboxItem;
	}
	public void setPickedChosenboxItem(Set pickedChosenboxItem) {
		logger.debug("set the pickedTreeItem is "+pickedTreeItem);
		this.pickedChosenboxItem = pickedChosenboxItem;
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

	public List<Checkbox> getCheckboxlist() {
		return checkboxlis22t;
	}

	public void setCheckboxlist(List<Checkbox> checkboxlist) {
		this.checkboxlis22t = checkboxlist;
	}

	public List<Tag> getAllTagList() {
		return allTagList;
	}

	public void setAllTagList(List<Tag> allTagList) {
		this.allTagList = allTagList;
	}
    
	@NotifyChange({"lastest10Article"})
	public void setLastest10Article(List<Article> lastest10Article){
		System.out.print("do lastest10Article");
		this.lastest10Article = lastest10Article;
	}
	
	public List<Article> getLastest10Article(){
		return this.lastest10Article;
	}
	
	@NotifyChange({"lastest10Reply"})
	public void setLastest10Reply(List<Article> lastest10Article){
		System.out.print("do lastest10Reply");
		this.lastest10Reply = lastest10Article;
	}
	
	public List<Article> getLastest10Reply(){
		return this.lastest10Reply;
	}


	public void setAction(String action) {
		this.action = action;
	}


	@NotifyChange
	public void setLastest10UserArticle(List<Article> articles){
		System.out.print("do lastest10UserArticle");
		this.lastest10UserArticle = articles;				
	}
	
	public List<Article> getLastest10UserArticle(){
		return this.lastest10UserArticle;
	}
	
	public ArticleGroupModel getGroupModel(){
		return this.groupModel;
	}
	
	public Article getTempArticle() {
		return tempArticle;
	}

	public void setTempArticle(Article tempArticle) {
		this.tempArticle = tempArticle;
	}

	@NotifyChange({"groupModel"})
	public void setGroupModel(ArticleGroupModel agm){
		this.groupModel = agm;
	}
	
    @Command
    public void logout(){
    	sess.setAttribute("sessionUser",null);
    	 Executions.sendRedirect("/login.zul");
    }  
	
	/***start to insert update data***/
    
	@Command
	public void savePopupComp(@BindingParam("popup1") Popup popup1,
			@BindingParam("popup2") Popup popup2, @BindingParam("chbox") Chosenbox chosenbox,
			@BindingParam("action") Integer action){
		this.popup1 = popup1;
		this.popup2 = popup2;
		this.chosenbox = chosenbox;
		System.out.println(this.pickedTreeItem);
		if(action == 0){
			newArticle();
		}else if(action== 1){			
			reply();
		}else if(action==2){
			edit();
		}	
	}
	
	public void newArticle() {
		logger.debug("new Article dosomething");
		this.action="insert";
		this.tempArticle.setParentId(null);
		this.tempArticle.setRootId(null);
		this.tempArticle.setUserId(this.theUser.getUserid());		
	}
	
	@NotifyChange({"tempArticle"})
	public void reply(){
		
		this.action="insert";
		this.tempArticle.setTitle("Re:"+this.pickedTreeItem.getData().getTitle());
		this.tempArticle.setContent("");
		this.tempArticle.setParentId(this.pickedTreeItem.getData().getArticleId());
		this.tempArticle.setRootId(this.pickedTreeItem.getData().getRootId());
		this.tempArticle.setUserId(this.theUser.getUserid());	
		chosenbox.setSelectedObjects(ts.getTagsByArticleId(this.tempArticle.getArticleId()));
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
	}
	
	@NotifyChange({"tempArticle"})
	public void edit(){
		if(this.pickedTreeItem.getData().getParentId() != null){
			logger.warn(this.pickedTreeItem.getData().getContent());
			System.out.println(this.pickedTreeItem.getData().getContent());
			this.action="update";
			this.tempArticle.setArticleId(this.pickedTreeItem.getData().getArticleId());
			this.tempArticle.setTitle(this.pickedTreeItem.getData().getTitle());
			this.tempArticle.setContent(this.pickedTreeItem.getData().getContent());
			this.tempArticle.setParentId(this.pickedTreeItem.getData().getParentId());
			this.tempArticle.setRootId(this.pickedTreeItem.getData().getRootId());
			this.tempArticle.setUserId(this.pickedTreeItem.getData().getUserId());
			List<Tag> tempList = ts.getTagsByArticleId(this.tempArticle.getArticleId());
			List<Tag> selectedList = new LinkedList<Tag>();
			for(int i = 0; i< chosenboxModel.size();i++){
				for(int j = 0; j<tempList.size();j++){
					if(((Tag)chosenboxModel.get(i)).getTagId() == tempList.get(j).getTagId()){
						selectedList.add((Tag)chosenboxModel.get(i));
						break;
					}						
				}
			}
			chosenbox.setSelectedObjects(selectedList);
		}else{
			Messagebox.show("only reply can be edited");
			popup1.close();
		}		
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
	}
	@Command
	public void delete(){
		as.deleteArticleAndChildren(this.pickedTreeItem.getData());
		notifiyToAll();
	}
	@Command
	public void confirmArticle(){
		logger.debug(chosenbox.getSelectedObjects().size());
		logger.warn(chosenbox.getSelectedObjects().size());		
		this.executorService.execute(new insertAfter5Sec(this.insertAfter5, this.desktop, this.sess));
	}
	
	public boolean isMyArticle(){		
		return (this.getPickedTreeItem().getData().getUserId() == theUser.getUserid());
	}
	
	private void insertArticle(){
		if(this.undoFlag== false){
			this.selectedTag =this.chosenbox.getSelectedObjects(); 
			dealArticle();
			this.popup2.close();
			this.popup1.close();
		}else{
			this.undoFlag = false;
		}
	}
	
	
	@Command
	public void changeUndoFlag(){
		logger.debug("undo inser article");
		this.undoFlag = true;
		this.popup2.close();
	}
	
	public void dealArticle(){
		logger.debug("start to deal article");
		Set<Tag> tags =	 this.selectedTag;
		if(this.action.equals("insert")){
			as.insertNewArticle(this.tempArticle);	
		}else if(this.action.equals("update")){
			as.updateArticle(this.tempArticle);
			tds.deleteTagDetailByArticleId(this.tempArticle.getArticleId());
		}	
		TagDetail td = new TagDetail();
		td.setArticleId(tempArticle.getArticleId());
		for(Iterator itr = tags.iterator(); itr.hasNext();){			
			td.setTagId(((Tag)itr.next()).getTagId());
			tds.insertTagDetail(td);
		}	
	}	
    //@NotifyChange({"lastest10UserArticle","lastest10Article","lastest10Replay","groupModel"}) not working in zk eventQueue
    //https://stackoverflow.com/questions/18382760/zk-eventqueue-working-but-data-not-refreshing
    public void notifiyToAll(){
		logger.debug("in the que event listener");    			
		this.renewGroupModel();
		this.renewTreeModel();
		this.setLastest10Article(as.getLastest10Article());
		this.setLastest10Reply(as.getLastest10Reply());
		this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10UserArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Article");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Reply");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "groupModel");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "myArticleTreeModel");
		resetTempArticle();
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "tempArticle");
    }
    
    public void renewTreeModel(){
    	this.myArticleTreeModel = null;
    	this.rootArticle = ArticleDataUtil.getRoot();
		this.myArticleTreeModel= new ArticleTreeModel(this.rootArticle);
		this.setPickedTreeItem(this.rootArticle);
    }
    
	@NotifyChange({"groupModel","lastest10Article"})	
	public void renewGroupModel(){
		this.groupModel = null;
		//this method is not good, think a better method again;
		List<Article> atary = as.getAllArticles();
    	Article[] ary = new Article[atary.size()];
    	ary = atary.toArray(ary);
    	this.groupModel = new ArticleGroupModel(ary, new ArticleComparator());    	
    	this.setGroupModel(this.groupModel);
    	logger.debug("renew done");
	}
    
    private void resetTempArticle(){
    	this.tempArticle.setTitle("");
    	this.tempArticle.setContent("");
    	this.tempArticle.setParentId(null);
    	this.tempArticle.setArticleId(null);
    	this.chosenbox.clearSelection();
    }
	
//    @Command
//    public void dealArticle(){
//    	Article article = (Article)sess.getAttribute("theArticle");
//    	if(sess.getAttribute("Action").equals("Reply")){
//    		Article newArticle = new Article();
//    		newArticle.setTitle(tempArticle.getTitle());
//    		newArticle.setContent(tempArticle.getContent());
//    		newArticle.setUserId(theUser.getUserid());
//    		newArticle.setParentId(article.getArticleId());
//    		newArticle.setRootId(article.getRootId());
//    		as.insertNewArticle(newArticle);
//    		
//    	}else if(sess.getAttribute("Action").equals("Edit")){
//    		article.setTitle(tempArticle.getTitle());
//    		article.setContent(tempArticle.getContent());    		
//    	}
//    }
//    
	

	
}
