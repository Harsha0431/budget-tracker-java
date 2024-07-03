package com.login;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.store.HomeStore;

@ManagedBean(name = "authBean", eager = true)
@Stateless
@RequestScoped
public class AuthModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private static LoginService loginService = new LoginService();
	
	private static RegisterService registerService = new RegisterService();
	
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
			homeStore.setMainLoaderMessage("Authenticating ...");
			if(loginEmail!=null && loginPassword!=null && loginEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") && loginPassword.length()>0) {
				loginMessage = "Authenticating ...";
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
			homeStore.setShowMainLoader(false);
			homeStore.setMainLoaderMessage(null);
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
	
	
	// Register attributes
	@NotNull(message = "Email field shouldn't be empty")
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid Email")
	@Size(min=1, max = 500, message = "Email must be between 3 and 500 characters long.")
	private String registerEmail;
	@NotNull(message = "Password field shouldn't be empty")
	@Pattern(regexp="^(?=.*[a-z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters long, with at least one lowercase letter and one number.")
	@Size(min=8, max = 300, message = "Password must be between 8 and 300 characters long.")
	private String registerPassword;
	@NotNull(message = "Name field shouldn't be empty")
	@Size(min = 3, max=500 ,message = "Name must be between 3 and 500 characters long.")
	private String registerName;
	private String registerMessage, registerMessageType;

	public String getRegisterEmail() {
		return registerEmail;
	}

	public void setRegisterEmail(String registerEmail) {
		this.registerEmail = registerEmail;
	}

	public String getRegisterPassword() {
		return registerPassword;
	}

	public void setRegisterPassword(String registerPassword) {
		this.registerPassword = registerPassword;
	}

	public String getRegisterName() {
		return registerName;
	}

	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}

	public String getRegisterMessage() {
		return registerMessage;
	}

	public void setRegisterMessage(String registerMessage) {
		this.registerMessage = registerMessage;
	}

	public String getRegisterMessageType() {
		return registerMessageType;
	}

	public void setRegisterMessageType(String registerMessageType) {
		this.registerMessageType = registerMessageType;
	}
	
	public void handlerRegisterUser() {
		try {
			homeStore.setShowMainLoader(true);
			LoginEntity user = new LoginEntity();
			user.setEmail(registerEmail);
			user.setPassword(registerPassword);
			user.setName(registerName.toUpperCase());
			List<String> response = registerService.registerUser(user);
			registerMessage = response.get(0);
			registerMessageType = response.get(1);
			if(registerMessageType.equals("success")) {
				registerMessage = "Registered successfully. Please login to continue";
			}
		}
		catch(Exception e) {
			registerMessage = "Failed to register. Please try again!";
			registerMessageType = "error";
			System.out.println("Caught error in handleRegisterUser bean class: ");
			e.printStackTrace();
		}
		finally {
			homeStore.setShowMainLoader(false);
		}
	}
	
}
