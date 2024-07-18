package com.store;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "dashboard", eager = true)
@SessionScoped
public class DashboardStore {
	@ManagedProperty(value="#{homeStore}")
	private HomeStore homeStore;
	
	private String activePage = "home";
	enum Pages {
		home,
		manageIncome,
		manageExpenses,
		viewReports
	}
	
	enum ManageIncomeActiveOptions {
		addNewIncome,
		viewIncomeHistory
	}
	private String manageIncomeActive = "addNewIncome";
	
	public String getActivePage() {
		return activePage;
	}

	public void setActivePage(String activePage) {
		this.activePage = activePage;
	}
	
	public void activatePage(String page) {
		if(!homeStore.getIsLoggedIn()) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {
				ec.redirect(ec.getRequestContextPath() + "/login.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        if (isValidPage(page)) {
            this.activePage = page;
        }
        else {
        	this.activePage = "home";
        }
    }

    public HomeStore getHomeStore() {
		return homeStore;
	}

	public void setHomeStore(HomeStore homeStore) {
		this.homeStore = homeStore;
	}

	private boolean isValidPage(String page) {
        try {
            Pages.valueOf(page);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

	public String getManageIncomeActive() {
		return manageIncomeActive;
	}

	public void setManageIncomeActive(String manageIncomeActive) {
		this.manageIncomeActive = manageIncomeActive;
	}
	
	public void changeManageIncomeActive(String manageIncomeActive) {
		if(this.manageIncomeActive!=manageIncomeActive)
			setManageIncomeActive(manageIncomeActive);
	}
}
