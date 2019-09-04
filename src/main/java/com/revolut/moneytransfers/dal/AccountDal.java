package com.revolut.moneytransfers.dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.AccountStatus;
import com.revolut.moneytransfer.model.AccountType;
import com.revolut.moneytransfer.model.CurrencyType;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfers.utils.MoneyUtils;

/**
 * @author venky
 *
 */
public class AccountDal extends RepoHelper {

	private static Logger log = Logger.getLogger(AccountDal.class);
	private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE id = ? ";
	private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE id = ? FOR UPDATE";
	private final static String SQL_CREATE_ACC = "INSERT INTO Account (acc_type, user_id, balance, currency_type, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET balance = ? WHERE id = ? ";
	private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";
	private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM Account WHERE id = ?";

	/**
	 * Get all accounts.
	 */
	public List<Account> getAllAccounts() throws Exception {
		Connection connection = getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(SQL_GET_ALL_ACC);
			return fetchData(stmt);
		} finally {
			DbUtils.closeQuietly(connection);
		}

	}

	/**
	 * Get account by id
	 */
	public Account getAccountById(long accountId) throws Exception {
		Connection connection = getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(SQL_GET_ACC_BY_ID);
			stmt.setLong(1, accountId);
			List<Account> accounts = fetchData(stmt);
			if (accounts.isEmpty()) {
				return null;
			}
			return accounts.get(0);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	private List<Account> fetchData(PreparedStatement stmt) throws Exception {
		ResultSet rs = null;
		try {
			List<Account> accounts = new ArrayList<Account>();
			rs = stmt.executeQuery();
			while (rs.next()) {
				Account acc = getAccountFromResultSet(rs);
				accounts.add(acc);
				if (log.isDebugEnabled())
					log.debug("getAllUsers() Retrieve Acc: " + acc);
			}
			return accounts;
		} catch (SQLException e) {
			throw new Exception("Error reading acc data", e);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
	}

	/**
	 * Create account
	 */
	public long createAccount(Account account) throws Exception {
		Connection connection = getConnection();
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		try {
			stmt = connection.prepareStatement(SQL_CREATE_ACC);
			stmt.setString(1, account.getType().name());
			stmt.setLong(2, account.getUserId());
			stmt.setBigDecimal(3, account.getBalance());
			stmt.setString(4, account.getCurrencyType().name());
			stmt.setString(5, account.getStatus().name());
			stmt.setDate(6, account.getCreatedAt());
			stmt.setDate(7, account.getUpdatedAt());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("createAccount(): Creating account failed, no rows affected.");
				throw new Exception("Account Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("Creating account failed, no ID obtained.");
				throw new Exception("Account Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting Account  " + account);
			throw new Exception("createAccount(): Error creating user account " + account, e);
		} finally {
			DbUtils.closeQuietly(connection, stmt, generatedKeys);
		}
	}

	/**
	 * Delete account by id
	 */
	public int deleteAccountById(long accountId) throws Exception {
		Connection connection = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(SQL_DELETE_ACC_BY_ID);
			stmt.setLong(1, accountId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new Exception("deleteAccountById(): Error deleting user account Id " + accountId, e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(stmt);
		}
	}

	/**
	 * Update account balance
	 */
	public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws Exception {
		Connection connection = getConnection();
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account targetAccount = null;
		int updateCount = -1;
		try {
			connection.setAutoCommit(false);
			// lock account for writing:
			lockStmt = connection.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, accountId);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				targetAccount = getAccountFromResultSet(rs);
				if (log.isDebugEnabled())
					log.debug("updateAccountBalance from Account: " + targetAccount);
			}

			if (targetAccount == null) {
				throw new Exception("updateAccountBalance(): fail to lock account : " + accountId);
			}
			// update account upon success locking
			BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
			if (balance.compareTo(MoneyUtils.ZERO) < 0) {
				throw new Exception("Not sufficient Fund for account: " + accountId);
			}

			updateStmt = connection.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, balance);
			updateStmt.setLong(2, accountId);
			updateCount = updateStmt.executeUpdate();
			connection.commit();
			if (log.isDebugEnabled())
				log.debug("New Balance after Update: " + targetAccount);
			return updateCount;
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountId, se);
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException re) {
				throw new Exception("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return updateCount;
	}

	/**
	 * Transfer balance between two accounts.
	 */
	public int transferAccountBalance(Transaction transaction) throws Exception {
		int result = -1;
		Connection connection = getConnection();
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account fromAccount = null;
		Account toAccount = null;

		try {
			
			connection.setAutoCommit(false);
			// lock the credit and debit account for writing:
			lockStmt = connection.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, transaction.getFromAccount());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				fromAccount = getAccountFromResultSet(rs);
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance from Account: " + fromAccount);
			}
			lockStmt = connection.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, transaction.getToAccount());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				toAccount = getAccountFromResultSet(rs);
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance to Account: " + toAccount);
			}

			BigDecimal debitAmount = MoneyUtils.doCurrencyConversion(transaction, fromAccount.getCurrencyType());
			BigDecimal creditAmount = MoneyUtils.doCurrencyConversion(transaction, toAccount.getCurrencyType());
			System.out.println("Debit : " + debitAmount + " --  Credit : " + creditAmount);


			// check enough fund in source account
			BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(debitAmount);
			if (fromAccountLeftOver.compareTo(MoneyUtils.ZERO) < 0) {
				throw new Exception("Not enough Fund from source Account ");
			}
			// proceed with update
			updateStmt = connection.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, fromAccountLeftOver);
			updateStmt.setLong(2, transaction.getFromAccount());
			updateStmt.addBatch();
			updateStmt.setBigDecimal(1, toAccount.getBalance().add(creditAmount));
			updateStmt.setLong(2, transaction.getToAccount());
			updateStmt.addBatch();
			int[] rowsUpdated = updateStmt.executeBatch();
			result = rowsUpdated[0] + rowsUpdated[1];
			if (log.isDebugEnabled()) {
				log.debug("Number of rows updated for the transfer : " + result);
			}
			// If there is no error, commit the transaction
			connection.commit();
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + transaction,
					se);
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException re) {
				throw new Exception("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return result;
	}

	private Account getAccountFromResultSet(ResultSet rs) throws SQLException {
		return new Account(rs.getLong("id"), AccountType.valueOf(rs.getString("acc_type")),
				rs.getLong("user_id"), rs.getBigDecimal("balance"),
				CurrencyType.valueOf(rs.getString("currency_type")),
				AccountStatus.valueOf(rs.getString("status")), rs.getDate("created_at"),
				rs.getDate("updated_at"));
	}

}
