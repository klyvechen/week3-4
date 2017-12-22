 package org.test.mvvm;

import java.util.List;

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
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeModel;
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
	private Button newArticleBtn;
	private SampleExecutorHolder seh = new SampleExecutorHolder();
	private org.zkoss.zk.ui.event.EventQueue<Event> que = EventQueues.lookup("there is a new article", EventQueues.APPLICATION,true);

	UserService us = new UserService();
	ArticleService as;
	TagService ts = new TagService();
	TagDetailService tds = new  TagDetailService();
	
    @Init
    public void init() {
    	
    	HibernateUtil.getSessionFactory();
    	as = new ArticleService();
    	List<Article> atary = as.getAllArticles();
    	Article[] ary = new Article[atary.size()];
    	ary = atary.toArray(ary);
    	this.groupModel = new ArticleGroupModel(ary, new ArticleComparator());
    	this.setLastest10Article();
    	this.setLastest10Reply();
    	this.setLastest10UserArticle(as.getLastest10UserArticle(theUser.getUserid()));
    	sess = Sessions.getCurrent();
    	uc = (UserCre)sess.getAttribute("userCre");
    	if(uc == null){
    		uc = new UserCre();
    		sess.setAttribute("userCre", uc);
    	}
    	que.subscribe(new EventListener(){
    		public void onEvent(Event evt){
    			System.out.println("in the que event listener");
    			System.out.println(evt.getName());
    			renewGroupModel();
    		}
    	});;
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
    
	public void setLastest10Article(){
		this.lastest10Article = as.getLastest10Article();
	}
	
	public List<Article> getLastest10Article(){
		return this.lastest10Article;
	}
	
	public void setLastest10Reply(){
		this.lastest10Reply = as.getLastest10Reply();
	}
	
	public List<Article> getLastest10Reply(){
		return this.lastest10Reply;
	}

	@NotifyChange
	public void setLastest10UserArticle(List<Article> articles){
		this.lastest10UserArticle = articles;				
	}
	
	public List<Article> getLastest10UserArticle(){
		return this.lastest10UserArticle;
	}
	@NotifyChange({"groupModel"})	
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
		createNewArticle();
	}
	
	@NotifyChange({"lastest10UserArticle","groupModel","tempArticle"})
	public void createNewArticle(){
		System.out.println("notify show result");
		Article newArticle = new Article();
		newArticle.setTitle(tempArticle.getTitle());
		newArticle.setContent(tempArticle.getContent());
		newArticle.setUserId(theUser.getUserid());
		newArticle.setParentId(null);
		newArticle.setRootId(null);
		newArticle.setTagId(null);
		System.out.println(theUser.getUserid());
		as.insertNewArticle(newArticle);
		que.publish(new Event("there is a new article",null));
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
