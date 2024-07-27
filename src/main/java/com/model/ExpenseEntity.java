package com.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

import com.login.LoginEntity;


@Entity
@Table(name = "expense")
public class ExpenseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private LoginEntity user;

    @Column(name = "allocated_month", nullable = false)
    private short allocatedMonth;

    @Column(name = "allocated_year", nullable = false)
    private short allocatedYear;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private ExpenseCatalogEntity category;

    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Column(name = "created_at", nullable = true, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
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

	// Getters and Setters
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

    public short getAllocatedMonth() {
        return allocatedMonth;
    }

    public void setAllocatedMonth(short allocatedMonth) {
        this.allocatedMonth = allocatedMonth;
    }

    public short getAllocatedYear() {
        return allocatedYear;
    }

    public void setAllocatedYear(short allocatedYear) {
        this.allocatedYear = allocatedYear;
    }

    public ExpenseCatalogEntity getCategory() {
        return category;
    }

    public void setCategory(ExpenseCatalogEntity category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Transient
	public String getMonthName(short index) {
		return Month.fromIndex(index).name();
	}
	@Transient
	public String getFormattedTimeStamp(LocalDateTime now) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
        String formatDateTime = now.format(format);  
        return formatDateTime;
	}
}

