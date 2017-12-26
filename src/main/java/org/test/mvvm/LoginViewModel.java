package org.test.mvvm;

import org.test.model.User;
import org.test.model.service.AuthenService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

public class LoginViewModel {
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
		sess = Sessions.getCurrent();
	}

	@Command
	public void AuthenticateUser() {
		if (AuthenService.verifyUser(loginUser)) {
			System.out.println("Login success");
			sess.setAttribute("sessionUser", loginUser);
			 Executions.sendRedirect("/index.zul");
		}else{
			Messagebox.show("wrong");
		}

	}
}
