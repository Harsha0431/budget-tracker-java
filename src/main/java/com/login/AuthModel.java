package com.login;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ManagedBean(name = "authBean")
@Stateless
@SessionScoped
public class AuthModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private static LoginService loginService = new LoginService();
	
	private boolean loginStatus;
	private String userEmail;


	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid Email")
	private String loginEmail;
	@Size(min = 1, message = "Please enter password")
	private String loginPassword;
	private String loginMessage;
	private String loginMessageType;
	
	@PostConstruct
	public void init() {
		loginStatus = false;
	}
	
	public boolean getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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
			if(loginStatus) {
				loginMessage = "Already loggedin.";
				loginMessageType = "warning";
				return "index.jsf?faces-redirect=true";
			}
			if(loginEmail!=null && loginPassword!=null && loginEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") && loginPassword.length()>0) {
				loginMessage = "Ready to login";
				loginMessageType = "success";
				List<String> response = loginService.verifyUserLoginDetails(loginEmail, loginPassword);
				loginMessage = response.get(0);
				loginMessageType = response.get(1);
				if(response.get(1).equals("success")) {
					loginStatus = true;
					userEmail = loginEmail;
					loginPassword= null;
					loginMessage = null;
					return "index.jsf?faces-redirect=true";
				}
			}
			else {
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
		return null;
	}
	
	public void handleLogout() {
		try {
			System.out.println("Came to logout: " + loginStatus +" --- " + loginEmail);
			loginStatus = false;
			userEmail = null;
		}
		catch (Exception e) {
			System.out.println("Caught error in handleLogout: ");
			e.getStackTrace();
		}
	}
	
}
