package com.manageIncome;

import java.util.List;

import javax.ejb.Remote;

import com.model.IncomeEntity;

@Remote
public interface ManageIncomeRemote {
	public List<String> addUserIncome(IncomeEntity income);
}
