package org.test.model.service;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.Article;
import org.test.model.TagDetail;

public class TagDetailService {
	
	private static final String INSERT_TAGDETAIL = "insert into tagDetail values(:tagId, :articleId)";
	private static final String DELETE_TAGDETAIL_BY_ARTICLEID = "delete from tagDetail where articleId = :articleId";
	public void insertTagDetail(TagDetail tagDetail){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(INSERT_TAGDETAIL);		
		query.setParameter("articleId",tagDetail.getArticleId());
		query.setParameter("tagId", tagDetail.getTagId());
		query.addEntity(TagDetail.class);
		query.executeUpdate();
		session.getTransaction().commit();
	}
	public void deleteTagDetailByArticleId(Integer articleId){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(DELETE_TAGDETAIL_BY_ARTICLEID);		
		query.setParameter("articleId",articleId);		
		query.executeUpdate();
		session.getTransaction().commit();
	}
}
