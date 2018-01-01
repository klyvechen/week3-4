package org.test.model.service;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.User;

public class UserService {
	private static final String GET_IDENTITY = "select top 1 * from user order by userid desc";
	private static final String GET_USERS = "select * from User";
	private static final String GET_USER_BY_NAME = "select * from User where username = :un";
	private static final String GET_USER_BY_ID = "select * from User where userId = :uid";
	private static final String CREATE_NEW_USER = "INSERT INTO USER VALUES(:identity,:username, :password)";
	private static Integer tableIdentity = 0;
	static {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_IDENTITY);
		query.addEntity(User.class);
		User u = (User) query.uniqueResult();
		tableIdentity = (u != null) ? u.getUserid() : 0;
		session.getTransaction().commit();
	}

	public List<User> getUsers() {
		List<User> results = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_USERS);
		// query.setParameter(0, userId);
		query.addEntity(User.class);
		results = query.list();
		session.getTransaction().commit();
		for (int i = 0; i < results.size(); i++) {
			System.out.println(results.get(i).getUsername());
		}

		return results;
	}

	public User getUserByName(String username) {
		User u = new User();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_USER_BY_NAME);
		query.setParameter("un", username);
		query.addEntity(User.class);
		u = (User) query.uniqueResult();
		session.getTransaction().commit();
		return u;
	}

	public User getUserById(Integer userId) {
		User u = new User();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_USER_BY_ID);
		query.setParameter("uid", userId);
		query.addEntity(User.class);
		u = (User) query.uniqueResult();
		session.getTransaction().commit();
		return u;
	}

	public void createNewUser(User newuser) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(CREATE_NEW_USER);
		query.setParameter("identity", ++tableIdentity);
		query.setParameter("username", newuser.getUsername());
		query.setParameter("password", newuser.getPassword());
		query.addEntity(User.class);
		query.executeUpdate();
		session.getTransaction().commit();
	}

	public static void main(String[] args) {
		UserService us = new UserService();
		System.out.println(us.getUsers());
		System.exit(0);
	}

}
