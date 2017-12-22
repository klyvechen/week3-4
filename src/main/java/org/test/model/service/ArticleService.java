package org.test.model.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.Article;
import org.test.model.User;

public class ArticleService {
	private static final String GET_IDENTITY = "select top 1 * from article order by articleId desc";
	private static final String GET_ALL_ARTICLE = "select * from article ";
	private static final String GET_LATEST10_ARTICLE = "select top 10 * from article where parentid is null order by DATE desc, TIME desc";
	private static final String GET_LATEST10_REPLY = "select top 10 * from article where parentid is not null order by DATE desc, TIME desc";
	private static final String GET_LATEST10_USERS_ARTICLE = "select top 10 * from article  where userId = :userId order by DATE desc, TIME desc";
	private static final String INSERT_NEW_ARTICLE = "insert into article values(:articleId, :parentId, :rootId, :userId, :title , :content, :tagId, CURRENT_DATE, CURRENT_TIME, :status)";
	private static final String UPDATE_ARTICLE = "update article set content = :content, title = :title where articleId = :articleId";
	private static Integer tableIdentity = 0;
	static{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_IDENTITY);		
		query.addEntity(Article.class);		
		Article u = (Article)query.uniqueResult();
		tableIdentity = (u!= null)? u.getArticleId(): 0;
		session.getTransaction().commit();		
	}
	
	public List<Article> getAllArticles(){
		System.out.println("in the get lastest10Article");
		List<Article> articlelist = new ArrayList<Article>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_ALL_ARTICLE);
		query.addEntity(Article.class);
		articlelist = query.list();
		session.getTransaction().commit();		
		return articlelist;
	}
	
	public List<Article> getLastest10Article(){
		System.out.println("in the get lastest10Article");
		List<Article> articlelist = new ArrayList<Article>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_LATEST10_ARTICLE);
		query.addEntity(Article.class);
		articlelist = query.list();
		session.getTransaction().commit();		

		return articlelist;
	}
	
	public List<Article> getLastest10Reply(){
		List<Article> articlelist = new ArrayList<Article>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_LATEST10_REPLY);
		query.addEntity(Article.class);
		articlelist = query.list();
		session.getTransaction().commit();		
		for(int i = 0; i< articlelist.size();i++){
			Article at = articlelist.get(i);
			if(at.getTitle().equals("")){
				at.setTitle("為推文 沒有標題");
			}
		}
			
		return articlelist;
	}
	
	public List<Article> getLastest10UserArticle(int userId){
		List<Article> articlelist = new ArrayList<Article>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_LATEST10_USERS_ARTICLE);
		query.setParameter("userId", userId);
		query.addEntity(Article.class);
		articlelist = query.list();
		session.getTransaction().commit();		
		for(int i = 0; i< articlelist.size();i++){
			Article at = articlelist.get(i);
			if(at.getTitle().equals("")){
				at.setTitle("為推文 沒有標題");
			}
		}
		return articlelist;
	}
	
	public void insertNewArticle(Article article){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		SQLQuery query = session.createSQLQuery(INSERT_NEW_ARTICLE);		
		query.setParameter("articleId", ++tableIdentity);
		query.setParameter("parentId", article.getParentId());
		Integer rootId = (article.getRootId()!=null)?article.getRootId():tableIdentity;
		query.setParameter("rootId", rootId);
		query.setParameter("userId", article.getUserId());
		query.setParameter("title", article.getTitle());
		query.setParameter("content", article.getContent());
		query.setParameter("tagId", article.getTagId());
		query.setParameter("status", 1);
		query.addEntity(User.class);
		query.executeUpdate();
		session.getTransaction().commit();
	}
	public void deleteArticle(Article article){
		
	}
	
	public void updateArticle(Article article){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(INSERT_NEW_ARTICLE);		
		query.setParameter("articleId", article.getArticleId());
		query.setParameter("title", article.getTitle());
		query.setParameter("content", article.getContent());
		query.setParameter("status", 1);
		query.addEntity(User.class);
		query.executeUpdate();
		session.getTransaction().commit();
	}
	
	public void deleteArticle(){
		
	}
	
	public static void main(String[] args){
		ArticleService as = new  ArticleService();
		System.out.println(as.getLastest10Article());
		System.out.println(as.getLastest10Reply());
		System.out.println(as.getAllArticles());
		System.exit(0);
	}
}
