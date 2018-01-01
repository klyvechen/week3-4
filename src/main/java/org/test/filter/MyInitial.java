package org.test.filter;

import java.util.Map;

import org.test.hibernate.util.HibernateUtil;
import org.test.model.User;
import org.test.myevent.SampleExecutorHolder;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;

public class MyInitial implements Initiator {
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		SampleExecutorHolder seh = new SampleExecutorHolder();
		
		sess = Sessions.getCurrent();
		theUser = (User) sess.getAttribute("sessionUser");
		if(theUser == null){			
			Executions.sendRedirect("/login.zul");
		}
		
	}
	private User theUser;
	private Session sess;


}
