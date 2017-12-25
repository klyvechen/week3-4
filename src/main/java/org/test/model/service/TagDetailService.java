package org.test.model.service;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.TagDetail;

public class TagDetailService {
	
	private static final String INSERT_NEW_ARTICLE = "insert into tagDetail values(:articleId, :tagId)";
	public void insertTagDetail(TagDetail tagDetail){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(INSERT_NEW_ARTICLE);		
		query.setParameter("articleId",tagDetail.getArticleId());
		query.setParameter("tagId", tagDetail.getTagId());
		query.addEntity(TagDetail.class);
		query.executeUpdate();
		session.getTransaction().commit();
	}
}
