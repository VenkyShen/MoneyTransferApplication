package com.revolut.moneytransfers.service;

import org.apache.log4j.Logger;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionStatus;
import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.dal.AccountDal;
import com.revolut.moneytransfers.dal.TransactionDal;
import com.revolut.moneytransfers.utils.Utils;

/**
 * @author venky
 *
 */
public class TransactionManager {

	private final Logger log = Logger.getLogger(AccountController.class);

	private final TransactionDal transactionDal = new TransactionDal();

	private final AccountDal accountDal = new AccountDal();

	public TransactionStatus transferAccountBalance(Transaction transaction) throws Exception {
		log.info("Transaction initiated : " + transaction.toString());
		transaction.setStatus(TransactionStatus.INITIATED);
		long transactionId = transactionDal.createTransaction(transaction);
		int updateCount = 0;
		Account fromAccount = accountDal.getAccountById(transaction.getFromAccount());
		if (Utils.validAccount(fromAccount)) {
			Account toAccount = accountDal.getAccountById(transaction.getToAccount());
			if (Utils.validAccount(toAccount)) {
				updateCount = accountDal.transferAccountBalance(transaction);
			}
		}
		TransactionStatus transactionStatus = updateTransactionStatus(updateCount, transactionId);
		return transactionStatus;

	}

	private TransactionStatus updateTransactionStatus(int updateCount, long transactionId) throws Exception {
		TransactionStatus status = updateCount == 2 ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;
		transactionDal.updateTransaction(transactionId, status);
		log.info("Transaction Status : " + status);
		return status;
	}

}
