package com.store;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "homeStore", eager = true)
@SessionScoped
public class HomeStore implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean isLoggedIn = false;
	private String loginToken = "";
	private String userEmail = null;
	private String userName = null;
	
	private boolean showMainLoader = false;
	private String mainLoaderMessage = "" ;

	public String getMainLoaderMessage() {
		return mainLoaderMessage;
	}

	public void setMainLoaderMessage(String mainLoaderMessage) {
		this.mainLoaderMessage = mainLoaderMessage;
	}

	public boolean isShowMainLoader() {
		return showMainLoader;
	}

	public void setShowMainLoader(boolean showMainLoader) {
		this.showMainLoader = showMainLoader;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@PostConstruct
	public void init() {
		System.out.println("HomeStore post-construct initilized");
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public boolean getIsLoggedIn() {
		return isLoggedIn;
	}
	
	public void setIsLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	
	public String getLoginToken() {
		return loginToken;
	}
	
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	
	public String openDashboard() {
		if(isLoggedIn)
			return "dashboard.jsf";
		else
			return "login.jsf";
	}
	
	public String openAccountProfile() {
		if(isLoggedIn) {
			return "account.jsf";
		}
		else
			return "login.jsf";
	}
}
