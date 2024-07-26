package com.model;

import java.io.Serializable;

import javax.persistence.*;

import com.login.LoginEntity;

@Entity
@Table(name = "expense_catalog", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_email", "category"})
})
public class ExpenseCatalogEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private LoginEntity user;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description")
    private String description;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public LoginEntity getUser() {
		return user;
	}

	public void setUser(LoginEntity user) {
		this.user = user;
	}
}
