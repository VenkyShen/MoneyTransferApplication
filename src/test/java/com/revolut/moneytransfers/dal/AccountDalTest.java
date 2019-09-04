package com.revolut.moneytransfers.dal;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.AccountStatus;
import com.revolut.moneytransfer.model.AccountType;
import com.revolut.moneytransfer.model.CurrencyType;

public class AccountDalTest {

	public static final AccountDal accountDal = new AccountDal();

	@Before
	public final void setup() {
		RepoHelper.populateTestData();
	}

	@Test
	public void testGetAllAccounts() throws Exception {
		List<Account> allAccounts = accountDal.getAllAccounts();
		assertTrue(allAccounts.size() > 1);
	}

	@Test
	public void testGetAccountById() throws Exception {
		Account account = accountDal.getAccountById(1L);
		assertTrue(account.getUserId() == 1);
	}

	@Test
	public void testGetInvalidAccById() throws Exception {
		Account account = accountDal.getAccountById(100L);
		assertTrue(account == null);
	}

	@Test
	public void testCreateAccount() throws Exception {
		BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
		Account a = new Account(null, AccountType.CURRENT, 1l, balance, CurrencyType.DOLLAR, AccountStatus.OPEN,
				new Date(new java.util.Date().getTime()), new Date(new java.util.Date().getTime()));
		long aid = accountDal.createAccount(a);
		Account afterCreation = accountDal.getAccountById(aid);
		assertTrue(afterCreation.getType().equals(AccountType.CURRENT));
		assertTrue(afterCreation.getBalance().equals(balance));
	}

	@Test
	public void testDeleteAccount() throws Exception {
		int rowCount = accountDal.deleteAccountById(2L);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(accountDal.getAccountById(2L) == null);
	}

	@Test
	public void testDeleteNonExistingAccount() throws Exception {
		int rowCount = accountDal.deleteAccountById(500L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

	@Test
	public void testUpdateAccountBalanceSufficientFund() throws Exception {

		BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdated = accountDal.updateAccountBalance(1L, deltaDeposit);
		assertTrue(rowsUpdated == 1);
		assertTrue(accountDal.getAccountById(1L).getBalance().equals(afterDeposit));
		BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = accountDal.updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 1);
		assertTrue(accountDal.getAccountById(1L).getBalance().equals(afterWithDraw));

	}

	@Test(expected = Exception.class)
	public void testUpdateAccountBalanceNotEnoughFund() throws Exception {
		BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = accountDal.updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 0);

	}

}
