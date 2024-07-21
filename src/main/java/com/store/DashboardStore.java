package com.store;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.login.LoginEntity;
import com.login.LoginRemote;
import com.manageIncome.ManageIncomeRemote;
import com.model.IncomeEntity;

@ManagedBean(name = "dashboard", eager = true)
@SessionScoped
public class DashboardStore {
	@ManagedProperty(value="#{homeStore}")
	private HomeStore homeStore;
	@EJB(lookup = "java:global/budget-tracker/LoginService!com.login.LoginRemote")
	LoginRemote loginService;
	@EJB(lookup = "java:global/budget-tracker/ManageIncomeController!com.manageIncome.ManageIncomeRemote")
	ManageIncomeRemote manageIncomeController;
	
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
	
	// *Manage Income attributes*
	private String manageIncomeActive = "viewIncomeHistory";
	private String manageIncomeFormMessage;
	private boolean manageIncomeFormMessageIsError = false;
	private List<IncomeEntity> incomeTransactionList;
	// Add Income
	@NotNull(message = "Allocated year is required")
	private int allocatedYear;
	@NotNull(message = "Allocated month is required")
	@Min(value = 1L, message = "Please select valid month")
	@Max(value = 12L,  message = "Please select valid month")
	private int allocatedMonth;
	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Invalid income ammount")
	private BigDecimal incomeAmmount;
	private String incomeDescription;
    ArrayList<Integer> incomeYearList = new ArrayList<>();
    public static class MonthValue{
    	public String month;
    	public String value;
    	public MonthValue(String month, String value) {
    		this.month = month;
    		this.value = value;
    	}
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
    }
	@PostConstruct
    public void init() {
		allocatedMonth = LocalDate.now().getMonthValue();
		allocatedYear = LocalDate.now().getYear();
    	int currentYear = LocalDate.now().getYear()-5;
    	while(currentYear < LocalDate.now().getYear()+5) {
    		incomeYearList.add(currentYear);
    		currentYear+=1;
    	}
    }

	public ArrayList<Integer> getIncomeYearList() {
		return incomeYearList;
	}
	
	public void setIncomeYearList(ArrayList<Integer> incomeYearList) {
		this.incomeYearList = incomeYearList;
	}

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
		if(!homeStore.getIsLoggedIn()) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {
				ec.redirect(ec.getRequestContextPath() + "/login.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(this.manageIncomeActive!=manageIncomeActive) {
			if(manageIncomeActive.equalsIgnoreCase("viewIncomeHistory") && incomeTransactionList!=null && incomeTransactionList.size()==0) {
				homeStore.setShowMainLoader(true);
				if(homeStore.getUserEntity()==null) {
					LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
					homeStore.setUserEntity(user);
				}
				incomeTransactionList = manageIncomeController.getTransactionList(homeStore.getUserEntity());
				homeStore.setShowMainLoader(false);
			}
			setManageIncomeActive(manageIncomeActive);
		}
	}

	public int getAllocatedYear() {
		return allocatedYear;
	}

	public void setAllocatedYear(int allocatedYear) {
		this.allocatedYear = allocatedYear;
	}

	public int getAllocatedMonth() {
		return allocatedMonth;
	}

	public void setAllocatedMonth(int allocatedMonth) {
		this.allocatedMonth = allocatedMonth;
	}

	public BigDecimal getIncomeAmmount() {
		return incomeAmmount;
	}

	public void setIncomeAmmount(BigDecimal incomeAmmount) {
		this.incomeAmmount = incomeAmmount;
	}

	public String getIncomeDescription() {
		return incomeDescription;
	}

	public void setIncomeDescription(String incomeDescription) {
		this.incomeDescription = incomeDescription;
	}
	
	public void handleAddIncomeClick() {
		homeStore.setShowMainLoader(true);
		homeStore.setMainLoaderMessage("Adding Income");
		if(homeStore.getUserEntity()==null) {
			LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
			homeStore.setUserEntity(user);
		}
		IncomeEntity income = new IncomeEntity();
		income.setUser(homeStore.getUserEntity());
		income.setAllocatedMonth(allocatedMonth);
		income.setAllocatedYear(allocatedYear);
		income.setAmount(incomeAmmount);
		income.setDescription(incomeDescription);
		income.setCreatedAt(LocalDateTime.now());
		List<String> response = manageIncomeController.addUserIncome(income);
		manageIncomeFormMessage = response.get(1);
		manageIncomeFormMessageIsError = !response.get(0).equalsIgnoreCase("success");
		if(!manageIncomeFormMessageIsError) {
			incomeTransactionList.add(0, income);
		}
		System.out.println("HANDLE ADD INCOME CLICK ~ RESPONSE: " + response.get(1));
		homeStore.setShowMainLoader(false);
		homeStore.setMainLoaderMessage(null);
	}
	
	public void handleResetIncomeFormClick() {
		allocatedMonth = LocalDate.now().getMonthValue();
		allocatedYear = LocalDate.now().getYear();
		incomeAmmount = BigDecimal.ZERO;
		incomeDescription = null;
		System.out.println(allocatedMonth + " " + allocatedYear + " " + incomeAmmount + " " + incomeDescription);
		System.out.println("HANDLE INCOME RESET CLICK");
	}

	public String getManageIncomeFormMessage() {
		return manageIncomeFormMessage;
	}

	public void setManageIncomeFormMessage(String manageIncomeFormMessage) {
		this.manageIncomeFormMessage = manageIncomeFormMessage;
	}

	public boolean isManageIncomeFormMessageIsError() {
		return manageIncomeFormMessageIsError;
	}

	public void setManageIncomeFormMessageIsError(boolean manageIncomeFormMessageIsError) {
		this.manageIncomeFormMessageIsError = manageIncomeFormMessageIsError;
	}

	public List<IncomeEntity> getIncomeTransactionList() {
		return incomeTransactionList;
	}

	public void setIncomeTransactionList(List<IncomeEntity> incomeTransactionList) {
		this.incomeTransactionList = incomeTransactionList;
	}
}
