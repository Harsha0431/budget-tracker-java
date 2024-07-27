package com.dashboard.manageExpenses;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.login.LoginEntity;
import com.model.ExpenseCatalogEntity;
import com.model.ExpenseEntity;

@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class ManageExpensesController implements ManageExpensesRemote {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("budgetTracker");

	@Override
	public List<String> addExpense(ExpenseEntity expense) {
		List<String> response = new ArrayList<>();
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(expense);
			em.getTransaction().commit();
			response.add("success");
			response.add("Expense added successfully");
		}
		catch(Exception e) {
			if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
			System.err.println("Caught error in in addExpense() due to: " + e.getMessage());
			e.printStackTrace();
			response.add("error");
			response.add("Failed to add expense. Please try again");
		}
		finally {
			em.close();
		}
		return response;
	}

	@Override
	public List<ExpenseCatalogEntity> getExpenseCategories(LoginEntity user) {
		EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ExpenseCatalogEntity> query = em.createQuery("SELECT e FROM ExpenseCatalogEntity e where e.user=:user", ExpenseCatalogEntity.class);
            query.setParameter("user", user);
            return query.getResultList();
        } finally {
            em.close();
        }
	}

	@Override
	public ExpenseCatalogEntity getExpenseCatalogWithIdFromList(List<ExpenseCatalogEntity> expenseCatalogList, long id) {
		for(ExpenseCatalogEntity catalog: expenseCatalogList) {
			if(catalog.getId() == id)
				return catalog;
		}
		return null;
	}

	@Override
	public List<ExpenseEntity> getExpenseHistoryList(LoginEntity user, int offset) {
		List<ExpenseEntity> list = new LinkedList<>();
		EntityManager em = emf.createEntityManager();
		int limit = 10;
		try {
			TypedQuery<ExpenseEntity> query = em.createQuery("SELECT e from ExpenseEntity e where e.user=:user order by e.createdAt desc", ExpenseEntity.class);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			query.setParameter("user", user);
			list = query.getResultList();
			return list;
		}
		catch(Exception e) {		
			System.err.println("Caught error in getting expense history from controller: " + e.getMessage());
			return null;
		}
		finally {
			em.close();
		}
	}
}
