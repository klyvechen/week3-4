package org.test.model.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.Tag;

public class TagService {
	private static final String GET_ALL_TAGS = "select * from Tag";
	private static final String GET_ALL_TAGNAMES = "select tagContent from Tag";
	private static final String GET_TAGS_BY_ARTID = "select * from Tag t join tagDetail td on t.tagId = td.tagId  where td.articleId = :articleId";

	public List<Tag> getAllTag() {
		List<Tag> tl = new ArrayList<Tag>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_ALL_TAGS);
		query.addEntity(Tag.class);
		tl = query.list();
		session.getTransaction().commit();
		return tl;
	}

	public List<Tag> getTagsByArticleId(Integer articleId) {
		List<Tag> tl = new ArrayList<Tag>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_TAGS_BY_ARTID);
		query.setParameter("articleId", articleId);
		query.addEntity(Tag.class);
		tl = query.list();
		session.getTransaction().commit();
		return tl;
	}

}
