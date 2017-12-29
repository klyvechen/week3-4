package org.test.model.service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.Article;
import org.test.model.User;

public class ArticleService {
	final static Logger logger = Logger.getLogger(ArticleService.class);
	private static final String GET_IDENTITY = "select top 1 * from article  order by articleId desc";
	private static final String GET_ALL_ARTICLE = "select * from article where status = 1 ";
	private static final String GET_LATEST10_ARTICLE = "select top 10 * from article where parentid is null and  status = 1 order by DATE desc, TIME desc";
	private static final String GET_LATEST10_REPLY = "select top 10 * from article where parentid is not null and  status = 1 order by DATE desc, TIME desc";
	private static final String GET_LATEST10_USERS_ARTICLE = "select top 10 * from article  where userId = :userId and  status = 1 order by DATE desc, TIME desc";
	private static final String GET_ARTICLE_BY_ID = "select * from article where articleID = :articleId";
	private static final String GET_CHILDREN_BY_PARENTID = "select * from article  where parentId = :parentId and  status = 1 order by DATE desc , TIME desc ";
	private static final String GET_CHILDREN_BY_PARENTID_ASC = "select * from article  where parentId = :parentId and  status = 1 order by DATE asc , TIME asc ";	
	private static final String GET_PARENTID_IS_NULL = "select * from article  where parentId is null and  status = 1 order by DATE asc , TIME asc";
	private static final String HAS_CHILD = "select * from article  where parentId = :articleId and status = 1";
	private static final String INSERT_NEW_ARTICLE = "insert into article values(:articleId, :parentId, :rootId, :userId, :title , :content,  CURRENT_DATE, CURRENT_TIME, :status)";
	private static final String UPDATE_ARTICLE = "update article set content = :content, title = :title where articleId = :articleId";
	private static final String DELETE_ARTICLE = "update article set status = :status  where articleId = :articleId";
	private static final String RECOVER_ALL = "update article set status = 1";
	public static Integer tableIdentity = 0;
	static{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_IDENTITY);		
		query.addEntity(Article.class);		
		Article u = (Article)query.uniqueResult();
		tableIdentity = (u!= null)? u.getArticleId(): 0;
		session.getTransaction().commit();		
	}
	
	public List<Article> getArticleDFSResult(Article rootArticle){
		logger.debug("in ArticleDFS");		
		List<Article> resultList = new LinkedList<Article>();
		Deque<Article> nextList = new LinkedList<Article>();
		nextList.add(rootArticle);
		rootArticle.setGeneration(0);		
		for(int i = 0; nextList.size() > 0;i++){			
			Article a = nextList.pop();
			int generation = a.getGeneration();
			List<Article> children =this.getChildrenByParentId(a.getArticleId());
			for(Article child: children){
				child.setGeneration(generation+1);
				nextList.push(child);
			}			
			resultList.add(a);
		}
		return resultList;
	}

	public List<Article> getAllArticles(){
		logger.debug("in the get lastest10Article");
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
		logger.debug("in the get lastest10Article");
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
			if((at.getTitle()!=null)&&at.getTitle().equals("")){
				at.setTitle("為推文 沒有標題");
			}
		}
		return articlelist;
	}
	
	

	public List<Article> getChildrenByParentId(Integer parentId){		
		List<Article> articlelist = new ArrayList<Article>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query;
		if(parentId!=null){
			query= session.createSQLQuery(GET_CHILDREN_BY_PARENTID);
			query.setParameter("parentId", parentId);
		}else{
			query = session.createSQLQuery(GET_PARENTID_IS_NULL);
		}
		query.addEntity(Article.class);
		articlelist = query.list();
		session.getTransaction().commit();		
		for(int i = 0; i< articlelist.size();i++){
			Article at = articlelist.get(i);
			if((at.getTitle()!=null)&&at.getTitle().equals("")){
				at.setTitle("為推文 沒有標題");
			}
		}
		return articlelist;
	}

	public List<Article> getChildrenByParentIdAsc(Integer parentId){		
		List<Article> articlelist = new ArrayList<Article>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query;
		if(parentId!=null){
			query= session.createSQLQuery(GET_CHILDREN_BY_PARENTID_ASC);
			query.setParameter("parentId", parentId);
		}else{
			query = session.createSQLQuery(GET_PARENTID_IS_NULL);
		}
		query.addEntity(Article.class);
		articlelist = query.list();
		session.getTransaction().commit();		
		for(int i = 0; i< articlelist.size();i++){
			Article at = articlelist.get(i);
			if((at.getTitle()!=null)&&at.getTitle().equals("")){
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
		article.setArticleId(tableIdentity);
		query.setParameter("parentId", article.getParentId());
		Integer rootId = (article.getRootId()!=null)?article.getRootId():tableIdentity;
		query.setParameter("rootId", rootId);
		query.setParameter("userId", article.getUserId());
		query.setParameter("title", article.getTitle());
		query.setParameter("content", article.getContent());
		query.setParameter("status", 1);
		query.addEntity(User.class);
		query.executeUpdate();
		session.getTransaction().commit();
	}
	
	public List<Article> deleteArticleAndChildren(Article article){
		List<Article> articleList = new ArrayList();
		articleList.add(article);		
		for(int i = 0; i< articleList.size();i++){
			Article thisArticle = articleList.get(i);
			articleList.addAll(getChildrenByParentId(thisArticle.getArticleId()));
			deleteArticle(thisArticle);
			logger.warn(i + ", " + article.getArticleId());
		}
		return articleList;
	}
	
	public void deleteArticle(Article article){		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(DELETE_ARTICLE);		
		query.setParameter("status", 0);
		query.setParameter("articleId", article.getArticleId());
		query.addEntity(User.class);
		query.executeUpdate();
		session.getTransaction().commit();		
	}
	
	public void recoverAllArticle(){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(RECOVER_ALL);		
		query.executeUpdate();
		session.getTransaction().commit();		
	}
	
	public void updateArticle(Article article){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(UPDATE_ARTICLE);		
		query.setParameter("articleId", article.getArticleId());
		query.setParameter("title", article.getTitle());
		query.setParameter("content", article.getContent());
		query.addEntity(User.class);
		query.executeUpdate();
		session.getTransaction().commit();
	}
	
	public Integer getArticleGeneration(Article ag){
		Article root = new Article();
		root.setGeneration(0);		
		List<Article> resultList = new LinkedList<Article>();
		Deque<Article> nextList = new LinkedList<Article>();
		nextList.add(root);
		Integer generation = 0;
		for(int i = 0; nextList.size() > 0;i++){			
			Article a = nextList.pop();
			generation= a.getGeneration();
			if(ag.getParentId() == a.getArticleId()){
				generation++;
				break;
			}			
			List<Article> children =this.getChildrenByParentId(a.getArticleId());
			for(Article child: children){				
				child.setGeneration(generation+1);
				nextList.push(child);
			}			
			resultList.add(a);
		}				
		return generation;
	}
	
	public Article getArticleById(Integer articleId){
		Article article = new Article();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_ARTICLE_BY_ID);
		query.setParameter("articleId", articleId);
		query.addEntity(Article.class);
		article = (Article)query.uniqueResult();
		session.getTransaction().commit();
		return article;
	}
	
	public boolean hasChildren(Integer articleId){
		
		List<Article> articlelist;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(HAS_CHILD);
		query.setParameter("articleId", articleId);
		query.addEntity(Article.class);
		articlelist = query.list();
		session.getTransaction().commit();		
		
		return (articlelist.size() == 0)? false: true;
	}
	
	public static void main(String[] args){
		ArticleService as = new  ArticleService();
		logger.debug(as.getLastest10Article());
		logger.debug(as.getLastest10Reply());
		logger.debug(as.getAllArticles());
		System.exit(0);
	}
}
