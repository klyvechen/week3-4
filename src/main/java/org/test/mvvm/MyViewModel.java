 package org.test.mvvm;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.Article;
import org.test.model.Tag;
import org.test.model.TagDetail;
import org.test.model.TempArticle;
import org.test.model.User;
import org.test.model.group.ArticleComparator;
import org.test.model.group.ArticleGroupModel;
import org.test.model.mytree.ArticleDataUtil;
import org.test.model.mytree.ArticleTreeModel;
import org.test.model.mytree.ArticleTreeNode;
import org.test.model.service.ArticleService;
import org.test.model.service.AuthenService;
import org.test.model.service.TagDetailService;
import org.test.model.service.TagService;
import org.test.model.service.UserService;
import org.test.myevent.SampleExecutorHolder;
import org.test.thread.insertAfter5Sec;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Popup;
import org.zkoss.zul.TreeNode;

public class MyViewModel{
	private static Logger logger ;	
	private List<Article> lastest10UserArticle;
	private List<Article> lastest10Article;
	private List<Article> lastest10Reply;
	private List<Article> allArticles;
	private List<Tag> allTagList;
	private ArticleGroupModel groupModel;
	private ArticleTreeModel myArticleTreeModel;
	private TreeNode<Article> rootArticle;
	private ArticleTreeNode pickedTreeItem;
	private TempArticle tempArticle= new TempArticle();
	private User loginUser = new User();
	private User regUser = new User();
	private User theUser = new User();
	private Session sess;
	private UserCre uc;	
	private org.zkoss.zk.ui.event.EventQueue<Event> xSecondEvtQue = EventQueues.lookup("count to x second", EventQueues.APPLICATION,true);   
	private org.zkoss.zk.ui.event.EventQueue<Event> insertNewArticleEvtQue = EventQueues.lookup("insertNewArticle", EventQueues.APPLICATION,true);
	private org.zkoss.zk.ui.event.EventQueue<Event> undoInsertEvtQue = EventQueues.lookup("undo insert Article", EventQueues.APPLICATION,true);
	private org.zkoss.zk.ui.event.EventQueue<Event> insertAfter5 = EventQueues.lookup("insertAfter5", EventQueues.APPLICATION,true);
	private ExecutorService executorService;
	private Desktop desktop = Executions.getCurrent().getDesktop();
	private boolean undoFlag = false;
	private Popup popup1 , popup2;
	private Hlayout  checkboxlist;
	private List<Checkbox> checkboxlis22t;
	private String selectedArticleContent = "please select content";
	Button undo;
	Popup ckarticleEditor;
	Popup waitFor10Sec;

	UserService us = new UserService();
	ArticleService as;
	TagService ts = new TagService();
	TagDetailService tds = new  TagDetailService();
	
    @Init
    public void init() {
    	//SampleExecutorHolder seh = new SampleExecutorHolder();
    	PropertyConfigurator.configure("log4j.properties");
    	SampleExecutorHolder seh = new SampleExecutorHolder();
    	logger = Logger.getLogger(MyViewModel.class);
    	HibernateUtil.getSessionFactory();
    	as = new ArticleService();
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
    	sess = Sessions.getCurrent();
    	uc = (UserCre)sess.getAttribute("userCre");
    	if(uc == null){
    		uc = new UserCre();
    		sess.setAttribute("userCre", uc);
    	}
    	executorService = SampleExecutorHolder.getExecutor();
    	//queSubScribe();
		this.insertAfter5.subscribe(new EventListener(){
			public void onEvent(Event evt){
				System.out.println(evt.getName());
				if(evt.getData() == getSess()){					
					insertArticle();			
				}
				notifiyToAll();
			}
		});
		this.rootArticle = ArticleDataUtil.getRoot();
		this.myArticleTreeModel= new ArticleTreeModel(this.rootArticle);
		System.out.println(this.myArticleTreeModel);
    }
	public ArticleTreeNode getPickedTreeItem() {
		return pickedTreeItem;
	}
	@NotifyChange("selectedArticleContent")
	public void setPickedTreeItem(ArticleTreeNode pickedTreeItem) {
		System.out.print("the pickedTreeItem is ");
		System.out.println(pickedTreeItem);
		this.pickedTreeItem = pickedTreeItem;
		this.setSelectedArticleContent(pickedTreeItem.getData().getContent());
	}
	public Session getSess() {
		return sess;
	}
	public void setSess(Session sess) {
		this.sess = sess;
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

	@NotifyChange({"lastest10UserArticle","groupModel"})
    @Command
    public void AuthenticateUser(){
    	if(AuthenService.verifyUser(loginUser)){
    		System.out.println("Login success");
    	    		theUser = loginUser;    	    		
    	    		this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
    	    		sess.setAttribute("sessionUser", theUser);
    	}
    }    
    
    @Command
    public void registUser(){
    	AuthenService.newUser(regUser);
    }
    
    /****start to get datas***/
	public User getLoginUser() {
		return loginUser;
	}


	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}


	public User getRegUser() {
		return regUser;
	}


	public void setRegUser(User regUser) {
		this.regUser = regUser;
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

	@NotifyChange
	public void setLastest10UserArticle(List<Article> articles){
		System.out.print("do lastest10UserArticle");
		this.lastest10UserArticle = articles;				
	}
	
	public List<Article> getLastest10UserArticle(){
		return this.lastest10UserArticle;
	}
	
	@Command
	public void savePopupComp(@BindingParam("popup1") Popup popup1,
			@BindingParam("popup2") Popup popup2, @BindingParam("chklist") Hlayout checkboxlist){
		this.popup1 = popup1;
		this.popup2 = popup2;
		this.checkboxlist = checkboxlist;
		System.out.println(checkboxlist);
	}
	
	@Command
	public void setCheckbox(@BindingParam("popup1") Popup popup1){
		
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
    	System.out.println("renew done");
	}
	
	@NotifyChange({"groupModel"})
	public void setGroupModel(ArticleGroupModel agm){
		this.groupModel = agm;
	}
	
	public ArticleGroupModel getGroupModel(){
		return this.groupModel;
	}
	
	public TempArticle getTemparticle() {
		return tempArticle;
	}

	public void TempArticle(TempArticle tempArticle) {
		this.tempArticle = tempArticle;
	}
	
	@Command
	public void newArticle() throws CloneNotSupportedException{
		System.out.println("new Article dosomething");
		//Event event = ctx.getTriggerEvent();
		//System.out.println(event.getName());
		System.out.println("print session");
		System.out.println(Sessions.getCurrent());		
		this.executorService.execute(new insertAfter5Sec(this.insertAfter5, this.desktop, this.sess));
		//this.executorService.execute(new WaitXSecond(this.xSecondEvtQue, this.desktop));
		//this.executorService.execute(new InsertArticle(new Button(), this.xSecondEvtQue, this.insertNewArticleEvtQue, this.undoInsertEvtQue,this.desktop));
		//System.out.println(Sessions.getCurrent());
		
		//createNewArticle();
	}
	
	private void insertArticle(){
		if(this.undoFlag== false){
			createNewArticle();

			this.popup2.close();
			this.popup1.close();
		}else{
			this.undoFlag = false;
		}
	}
	
	
	@Command
	public void changeUndoFlag(){
		System.out.println("undo inser article");
		this.undoFlag = true;
		this.popup2.close();
	}
	
	//@NotifyChange({"lastest10UserArticle","groupModel","tempArticle","lastest10Article"})
	public void createNewArticle(){
		System.out.println("start to create article");
		Article newArticle = new Article();
		newArticle.setTitle(tempArticle.getTitle());
		newArticle.setContent(tempArticle.getContent());
		newArticle.setUserId(theUser.getUserid());
		newArticle.setParentId(null);
		newArticle.setRootId(null);
		as.insertNewArticle(newArticle);
		this.checkboxlist.getChildren();
		for(Iterator itr = this.checkboxlist.getChildren().iterator(); itr.hasNext();){
			Checkbox chkbox = (Checkbox)itr.next(); 
			if(chkbox.isChecked()){
				TagDetail newTagDetail = new TagDetail();
				newTagDetail.setTagId(Integer.parseInt(chkbox.getName()));
				newTagDetail.setArticleId(newArticle.getArticleId());
				tds.insertTagDetail(newTagDetail);
			}			
		}		
	}	

    public void queSubScribe(){
    	insertNewArticleEvtQue.subscribe(new EventListener(){    		
    		public void onEvent(Event evt){

    		}
    	});
    }
    //@NotifyChange({"lastest10UserArticle","lastest10Article","lastest10Replay","groupModel"}) not working in zk eventQueue
    //https://stackoverflow.com/questions/18382760/zk-eventqueue-working-but-data-not-refreshing
    public void notifiyToAll(){
		System.out.println("in the que event listener");    			
		this.renewGroupModel();
		this.setLastest10Article(as.getLastest10Article());
		this.setLastest10Reply(as.getLastest10Reply());
		this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10UserArticle");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Article");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "lastest10Replay");
		BindUtils.postNotifyChange(null, null, MyViewModel.this, "groupModel");
    }
    
	
    @Command
    public void dealArticle(){
    	Article article = (Article)sess.getAttribute("theArticle");
    	if(sess.getAttribute("Action").equals("Reply")){
    		Article newArticle = new Article();
    		newArticle.setTitle(tempArticle.getTitle());
    		newArticle.setContent(tempArticle.getContent());
    		newArticle.setUserId(theUser.getUserid());
    		newArticle.setParentId(article.getArticleId());
    		newArticle.setRootId(article.getRootId());
    		as.insertNewArticle(newArticle);
    		
    	}else if(sess.getAttribute("Action").equals("Edit")){
    		article.setTitle(tempArticle.getTitle());
    		article.setContent(tempArticle.getContent());    		
    	}
    }
    
	/***start to insert update data***/
	
	public void insertArticle(Article article){	
		as.insertNewArticle(article);
	}
	

	
}
