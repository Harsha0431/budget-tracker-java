package com.store;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
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
import javax.validation.constraints.Size;

import com.dashboard.manageExpenses.ManageExpensesRemote;
import com.login.LoginEntity;
import com.login.LoginRemote;
import com.manageIncome.ManageIncomeRemote;
import com.model.ExpenseCatalogEntity;
import com.model.ExpenseEntity;
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
	@EJB(lookup = "java:global/budget-tracker/ManageExpensesController!com.dashboard.manageExpenses.ManageExpensesRemote")
	ManageExpensesRemote manageExpensesRemote;
	
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
	private List<IncomeEntity> incomeTransactionList = new LinkedList<>();
	private boolean haveMoreTransactions = true;
	private int incomeTransactionListLimit = 10;
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
    
    // *Manage Expenses attributes*
	enum ManageExpenseActiveOptions {
		addNewExpense,
		viewExpenseHistory,
		viewExpenseCatalog
	}
	private String manageExpenseActive = "addNewExpense";
	private String manageExpenseFormMessage;
	private boolean manageExpenseFormMessageIsError = false;
	private List<ExpenseEntity> expenseHistoryListAll = new LinkedList<>();
	// Add expense
	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.1", inclusive = true, message = "Invalid expense ammount")
	private BigDecimal expenseAmount;
	@NotNull(message = "Allocated month is required")
	@Min(value = 1L, message = "Please select valid month")
	@Max(value = 12L,  message = "Please select valid month")
	private short expenseAllocatedMonth;
	@NotNull(message = "Allocated year is required")
	@Min(value = 1900L, message = "Year must be 1900 or later")
	private short expenseAllocatedYear;
	@NotNull(message = "Please select category")
	private Long expenseCategoryId;
	private String expenseDescription;
	// Expense catalog list
	private List<ExpenseCatalogEntity> expenseCatalogList = new LinkedList<>();
	// Expenses history
	private boolean loadMoreExpenseHistoryListAll = true;
	// Expense catalog attributes
	private boolean openAddCatalogForm = false;
	@NotNull(message = "Category title is required")
	@Size(min=3, max=128)
	private String addCatalogCategoryTitle;
	private String addCatalogDescription;
	private String addCatalogFormMessage;
	private boolean addCatalogFormMessageIsError = false;
	
	public void handleResetCatalogAddForm() {
		addCatalogCategoryTitle = null;
		addCatalogDescription = null;
	}
	
	public void handleCatalogBtnClick(boolean openAddCatalogForm) {
		if(!openAddCatalogForm) {
			handleResetCatalogAddForm();
		}
		addCatalogFormMessage = null;
		addCatalogFormMessageIsError = false;
		this.openAddCatalogForm = openAddCatalogForm;
	}
	
	public void handleAddCategoryEntity() {
		try {
			if(homeStore.getUserEntity()==null) {
				LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
				homeStore.setUserEntity(user);
			};
			boolean categoryTitleAlreadyExists = false;
			for(ExpenseCatalogEntity e: expenseCatalogList) {
				if(e.getCategory().equalsIgnoreCase(addCatalogCategoryTitle.trim()))
				{
					setAddCatalogFormMessage("Category with title "+ addCatalogCategoryTitle.trim() +" already exists");
					setAddCatalogFormMessageIsError(true);
					categoryTitleAlreadyExists = true;
					break;
				}
			}
			if(categoryTitleAlreadyExists)
				return;
			System.out.println(categoryTitleAlreadyExists);
			homeStore.setMainLoaderMessage(null);
			homeStore.setShowMainLoader(true);
			ExpenseCatalogEntity category = new ExpenseCatalogEntity();
			category.setCategory(addCatalogCategoryTitle);
			category.setDescription(addCatalogDescription);
			category.setUser(homeStore.getUserEntity());
			List<String> response = manageExpensesRemote.addCatalog(category);
			addCatalogFormMessageIsError = response.get(0).equals("error");
			addCatalogFormMessage = response.get(1);
			if(!addCatalogFormMessageIsError) {
				expenseCatalogList.add(0, category);
			}
			handleResetCatalogAddForm();
			homeStore.setShowMainLoader(false);
		}
		catch(Exception e) {
			System.err.println("Caught error in handleAddCategologEntity() : " + e.getMessage());
			addCatalogFormMessage = "Failed to add catalog. Please try again";
			addCatalogFormMessageIsError = true;
			homeStore.setShowMainLoader(false);
		}
	}
	
	public void getLoadMyExpensesHistoryData() {
		try {
			if(homeStore.getUserEntity()==null) {
				LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
				homeStore.setUserEntity(user);
			}
			if(!loadMoreExpenseHistoryListAll)
				return;
			List<ExpenseEntity> list = manageExpensesRemote.getExpenseHistoryList(homeStore.getUserEntity(), expenseHistoryListAll.size());
			if(list.size()>0) {
				if(expenseHistoryListAll.size()>0)
					expenseHistoryListAll.addAll(list);
				else
					expenseHistoryListAll = list;
			}
			loadMoreExpenseHistoryListAll = list.size()>=10;
		}
		catch(Exception e) {
			System.err.println("Caught error in getLoadMyExpensesHistoryData() : " + e.getMessage());
		}
	}
	
	public void handleLoadMyExpensesHistoryList() {
		try {
			boolean isLoggedIn = checkUserLoggedIn();
			System.out.println(isLoggedIn);
			if(!isLoggedIn) {
				return;
			}
			homeStore.setShowMainLoader(true);
			homeStore.setMainLoaderMessage(null);
			getLoadMyExpensesHistoryData();
			homeStore.setShowMainLoader(false);
		}
		catch(Exception e) {
			System.err.println("Caught error in getting expenses list : " + e.getMessage());
			homeStore.setShowMainLoader(false);
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
	
	public void handleAddExpenseClick() {
		System.out.println("CAME TO ADD EXPENSE FORM SUBMIT");
		try {			
			boolean isLoggedIn = checkUserLoggedIn();
			if(!isLoggedIn) {
				return;
			}
			if(expenseAllocatedYear > (LocalDateTime.now().getYear()+2)) {
				manageExpenseFormMessage = "Allocated year shouldn't excced " + (LocalDateTime.now().getYear()+2);
				manageExpenseFormMessageIsError = true;
				return;
			}
			manageExpenseFormMessage = null;
			manageExpenseFormMessageIsError = false;
			homeStore.setShowMainLoader(true);
			homeStore.setMainLoaderMessage(null);
			if(homeStore.getUserEntity()==null) {
				LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
				homeStore.setUserEntity(user);
			}
			ExpenseEntity expense = new ExpenseEntity();
			expense.setUser(homeStore.getUserEntity());
			expense.setAllocatedMonth(expenseAllocatedMonth);
			expense.setAllocatedYear(expenseAllocatedYear);
			expense.setAmount(expenseAmount);
			expense.setDescription(expenseDescription);
			expense.setCreatedAt(LocalDateTime.now());
			ExpenseCatalogEntity catalog = manageExpensesRemote.getExpenseCatalogWithIdFromList(expenseCatalogList, expenseCategoryId);
			if(catalog == null) {
				manageExpenseFormMessage = "Please select valid category";
				manageExpenseFormMessageIsError = true;
				return;
			}
			expense.setCategory(catalog);
			List<String> response = manageExpensesRemote.addExpense(expense);
			manageExpenseFormMessage = response.get(1);
			manageExpenseFormMessageIsError = !response.get(0).equalsIgnoreCase("success");
			if(!manageExpenseFormMessageIsError) {
				expenseHistoryListAll.add(0, expense);
			}
			System.out.println("HANDLE ADD EXPENSE CLICK ~ RESPONSE: " + response.get(1));
			homeStore.setShowMainLoader(false);
			homeStore.setMainLoaderMessage(null);
			handleResetExpenseFormClick();
			return;
		}
		catch(Exception e) {
			System.out.println("Caught exception in adding expense due to " + e.getMessage());
			e.printStackTrace();
			manageExpenseFormMessage = "Failed to add expense";
			manageExpenseFormMessageIsError = true;
			homeStore.setShowMainLoader(false);
			homeStore.setMainLoaderMessage(null);
		}
	}
	
	public void handleResetExpenseFormClick() {
		System.out.println("CAME TO EXPENSE ADD FORM RESUBMIT");
		expenseAllocatedMonth = (short) LocalDateTime.now().getMonthValue();
		expenseAllocatedYear = (short) LocalDateTime.now().getYear();
		expenseAmount = BigDecimal.ZERO;
		expenseCategoryId = null;
	}
    
	public BigDecimal getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(BigDecimal expenseAmount) {
		this.expenseAmount = expenseAmount;
	}

	public short getExpenseAllocatedMonth() {
		return expenseAllocatedMonth;
	}

	public void setExpenseAllocatedMonth(short expenseAllocatedMonth) {
		this.expenseAllocatedMonth = expenseAllocatedMonth;
	}

	public short getExpenseAllocatedYear() {
		return expenseAllocatedYear;
	}

	public void setExpenseAllocatedYear(short expenseAllocatedYear) {
		this.expenseAllocatedYear = expenseAllocatedYear;
	}

	public Long getExpenseCategoryId() {
		return expenseCategoryId;
	}

	public void setExpenseCategoryId(Long expenseCategoryId) {
		this.expenseCategoryId = expenseCategoryId;
	}

	public String getExpenseDescription() {
		return expenseDescription;
	}

	public void setExpenseDescription(String expenseDescription) {
		this.expenseDescription = expenseDescription;
	}

	public String getManageExpenseActive() {
		return manageExpenseActive;
	}

	public void setManageExpenseActive(String manageExpenseActive) {
		this.manageExpenseActive = manageExpenseActive;
	}

	public String getManageExpenseFormMessage() {
		return manageExpenseFormMessage;
	}

	public void setManageExpenseFormMessage(String manageExpenseFormMessage) {
		this.manageExpenseFormMessage = manageExpenseFormMessage;
	}

	public boolean isManageExpenseFormMessageIsError() {
		return manageExpenseFormMessageIsError;
	}

	public void setManageExpenseFormMessageIsError(boolean manageExpenseFormMessageIsError) {
		this.manageExpenseFormMessageIsError = manageExpenseFormMessageIsError;
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
    	if(manageIncomeActive.equalsIgnoreCase("viewIncomeHistory"))
    		handleLoadTransactionList();
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
        	if(page.equals("manageExpenses")) {
        		expenseAllocatedMonth = (short) LocalDateTime.now().getMonthValue();
        		expenseAllocatedYear = (short) LocalDateTime.now().getYear();
        		homeStore.setShowMainLoader(true);
        		if(homeStore.getUserEntity()==null) {
    				LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
    				homeStore.setUserEntity(user);
    			}
        		if((expenseCatalogList.size()==0) || (expenseCatalogList.size()>0 && (!expenseCatalogList.get(0).getUser().getEmail().equals(homeStore.getUserEntity().getEmail())))) {
        			expenseCatalogList = manageExpensesRemote.getExpenseCategories(homeStore.getUserEntity());
        		}
        		homeStore.setShowMainLoader(false);
        	}
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
	
	public void handleLoadTransactionList() {
		homeStore.setShowMainLoader(true);
		if(homeStore.getUserEntity()==null) {
			LoginEntity user = loginService.getUserLoginEntity(homeStore.getUserEmail());
			homeStore.setUserEntity(user);
		}
		if(homeStore.getUserEntity()!=null) {
			int prevLength = incomeTransactionList.size() + incomeTransactionListLimit;
			List<IncomeEntity> data = manageIncomeController.getTransactionList(homeStore.getUserEntity(), incomeTransactionList.size(), incomeTransactionListLimit);
			if(incomeTransactionList.size()==0)
				incomeTransactionList = data;
			else
				incomeTransactionList.addAll(data);
			haveMoreTransactions = (incomeTransactionList.size() >= prevLength);
		}
		homeStore.setShowMainLoader(false);
		setManageIncomeActive(manageIncomeActive);
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
			if(manageIncomeActive.equalsIgnoreCase("viewIncomeHistory") && incomeTransactionList.size()==0)
				handleLoadTransactionList();
		}
		else {
			handleResetIncomeFormClick();
		}
		this.manageIncomeActive = manageIncomeActive;
	}
	
	public void changeManageExpenseActive(String manageExpenseActive) {
		if(!homeStore.getIsLoggedIn()) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {
				ec.redirect(ec.getRequestContextPath() + "/login.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// viewExpenseCatalog
		if(this.manageExpenseActive!=manageExpenseActive) {
			if(manageExpenseActive.equalsIgnoreCase("viewExpenseHistory") && expenseHistoryListAll.size()==0)
				handleLoadMyExpensesHistoryList();
			
		}
		else {
			// TODO: Review this
			handleResetExpenseFormClick();
		}
		this.manageExpenseActive = manageExpenseActive;
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
		if(!homeStore.getIsLoggedIn()) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {
				ec.redirect(ec.getRequestContextPath() + "/login.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		handleResetIncomeFormClick();
		homeStore.setShowMainLoader(false);
		homeStore.setMainLoaderMessage(null);
	}
	
	public void handleResetIncomeFormClick() {
		allocatedMonth = LocalDate.now().getMonthValue();
		allocatedYear = LocalDate.now().getYear();
		incomeAmmount = BigDecimal.ZERO;
		incomeDescription = null;
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

	public int getIncomeTransactionListLimit() {
		return incomeTransactionListLimit;
	}

	public void setIncomeTransactionListLimit(int incomeTransactionListLimit) {
		this.incomeTransactionListLimit = incomeTransactionListLimit;
	}

	public boolean isHaveMoreTransactions() {
		return haveMoreTransactions;
	}

	public void setHaveMoreTransactions(boolean haveMoreTransactions) {
		this.haveMoreTransactions = haveMoreTransactions;
	}

	public List<ExpenseCatalogEntity> getExpenseCatalogList() {
		return expenseCatalogList;
	}

	public void setExpenseCatalogList(List<ExpenseCatalogEntity> expenseCatalogList) {
		this.expenseCatalogList = expenseCatalogList;
	}

	public boolean isLoadMoreExpenseHistoryListAll() {
		return loadMoreExpenseHistoryListAll;
	}

	public void setLoadMoreExpenseHistoryListAll(boolean loadMoreExpenseHistoryListAll) {
		this.loadMoreExpenseHistoryListAll = loadMoreExpenseHistoryListAll;
	}

	public List<ExpenseEntity> getExpenseHistoryListAll() {
		return expenseHistoryListAll;
	}

	public void setExpenseHistoryListAll(List<ExpenseEntity> expenseHistoryListAll) {
		this.expenseHistoryListAll = expenseHistoryListAll;
	}

	public boolean isOpenAddCatalogForm() {
		return openAddCatalogForm;
	}

	public void setOpenAddCatalogForm(boolean openAddCatalogForm) {
		this.openAddCatalogForm = openAddCatalogForm;
	}

	public String getAddCatalogCategoryTitle() {
		return addCatalogCategoryTitle;
	}

	public void setAddCatalogCategoryTitle(String addCatalogCategoryTitle) {
		this.addCatalogCategoryTitle = addCatalogCategoryTitle.trim();
	}

	public String getAddCatalogDescription() {
		return addCatalogDescription;
	}

	public void setAddCatalogDescription(String addCatalogDescription) {
		this.addCatalogDescription = addCatalogDescription;
	}

	public String getAddCatalogFormMessage() {
		return addCatalogFormMessage;
	}

	public void setAddCatalogFormMessage(String addCatalogFormMessage) {
		this.addCatalogFormMessage = addCatalogFormMessage;
	}

	public boolean isAddCatalogFormMessageIsError() {
		return addCatalogFormMessageIsError;
	}

	public void setAddCatalogFormMessageIsError(boolean addCatalogFormMessageIsError) {
		this.addCatalogFormMessageIsError = addCatalogFormMessageIsError;
	}
}
