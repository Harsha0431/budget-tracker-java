package com.login;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface RegisterRemote {
	public List<String> registerUser(LoginEntity user);
}
