package com.login;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.store.HomeStore;

@ManagedBean(name = "authBean", eager = true)
@Stateless
@RequestScoped
public class AuthModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private static LoginService loginService = new LoginService();
	
	@ManagedProperty("#{homeStore}")
	private HomeStore homeStore;

	public HomeStore getHomeStore() {
		return homeStore;
	}

	public void setHomeStore(HomeStore homeStore) {
		this.homeStore = homeStore;
	}

	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid Email")
	private String loginEmail;
	@Size(min = 1, message = "Please enter password")
	private String loginPassword;
	private String loginMessage;
	private String loginMessageType;
	
	@PostConstruct
	public void init() {
		System.out.println("AuthModel post-construct initilized");
	}
	
	public String getLoginMessage() {
		return loginMessage;
	}

	public void setLoginMessage(String loginMessage) {
		this.loginMessage = loginMessage;
	}

	public String getLoginMessageType() {
		return loginMessageType;
	}

	public void setLoginMessageType(String loginMessageType) {
		this.loginMessageType = loginMessageType;
	}
	
	public String getLoginEmail() {
		return loginEmail;
	}
	
	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}
	
	public String getLoginPassword() {
		return loginPassword;
	}
	
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	
	public String handleLogin() {
		try {
			// System.out.println(loginEmail + " " + loginPassword);
			if(homeStore.getIsLoggedIn()) {
				loginMessage = "Already loggedin";
				loginMessageType = "warning";
				return "index.jsf?faces-redirect=true";
			}
			homeStore.setShowMainLoader(true);
			if(loginEmail!=null && loginPassword!=null && loginEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") && loginPassword.length()>0) {
				loginMessage = "Ready to login";
				loginMessageType = "success";
				List<String> response = loginService.verifyUserLoginDetails(loginEmail, loginPassword);
				loginMessage = response.get(0);
				loginMessageType = response.get(1);
				if(response.get(1).equals("success")) {
					homeStore.setIsLoggedIn(true);
					homeStore.setUserEmail(loginEmail);
					homeStore.setUserName(response.get(2));
					return "index.jsf?faces-redirect=true";
				}
			}
			else {
				homeStore.setIsLoggedIn(false);
				loginMessage = "Invalid login credentials";
				loginMessageType = "warning";
				return null;
			}			
		}
		catch (Exception e) {
			loginMessage = "Failed to login due to " + e.getMessage();
			loginMessageType = "error";
			System.out.println("Caught error in handle login:");
			e.printStackTrace();
		}
		finally {
			// homeStore.setShowMainLoader(false);
		}
		return null;
	}
	
	public void handleLogout() {
		try {
			if(!homeStore.getIsLoggedIn()) {
				return;
			}
			System.out.println("Came to logout: " + homeStore.getIsLoggedIn() +" --- " + homeStore.getUserEmail());
			homeStore.setIsLoggedIn(false);
			homeStore.setUserEmail(null);
			homeStore.setUserName(null);
		}
		catch (Exception e) {
			System.out.println("Caught error in handleLogout: ");
			e.getStackTrace();
		}
	}
	
}
