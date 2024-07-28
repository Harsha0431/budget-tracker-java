package com.dashboard.home;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.login.LoginEntity;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DashboardHomeController implements DashboardHomeRemote {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("budgetTracker");

	@Override
	public List<BigDecimal> getSelectedMonthIncomeExpenseSaving(LoginEntity user, short year, short month) {
		List<BigDecimal> response = new LinkedList<>();
		EntityManager em = emf.createEntityManager();
		try {
			Query incomeQuery = em.createQuery("SELECT SUM(i.amount) from IncomeEntity i where i.user=:user and i.allocatedMonth=:month and i.allocatedYear=:year");
			incomeQuery.setParameter("user", user);
			incomeQuery.setParameter("month", Integer.valueOf(month));
			incomeQuery.setParameter("year", Integer.valueOf(year));
			BigDecimal income = (BigDecimal) incomeQuery.getSingleResult();
			System.out.println("INCOME: " + income);
			Query expenseQuery = em.createQuery("SELECT SUM(e.amount) from ExpenseEntity e where e.user=:user and e.allocatedMonth=:month and e.allocatedYear=:year");
			expenseQuery.setParameter("user", user);
			expenseQuery.setParameter("month", month);
			expenseQuery.setParameter("year", year);
			BigDecimal expense = (BigDecimal) expenseQuery.getSingleResult();
			if(income == null)
				income = BigDecimal.ZERO;
			if(expense == null)
				expense = BigDecimal.ZERO;
			BigDecimal savings = income.subtract(expense);
			response.add(income);
			response.add(expense);
			response.add(savings);
			return response;
		}
		catch(Exception e) {
			System.out.println("CAUGHT ERROR IN getSelectedMonthIncomeExpenseSaving(...) controller due to: " + e.getMessage() );
			e.printStackTrace();
			return null;
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getUserExpensesCatalogData(LoginEntity user, short year, short month) {
		List<Object[]> response = new LinkedList<>();
		EntityManager em = emf.createEntityManager();
		try {
			Query query = em.createQuery("SELECT e.category.category, SUM(e.amount) FROM ExpenseEntity e WHERE e.user = :user AND e.allocatedYear=:year and e.allocatedMonth=:month GROUP BY e.category");
			query.setParameter("user", user);
			query.setParameter("year", year);
			query.setParameter("month", month);
			response = query.getResultList();
			return response;
		}
		catch (Exception e) {
			System.err.println("Caught error in getUserExpensesCatalogData(...) in controller due to : " + e.getMessage());
			return null;
		}
		finally {
			em.close();
		}
	}
	
	

}
