package com.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.login.LoginEntity;

@Entity
@Table(name = "income")
public class IncomeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "user_email", nullable = false)
    private LoginEntity user;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;
    
    @Column(name = "amount", nullable = false, precision = 20, scale = 3)
    private BigDecimal amount;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "allocated_month", nullable = false)
    private int allocatedMonth;

    @Column(name = "allocated_year", nullable = false)
    private int allocatedYear;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LoginEntity getUser() {
		return user;
	}

	public void setUser(LoginEntity user) {
		this.user = user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAllocatedMonth() {
		return allocatedMonth;
	}

	public void setAllocatedMonth(int allocatedMonth) {
		this.allocatedMonth = allocatedMonth;
	}

	public int getAllocatedYear() {
		return allocatedYear;
	}

	public void setAllocatedYear(int allocatedYear) {
		this.allocatedYear = allocatedYear;
	}
    
}
