package com.dashboard.home;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remote;

import com.login.LoginEntity;

@Remote
public interface DashboardHomeRemote {
	public List<BigDecimal> getSelectedMonthIncomeExpenseSaving(LoginEntity user, short year, short month);
	
	public List<Object[]> getUserExpensesCatalogData(LoginEntity user, short year, short month);
}
