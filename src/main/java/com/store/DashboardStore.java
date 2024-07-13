package com.store;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "dashboard", eager = true)
@SessionScoped
public class DashboardStore {
	private String activePage = "home";
	enum Pages {
		home,
		manageIncome,
		manageExpenses,
		viewReports
	}
	
	public String getActivePage() {
		return activePage;
	}
	
	public void activatePage(String page) {
        if (isValidPage(page)) {
            this.activePage = page;
        }
        else {
        	this.activePage = "home";
        }
    }

    private boolean isValidPage(String page) {
        try {
            Pages.valueOf(page);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
