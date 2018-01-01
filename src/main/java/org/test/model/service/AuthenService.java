package org.test.model.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.test.model.User;

public class AuthenService {
	public static boolean verifyUser(User verifyuser) {
		UserService us = new UserService();
		User user = us.getUserByName(verifyuser.getUsername());
		if (user == null) {
			System.out.println("no this user");
			return false;
		} else {
			String pw = verifyuser.getPassword();
			MessageDigest md;
			String shapw = "";
			try {
				md = MessageDigest.getInstance("SHA-1");
				md.update(pw.getBytes("UTF-8"), 0, pw.length());
				shapw = DatatypeConverter.printHexBinary(md.digest());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (shapw.equals(user.getPassword())) {
				verifyuser.setUserid(user.getUserid());
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean newUser(User newUser) {
		UserService us = new UserService();
		String pw = newUser.getPassword();
		MessageDigest md;
		String shapw = "";
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(pw.getBytes("UTF-8"), 0, pw.length());
			shapw = DatatypeConverter.printHexBinary(md.digest());
			newUser.setPassword(shapw);
			us.createNewUser(newUser);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return true;
	}
}
