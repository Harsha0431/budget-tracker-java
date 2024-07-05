package com.login;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;


@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class RegisterService implements RegisterRemote {
	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("budgetTracker");

	@Override
	public List<String> registerUser(LoginEntity user) {
		List<String> response = new ArrayList<>();
		EntityManager manager = emf.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			manager.persist(user);
			transaction.commit();
			response.add("User regested successfylly.");
			response.add("success");
		}
		catch(PersistenceException e) {
			System.out.println("CAUGHT PersistenceException in in register service: " + e.getCause().toString());
			if(e.getCause().toString().contains("ConstraintViolationException")){
				response.add("Failed to register. Email already exists.");
                response.add("error");
			}
			else {
				response.add("Failed to register. Please try again!");
				response.add("error");
			}
			if(transaction.isActive())
				transaction.rollback();
		}
		catch(Exception e) {
			if(transaction.isActive())
				transaction.rollback();
			response.add("Failed to register. Please try again!");
			response.add("error");
			System.out.println("Caught error in registerUser service: " + e.getMessage());
		}
		finally {
			manager.close();
		}
		return response;
	}
}
