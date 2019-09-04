package com.revolut.moneytransfers.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.revolut.moneytransfer.model.CurrencyType;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionStatus;

/**
 * @author venky
 *
 */
public class TransactionDal extends RepoHelper{
	
	private static Logger log = Logger.getLogger(TransactionDal.class);
	
	private final static String SQL_GET_ALL_TRANSACTION = "SELECT * FROM transaction";
	private final static String SQL_GET_TRANSACTION_BY_ID = "SELECT * FROM transaction WHERE id = ? ";
	private final static String SQL_CREATE_TRANSACTION = "INSERT INTO transaction (from_account, to_account, amount, currency_type, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private final static String SQL_UPDATE_TRANSACTION = "UPDATE transaction SET status = ? WHERE id = ? ";
	
	public List<Transaction> getAllTransactions() throws Exception {
		Connection connection = getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(SQL_GET_ALL_TRANSACTION);
			return fetchData(stmt);
		} finally {
			DbUtils.closeQuietly(connection);
		}

	}

	public Transaction getTransactionById(long transactionId) throws Exception {
		Connection connection = getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(SQL_GET_TRANSACTION_BY_ID);
			stmt.setLong(1, transactionId);
			List<Transaction> transactions = fetchData(stmt);
			if (transactions.isEmpty()) {
				return null;
			}
			return transactions.get(0);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	private List<Transaction> fetchData(PreparedStatement stmt) throws Exception {
		ResultSet rs = null;
		try {
			List<Transaction> transactions = new ArrayList<Transaction>();
			rs = stmt.executeQuery();
			while (rs.next()) {
				Transaction transaction = getTransactionFromResultSet(rs);
				transactions.add(transaction);
				if (log.isDebugEnabled())
					log.debug("getTransaction -  Retrieve transaction: " + transaction);
			}
			return transactions;
		} catch (SQLException e) {
			throw new Exception("Error reading transaction data", e);
		} finally {
			DbUtils.close(stmt);
			DbUtils.close(rs);
		}
	}

	private Transaction getTransactionFromResultSet(ResultSet rs) throws SQLException {
		return new Transaction(rs.getLong("id"), rs.getLong("from_account"), rs.getLong("to_account"),
				rs.getBigDecimal("amount"), CurrencyType.valueOf(rs.getString("currency_type")),
				TransactionStatus.valueOf(rs.getString("status")), rs.getDate("created_at"), rs.getDate("updated_at"));
	}

	public long createTransaction(Transaction transaction) throws Exception {
		Connection connection = getConnection();
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		try {
			stmt = connection.prepareStatement(SQL_CREATE_TRANSACTION);
			stmt.setLong(1, transaction.getFromAccount());
			stmt.setLong(2, transaction.getToAccount());
			stmt.setBigDecimal(3, transaction.getAmount());
			stmt.setString(4, transaction.getCurrencyType().name());
			stmt.setString(5, transaction.getStatus().name());
			stmt.setDate(6, transaction.getCreatedAt());
			stmt.setDate(7, transaction.getUpdatedAt());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("createTransaction(): Creating transactions failed, no rows affected.");
				throw new Exception("Transaction Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("Creating account failed, no ID obtained.");
				throw new Exception("Account Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting transaction  " + transaction);
			throw new Exception("createAccount(): Error creating user account " + transaction, e);
		} finally {
			DbUtils.closeQuietly(connection, stmt, generatedKeys);
		}
	}

	public int updateTransaction(Long transactionId, TransactionStatus transactionStatus) throws Exception {
		PreparedStatement stmt = null;
		Connection connection = getConnection();
		try {
			stmt = connection.prepareStatement(SQL_UPDATE_TRANSACTION);
			stmt.setString(1, transactionStatus.name());
			stmt.setLong(2, transactionId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			log.error("Error Updating transaction :" + transactionId);
			throw new Exception("Error update transaction data", e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(stmt);
		}
	}

	
	
}
