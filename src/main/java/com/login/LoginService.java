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
	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("budgetTracker");

	@Override
	public List<String> verifyUserLoginDetails(String email, String password) {
		String correctPassword = getPassword(email);
		List<String> response = new ArrayList<>();
		String message = null , status = null;
		if(correctPassword == null) {
			message = "Invalid email or user not found";
			status = "error";
		}
		else {
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
		return response;
	}

	@Override
	public String getPassword(String email) {
		EntityManager manager = null;
		try {
			manager = emf.createEntityManager();
			manager.getTransaction().begin();
			String query = "SELECT u.password from LoginEntity u where u.email=:email";
			Query q = (Query) manager.createQuery(query);
			q.setParameter("email", email);
			String password = (String) q.getSingleResult();
			return password;
		}
		catch(Exception e) {
			System.out.println("Caught error in getting password: " + e.getMessage());
			return null;
		}
		finally {
			if(manager!=null)
				manager.close();
		}
	}
}
