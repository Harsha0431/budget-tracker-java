package com.dashboard.manageExpenses;

import java.util.List;

import javax.ejb.Remote;

import com.login.LoginEntity;
import com.model.ExpenseCatalogEntity;
import com.model.ExpenseEntity;

@Remote
public interface ManageExpensesRemote {
	public List<String> addExpense(ExpenseEntity expense);
	public List<ExpenseCatalogEntity> getExpenseCategories(LoginEntity user);
	public ExpenseCatalogEntity getExpenseCatalogWithIdFromList(List<ExpenseCatalogEntity> expenseCatalogList, long id);
	public List<ExpenseEntity> getExpenseHistoryList(LoginEntity user, int offset);
}
