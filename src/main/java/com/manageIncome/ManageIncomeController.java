package com.manageIncome;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
			em.close();
		}
	}

}
