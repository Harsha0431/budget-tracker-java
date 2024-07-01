package com.login;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ManagedBean(name = "authBean")
@Stateless
@SessionScoped
public class AuthModel {
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid Email")
	private String loginEmail;
	@Size(min = 1, message = "Please enter password")
	private String loginPassword;
	
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
	
	public void handleLogin() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			System.out.println(loginEmail + " " + loginPassword);
			System.out.println(loginEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"));
			if(loginEmail!=null && loginPassword!=null && loginEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") && loginPassword.length()>0) {
				System.out.println("Came to login");
			}
			else {
				System.out.println("Invalid Credentials");
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email or password", null));
				return;
			}			
		}
		catch (Exception e) {
			System.out.println("Caught error in handleLogin: " + e.getMessage());
		}
	}
	
}
