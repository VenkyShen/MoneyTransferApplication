package com.revolut.moneytransfer.model;

/**
 * @author venky
 *
 */
import java.math.BigDecimal;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

	private Long id;

	private Long fromAccount;

	private Long toAccount;

	private BigDecimal amount;

	private CurrencyType currencyType;

	private TransactionStatus status;

	private Date createdAt;

	private Date updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Long fromAccount) {
		this.fromAccount = fromAccount;
	}

	public Long getToAccount() {
		return toAccount;
	}

	public void setToAccount(Long toAccount) {
		this.toAccount = toAccount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
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

	public Transaction(Long id, Long fromAccount, Long toAccount, BigDecimal amount, CurrencyType currencyType,
			TransactionStatus status, Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
		this.currencyType = currencyType;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Transaction(Long id, Long fromAccount, Long toAccount, BigDecimal amount, String currencyType,
			String status, Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
		this.currencyType = CurrencyType.valueOf(currencyType);
		this.status = TransactionStatus.valueOf(status);
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Transaction() {}
	
	@Override
	public String toString() {
		return "Transaction [id=" + id + ", fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", amount="
				+ amount + ", currencyType=" + currencyType + ", status=" + status + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}

}
