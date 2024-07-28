package com.store;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Transient;

import com.dashboard.home.DashboardHomeRemote;
import com.google.gson.Gson;
import com.login.LoginEntity;
import com.login.LoginRemote;
import com.model.ExpenseEntity.Month;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ManagedBean(name = "dashboardHome", eager = true)
@SessionScoped
public class DashboardHomeStore implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value="#{homeStore}")
	private HomeStore homeStore;
	
	@EJB(lookup = "java:global/budget-tracker/DashboardHomeController!com.dashboard.home.DashboardHomeRemote")
	private DashboardHomeRemote dashboardHomeRemote;
	@EJB(lookup = "java:global/budget-tracker/LoginService!com.login.LoginRemote")
	LoginRemote loginService;
	
	// Selected month and year and its income, expense and savings
	private short selectedMonth;
	private short selectedYear;
	private BigDecimal selectedIncome;
	private BigDecimal selectedExpense;
	private BigDecimal selectedSavings;
	private boolean failedToGetSelectedMonthIncomeExpenseSaving = false;
	
	private List<Object[]> expenseCatalogChartDataForSelectedYearMonth;
	private boolean failedToGetExpenseCatalogChartData = false;
	
	public String getExpenseCatalogChartDataForSelectedYearMonthJson() {
        Gson gson = new Gson();
        String s = gson.toJson(expenseCatalogChartDataForSelectedYearMonth);
        System.out.println(s);
        return s;
    }
	
	public void getSelectedMonthIncomeExpenseSaving() {
		if(homeStore.getUserEntity()==null) {
			LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
			homeStore.setUserEntity(user);
		};
		List<BigDecimal> response = dashboardHomeRemote.getSelectedMonthIncomeExpenseSaving(homeStore.getUserEntity(), selectedYear, selectedMonth);
		if(response!=null && response.size()>0) {
			selectedIncome = response.get(0);
			selectedExpense = response.get(1);
			selectedSavings = response.get(2);
			setFailedToGetSelectedMonthIncomeExpenseSaving(false);
			getSelectedExpensesCategory();
		}
		else {
			setFailedToGetSelectedMonthIncomeExpenseSaving(true);
		}
		System.out.println(response);
	}
	
    public void getSelectedExpensesCategory() {
    	boolean isLoggedIn = checkUserLoggedIn();
		if(!isLoggedIn) {
			return;
		}
    	if(homeStore.getUserEntity()==null) {
			LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
			homeStore.setUserEntity(user);
		}
		System.out.println("HERE BEFORE");
    	List<Object[]> response = dashboardHomeRemote.getUserExpensesCatalogData(homeStore.getUserEntity(), selectedYear, selectedMonth);
    	for (Object[] row : response)	 {
            System.out.println("Category: " + row[0] + ", Amount: " + row[1]);
        }
    	if(response == null) {
    		failedToGetExpenseCatalogChartData = true;
    	}
    	else {
    		expenseCatalogChartDataForSelectedYearMonth = response;
    		failedToGetExpenseCatalogChartData = false;
    	}
    }

	@PostConstruct
	public void init() {
		System.out.println("Came to DashboardHomeStore post construct");
		selectedMonth = (short) LocalDateTime.now().getMonthValue();
		selectedYear = (short) LocalDateTime.now().getYear();
		boolean isLoggedIn = checkUserLoggedIn();
		if(!isLoggedIn) {
			return;
		}
		else {
			getSelectedMonthIncomeExpenseSaving();
		}
	}
	
	public boolean checkUserLoggedIn() {
		if(!homeStore.getIsLoggedIn()) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			try {
				ec.redirect(ec.getRequestContextPath() + "/login.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
	
	public enum Month {
    	January,
        February,
        March,
        April,
        May,
        June,
        July,
        August,
        September,
        October,
        November,
        December;

        public static Month fromIndex(int index) {
            return Month.values()[index - 1];
        }
    }
	
	@Transient
	public String getMonthName(short index) {
		return Month.fromIndex(index).name();
	}
	
	public short getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(short selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public short getSelectedYear() {
		return selectedYear;
	}

	public void setSelectedYear(short selectedYear) {
		this.selectedYear = selectedYear;
	}

	public BigDecimal getSelectedIncome() {
		return selectedIncome;
	}

	public void setSelectedIncome(BigDecimal selectedIncome) {
		this.selectedIncome = selectedIncome;
	}

	public BigDecimal getSelectedExpense() {
		return selectedExpense;
	}

	public void setSelectedExpense(BigDecimal selectedExpense) {
		this.selectedExpense = selectedExpense;
	}

	public BigDecimal getSelectedSavings() {
		return selectedSavings;
	}

	public void setSelectedSavings(BigDecimal selectedSavings) {
		this.selectedSavings = selectedSavings;
	}
	
	public HomeStore getHomeStore() {
		return homeStore;
	}

	public void setHomeStore(HomeStore homeStore) {
		this.homeStore = homeStore;
	}

	public LoginRemote getLoginService() {
		return loginService;
	}
	
	public void setLoginService(LoginRemote loginService) {
		this.loginService = loginService;
	}

	public boolean isFailedToGetSelectedMonthIncomeExpenseSaving() {
		return failedToGetSelectedMonthIncomeExpenseSaving;
	}

	public void setFailedToGetSelectedMonthIncomeExpenseSaving(boolean failedToGetSelectedMonthIncomeExpenseSaving) {
		this.failedToGetSelectedMonthIncomeExpenseSaving = failedToGetSelectedMonthIncomeExpenseSaving;
	}

	public List<Object[]> getExpenseCatalogChartDataForSelectedYearMonth() {
		return expenseCatalogChartDataForSelectedYearMonth;
	}

	public void setExpenseCatalogChartDataForSelectedYearMonth(List<Object[]> expenseCatalogChartDataForSelectedYearMonth) {
		this.expenseCatalogChartDataForSelectedYearMonth = expenseCatalogChartDataForSelectedYearMonth;
	}

	public boolean isFailedToGetExpenseCatalogChartData() {
		return failedToGetExpenseCatalogChartData;
	}

	public void setFailedToGetExpenseCatalogChartData(boolean failedToGetExpenseCatalogChartData) {
		this.failedToGetExpenseCatalogChartData = failedToGetExpenseCatalogChartData;
	}
}
