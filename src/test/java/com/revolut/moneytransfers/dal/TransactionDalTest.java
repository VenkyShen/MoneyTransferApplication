package com.revolut.moneytransfers.dal;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.revolut.moneytransfer.model.CurrencyType;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionStatus;

public class TransactionDalTest {

	public static final TransactionDal transactionDal = new TransactionDal();

	@Before
	public final void setup() {
		RepoHelper.populateTestData();
	}

	@Test
	public void testGetAllTransactions() throws Exception {
		List<Transaction> allTransactions = transactionDal.getAllTransactions();
		assertTrue(allTransactions.size() > 1);
	}

	@Test
	public void testGetTransactionById() throws Exception {
		Transaction transaction = transactionDal.getTransactionById(2L);
		assertTrue(transaction.getId() == 2);
	}

	@Test
	public void testGetNonExistingUserById() throws Exception {
		Transaction t = transactionDal.getTransactionById(500L);
		assertTrue(t == null);
	}

	@Test
	public void testCreateTransaction() throws Exception {
		BigDecimal balance = BigDecimal.valueOf(10);
		Transaction transaction = new Transaction(null, 1l, 2l, balance, CurrencyType.DOLLAR,
				TransactionStatus.INITIATED, new Date(new java.util.Date().getTime()),
				new Date(new java.util.Date().getTime()));
		long id = transactionDal.createTransaction(transaction);
		Transaction tAfterInsert = transactionDal.getTransactionById(id);
		assertTrue(TransactionStatus.INITIATED.name().equals(tAfterInsert.getStatus().name()));
	}

	@Test
	public void testUpdateTransaction() throws Exception {
		int rowCount = transactionDal.updateTransaction(1L, TransactionStatus.FAILED);
		// assert one row(user) updated
		assertTrue(rowCount == 1);
		assertTrue(transactionDal.getTransactionById(1L).getStatus().equals(TransactionStatus.FAILED));
	}

	@Test
	public void testUpdateNonExistingUser() throws Exception {
		int rowCount = transactionDal.updateTransaction(500L, TransactionStatus.FAILED);
		assertTrue(rowCount == 0);
	}

}
