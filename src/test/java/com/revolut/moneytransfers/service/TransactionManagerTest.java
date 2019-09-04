package com.revolut.moneytransfers.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.CurrencyType;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionStatus;
import com.revolut.moneytransfers.dal.AccountDal;
import com.revolut.moneytransfers.dal.RepoHelper;

/**
 * @author venky
 *
 */
public class TransactionManagerTest {

	private final Logger log = Logger.getLogger(TransactionManagerTest.class);

	private static final int THREADS_COUNT = 100;

	public final TransactionManager transactionManager = new TransactionManager();

	public final AccountDal accountDal = new AccountDal();

	@Before
	public final void setup() {
		RepoHelper.populateTestData();
	}

	@Test
	public void testTransfer() throws Exception {
		BigDecimal amount = BigDecimal.valueOf(10);
		Transaction transaction = new Transaction(null, 1l, 3l, amount, CurrencyType.EURO, TransactionStatus.INITIATED,
				new java.sql.Date(new Date().getTime()), new java.sql.Date(new Date().getTime()));
		TransactionStatus transactionStatus = transactionManager.transferAccountBalance(transaction);
		assertTrue(transactionStatus.equals(TransactionStatus.SUCCESS));
	}

	public void testMultiThreadtransfer() throws Exception {

		/*
		 * transfer a total of 200 EURO from 100 EURO balance in multi-threaded mode,
		 * expect half of the transaction fail
		 */
		final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
		for (int i = 0; i < THREADS_COUNT; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						BigDecimal amount = BigDecimal.valueOf(2);
						Transaction transaction = new Transaction(null, 1l, 2l, amount, CurrencyType.EURO,
								TransactionStatus.INITIATED, new java.sql.Date(new Date().getTime()),
								new java.sql.Date(new Date().getTime()));
						TransactionStatus transactionStatus = transactionManager.transferAccountBalance(transaction);
					} catch (Exception e) {
						log.error("Error occurred during transfer ", e);
					} finally {
						latch.countDown();
					}
				}
			}).start();
		}

		latch.await();

		Account accountFrom = accountDal.getAccountById(1);
		Account accountTo = accountDal.getAccountById(2);
		log.debug("Account From: " + accountFrom);
		log.debug("Account From: " + accountTo);
		assertTrue(accountFrom.getBalance().equals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
		assertTrue(accountTo.getBalance().equals(new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN)));

	}
}
