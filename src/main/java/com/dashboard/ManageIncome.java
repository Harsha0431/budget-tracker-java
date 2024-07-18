package com.dashboard;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.manageIncome.ManageIncomeRemote;

@ManagedBean(name = "manageIncomeService", eager = true)
@SessionScoped
@Stateful
public class ManageIncome {
	
	@EJB(lookup = "java:global/budget-tracker/ManageIncomeController!com.manageIncome.ManageIncomeRemote")
	private ManageIncomeRemote incomeController;

}
