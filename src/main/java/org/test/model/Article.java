package org.test.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.test.model.service.UserService;
import org.test.mvvm.MyViewModel;


public class Article implements Cloneable,  Serializable {
	final static Logger logger = Logger.getLogger(Article.class);
	private Integer articleId; //primary key
	private Integer parentId;
	private Integer rootId;
	private Integer userId;
	private String title;
	private String content;
	private Date date;
	private Time time;
	private Integer generation = 0;
	private String margin;
	private List<Article> children = new LinkedList<Article>();
	private UserService us = new UserService();
	private SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	public Article(){
		this.title = "default title";
		this.content = "default content";
	}
	
	public String getMargin() {
		return "margin-left:"+ generation*30+"px";
	}		
	public Integer getArticleId() {
		return articleId;
	}
	public Integer getGeneration() {
		return generation;
	}

	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getRootId() {
		return rootId;
	}
	public void setRootId(Integer rootId) {
		this.rootId = rootId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getDate(){
		MyDate tempDate= new MyDate(Calendar.getInstance().getTime().getTime());
		if(date==null || time == null){
    		date = new Date(Calendar.getInstance().getTime().getTime());
    		time = new Time(Calendar.getInstance().getTime().getTime());
    	}else{
    		tempDate.setTime(date.getTime()+time.getTime());
		}
		return tempDate;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Time getTime() {
		return time;
	}
	public void setTime(Time time) {
		this.time = time;
	}
	
	public void setChildren(List<Article> children){
		this.children = children;
	}
	
	public List<Article> getChildren(){
		return this.children;
	}
	
	public void addChild(Article child){
		children.add(child);
	}
	
	public void removeChild(Article child){
		children.remove(child);
	}
	
    public String getUsername() {
    	String username;
    	if(getUserId() != null){
    		username =us.getUserById(getUserId()).getUsername();
    	}
    	else
    		username = "default name";
        return username;
    }
	@Override
	public Article clone() throws CloneNotSupportedException{
		Article a = (Article) super.clone();		
		return a;		
	}
}
