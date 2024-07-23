package com.login;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class LoginService implements LoginRemote {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("budgetTracker");

	@Override
	public List<String> verifyUserLoginDetails(String email, String password) {
		List<String> result = getPassword(email);
		List<String> response = new ArrayList<>();
		String message = null , status = null;
		if(result.size() == 0) {
			message = "Invalid email or user not found";
			status = "error";
		}
		else {
			String correctPassword = result.get(0);
			if(password.matches(correctPassword)) {
				message = "Login successful";
				status = "success";
				
			}
			else {
				message = "Invalid password";
				status = "warning";
			}
		}
		response.add(message);
		response.add(status);
		if(status.equals("success"))
			response.add(result.get(1));
		return response;
	}

	@Override
	public List<String> getPassword(String email) {
		EntityManager manager = null;
		List<String> list = new ArrayList<>();
		try {
			manager = emf.createEntityManager();
			manager.getTransaction().begin();
			String query = "SELECT u.password, u.name from LoginEntity u where u.email=:email";
			Query q = (Query) manager.createQuery(query);
			q.setParameter("email", email);
			Object[] result = (Object[]) q.getSingleResult();
			String password = (String) result[0];
			String name = (String) result[1];
			list.add(password);
			list.add(name);
			return list;
		}
		catch(Exception e) {
			System.out.println("Caught error in getting password: " + e.getMessage());
			return list;
		}
		finally {
			if(manager!=null)
				manager.close();
		}
	}

	@Override
	public LoginEntity getUserLoginEntity(String email) {
		LoginEntity user = null;
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			user = em.find(LoginEntity.class, email);
			if (user != null) {
				user.getIncomes().size();
		    }
			return user;
		}
		catch(Exception e){
	        System.err.println("Error fetching user login entity with email " + email + ": " + e.getMessage());
			return null;
		}
		finally {
			if(em.isOpen())
				em.close();
		}
	}
}
