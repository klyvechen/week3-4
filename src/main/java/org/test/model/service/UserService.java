package org.test.model.service;


import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.test.hibernate.util.HibernateUtil;
import org.test.model.User;

public class UserService {
	private static final String GET_USERS = "select * from User";

	public List<User> getUsers() {
		List<User> results = null;
//		 try {
//		     Class.forName("org.hsqldb.jdbc.JDBCDriver" );
//		     Connection c = DriverManager.getConnection("jdbc:hsqldb:file:E:/hsqldb/hsqldb/hemrajdb", "SA", "");
//		     Statement stm = c.createStatement();
//		     ResultSet rs = stm.executeQuery(GET_USERS);
//		     while(rs.next()){
//		    	 System.out.println( rs.getInt(1));
//		     }
//		     System.out.println(rs);
//		     rs.close();
//		     stm.close();
//		     c.close();
//		     return null;
//		 } catch (Exception e) {
//		     System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
//		     e.printStackTrace();
//		     return null;
//		 }
		 
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(GET_USERS);
		//query.setParameter(0, userId);
		query.addEntity(User.class);
		results = query.list();
		session.getTransaction().commit();
		for(int i = 0 ; i< results.size(); i++){
			System.out.println(results.get(i).getUsername());	
		}
		
		return results;
	}
	
	
	public static void main(String[] args){
		UserService us = new UserService();		
		System.out.println(us.getUsers());
		System.exit(0);
	}

}
