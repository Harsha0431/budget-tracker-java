package com.store;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "homeStore", eager = true)
@Stateful(
	description = "This bean is used to manage home page states."
)
@SessionScoped
public class HomeStore implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean darkTheme = true;
	private boolean navOpen = false;
	private boolean isLoggedIn = false;
	private String loginToken = "";
	
	public boolean getDarkTheme() {
		return darkTheme;
	}
	
	public void setDarkTheme(boolean darkTheme) {
		this.darkTheme = darkTheme;
	}
	
	public boolean getNavOpen() {
		return navOpen;
	}
	
	public void setNavOpen(boolean navOpen) {
		this.navOpen = navOpen;
	}
	
	public boolean getLoggedIn() {
		return isLoggedIn;
	}
	
	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	
	public String getLoginToken() {
		return loginToken;
	}
	
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	
	public void toggleTheme() {
		System.out.println(darkTheme);
		setDarkTheme(!darkTheme);
	}
}
