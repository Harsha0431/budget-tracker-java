package com.login;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.model.IncomeEntity;

@Entity
@Table(name = "user")
public class LoginEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "email")
	String email;
	@Column(name = "password")
	String password;
	@Column(name="name")
	String name;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IncomeEntity> incomes = new LinkedList<IncomeEntity>();

	public List<IncomeEntity> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<IncomeEntity> incomes) {
		this.incomes = incomes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void addIncome(IncomeEntity income) {
		this.incomes.add(income);
	}
	
	public void removeIncome(IncomeEntity income) {
		this.incomes.remove(income);
	}
}
