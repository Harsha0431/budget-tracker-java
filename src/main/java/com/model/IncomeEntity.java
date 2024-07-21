package com.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "allocated_month", nullable = false)
    private int allocatedMonth;

    @Column(name = "allocated_year", nullable = false)
    private int allocatedYear;
    
    public enum Month {
    	January,
        February,
        March,
        April,
        May,
        June,
        July,
        August,
        September,
        October,
        November,
        December;

        public static Month fromIndex(int index) {
            return Month.values()[index - 1];
        }
    }

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
	@Transient
	public String getMonthName(int index) {
		return Month.fromIndex(index).name();
	}
	@Transient
	public String getFormattedTimeStamp(LocalDateTime now) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
        String formatDateTime = now.format(format);  
        return formatDateTime;
	}
}
