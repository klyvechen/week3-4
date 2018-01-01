package org.test.mvvm;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.test.model.User;
import org.test.model.service.AuthenService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

public class LoginViewModel {
	final static Logger logger = Logger.getLogger(LoginViewModel.class);
	private Session sess;
	public Session getSess() {
		return sess;
	}

	public void setSess(Session sess) {
		this.sess = sess;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	private User loginUser = new User();

	@Init
	public void init() {
    	PropertyConfigurator.configure("log4j.properties");
		sess = Sessions.getCurrent();
	}

	@Command
	public void AuthenticateUser() {
		if (AuthenService.verifyUser(loginUser)) {
			sess.setAttribute("sessionUser", loginUser);
			 Executions.sendRedirect("/index.zul");
		}else{
			Messagebox.show("wrong");
		}

	}
}
