package com.login;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface LoginRemote {
	public List<String> verifyUserLoginDetails(String email, String password);
	
	public String getPassword(String email);
}
