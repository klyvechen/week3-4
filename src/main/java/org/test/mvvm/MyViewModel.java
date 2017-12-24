 package org.test.mvvm;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.test.hibernate.util.HibernateUtil;
import org.test.model.Article;
import org.test.model.TempArticle;
import org.test.model.User;
import org.test.model.group.ArticleComparator;
import org.test.model.group.ArticleGroupModel;
import org.test.model.service.ArticleService;
import org.test.model.service.AuthenService;
import org.test.model.service.TagDetailService;
import org.test.model.service.TagService;
import org.test.model.service.UserService;
import org.test.model.tree.PackageData;
import org.test.model.tree.PackageDataUtil;
import org.test.myevent.SampleExecutorHolder;
import org.test.thread.InsertArticle;
import org.test.thread.WaitXSecond;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
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
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class MyViewModel{
	private List<Article> lastest10UserArticle;
	private List<Article> lastest10Article;
	private List<Article> lastest10Reply;
	private ArticleGroupModel groupModel;
	private TempArticle tempArticle= new TempArticle();
	private User loginUser = new User();
	private User regUser = new User();
	private User theUser = new User();
	private Session sess;
	private UserCre uc;
	private SampleExecutorHolder seh = new SampleExecutorHolder();
	private org.zkoss.zk.ui.event.EventQueue<Event> xSecondEvtQue = EventQueues.lookup("count to x second", EventQueues.APPLICATION,true);   
	private org.zkoss.zk.ui.event.EventQueue<Event> insertNewArticleEvtQue = EventQueues.lookup("insertNewArticle", EventQueues.APPLICATION,true);
	private org.zkoss.zk.ui.event.EventQueue<Event> undoInsertEvtQue = EventQueues.lookup("undo insert Article", EventQueues.APPLICATION,true);
	private ExecutorService executorService;
	private Desktop desktop = Executions.getCurrent().getDesktop();
	@Wire("#undo")
	Button undo;
	@Wire("#ckarticleEditor")
	Popup ckarticleEditor;
	@Wire("#waitFor10Sec")
	Popup waitFor10Sec;

	UserService us = new UserService();
	ArticleService as;
	TagService ts = new TagService();
	TagDetailService tds = new  TagDetailService();
	
    @Init
    public void init() {
    	//SampleExecutorHolder seh = new SampleExecutorHolder();
    	HibernateUtil.getSessionFactory();
    	as = new ArticleService();
    	List<Article> atary = as.getAllArticles();
    	Article[] ary = new Article[atary.size()];
    	ary = atary.toArray(ary);
    	this.groupModel = new ArticleGroupModel(ary, new ArticleComparator());
    	this.setLastest10Article(as.getLastest10Article());
    	this.setLastest10Reply(as.getLastest10Reply());
    	this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
    	sess = Sessions.getCurrent();
    	uc = (UserCre)sess.getAttribute("userCre");
    	if(uc == null){
    		uc = new UserCre();
    		sess.setAttribute("userCre", uc);
    	}
    	executorService = SampleExecutorHolder.getExecutor();
    	queSubScribe();
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
	public void newArticle(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) throws CloneNotSupportedException{
		System.out.println("new Article dosomething");
		Event event = ctx.getTriggerEvent();
		System.out.println(event.getName());
//		waitFor10Sec.open(0,0);
		System.out.println(Sessions.getCurrent());
		this.executorService.execute(new WaitXSecond(this.xSecondEvtQue));
		this.executorService.execute(new InsertArticle(new Button(), this.xSecondEvtQue, this.insertNewArticleEvtQue, this.undoInsertEvtQue,this.desktop));
		//createNewArticle();
//		ckarticleEditor.close();
	}
	
    @Command
    public void undoClick(){
    	System.out.println("get click");
    	undoInsertEvtQue.publish(new Event("undoInsert",null));
    }

	
	@NotifyChange({"lastest10UserArticle","groupModel","tempArticle","lastest10Article"})
	public void createNewArticle(){
		System.out.println("notify show result");
		Article newArticle = new Article();
		newArticle.setTitle(tempArticle.getTitle());
		newArticle.setContent(tempArticle.getContent());
		newArticle.setUserId(theUser.getUserid());
		newArticle.setParentId(null);
		newArticle.setRootId(null);
		newArticle.setTagId(null);
		as.insertNewArticle(newArticle);				
	}	

    public void queSubScribe(){
    	insertNewArticleEvtQue.subscribe(new EventListener(){    		
    		public void onEvent(Event evt){
    			createNewArticle();
    			doQueSubscribeEvent(evt);	
    		}
    	});
    }
    //@NotifyChange({"lastest10UserArticle","lastest10Article","lastest10Replay","groupModel"}) not working in zk eventQueue
    //https://stackoverflow.com/questions/18382760/zk-eventqueue-working-but-data-not-refreshing
    public void doQueSubscribeEvent(Event evt){
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
    		newArticle.setTagId(null);	
    		as.insertNewArticle(newArticle);
    		
    	}else if(sess.getAttribute("Action").equals("Edit")){
    		article.setTitle(tempArticle.getTitle());
    		article.setContent(tempArticle.getContent());    		
    	}
    }
    
	@Command
	public void save(){
	
	}
		
	/***start to insert update data***/
	
	public void insertArticle(Article article){	
		as.insertNewArticle(article);
	}
	
	
	public TreeModel<TreeNode<PackageData>> getTreeModel() {
        return new DefaultTreeModel<PackageData>(PackageDataUtil.getRoot());
    }
	
}
