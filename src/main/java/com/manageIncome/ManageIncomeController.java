package com.manageIncome;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.login.LoginEntity;
import com.model.IncomeEntity;

@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class ManageIncomeController implements ManageIncomeRemote {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("budgetTracker");
	
	@Override
	public List<String> addUserIncome(IncomeEntity income) {
		List<String> response = new ArrayList<>();
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(income);
			em.getTransaction().commit();
			response.add("success");
            response.add("Income added successfully");
			return  response;
		}
		catch(Exception e) {
			em.getTransaction().rollback();
			System.out.println("Caught exception in adding user income in ManageIncomeController: " + e.getMessage());
			e.printStackTrace();
			response.add("error");
			response.add("Failed to add user income");
			return response;
		}
		finally {
			if(em.isOpen())
				em.close();
		}
	}

	@Override
	public List<IncomeEntity> getTransactionList(LoginEntity userEntity) {
		List<IncomeEntity> response = null;
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<IncomeEntity> query = em.createQuery("SELECT i from IncomeEntity i where i.user=:user order by i.createdAt desc", IncomeEntity.class);
			query.setParameter("user", userEntity);
			response = query.getResultList();
			em.getTransaction().commit();
			return response;
		}
		catch(Exception e) {
			em.getTransaction().rollback();
			System.out.println("Caught exception in getting user income list in ManageIncomeController: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		finally {
			if(em.isOpen())
				em.close();
		}
	}

}
