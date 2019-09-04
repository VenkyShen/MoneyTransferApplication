package com.revolut.moneytransfer.model;

import java.math.BigDecimal;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author venky
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

	private Long id;

	private AccountType type;

	private Long userId;

	private BigDecimal balance;

	private CurrencyType currencyType;

	private AccountStatus status;

	private Date createdAt;

	private Date updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Account(Long id, AccountType type, Long userId, BigDecimal balance, CurrencyType currencyType,
			AccountStatus status, Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.type = type;
		this.userId = userId;
		this.balance = balance;
		this.currencyType = currencyType;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Account(Long id, AccountType type, Long userId, BigDecimal balance, String currencyType,
			String status, Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.type = type;
		this.userId = userId;
		this.balance = balance;
		this.currencyType = CurrencyType.valueOf(currencyType);
		this.status = AccountStatus.valueOf(status);
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Account() {}

	@Override
	public String toString() {
		return "Account [id=" + id + ", type=" + type + ", userId=" + userId + ", balance=" + balance
				+ ", currencyType=" + currencyType + ", status=" + status + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}

}
