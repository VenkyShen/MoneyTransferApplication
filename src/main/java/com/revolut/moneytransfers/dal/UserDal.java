package com.revolut.moneytransfers.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.revolut.moneytransfer.model.User;

/**
 * @author venky
 *
 */
public class UserDal extends RepoHelper {

	private static Logger log = Logger.getLogger(UserDal.class);

	private final static String SQL_GET_USER_BY_ID = "SELECT * FROM User WHERE id = ? ";
	private final static String SQL_GET_ALL_USERS = "SELECT * FROM User";
	private final static String SQL_GET_USER_BY_NAME = "SELECT * FROM User WHERE name = ? ";
	private final static String SQL_INSERT_USER = "INSERT INTO User (name, mail, created_at, updated_at) VALUES (?, ?, ?, ?)";
	private final static String SQL_UPDATE_USER = "UPDATE User SET name = ?, mail = ?, updated_at = ? WHERE id = ? ";
	private final static String SQL_DELETE_USER_BY_ID = "DELETE FROM User WHERE id = ? ";

	public List<User> getAllUsers() throws Exception {
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement(SQL_GET_ALL_USERS);
		return fetchData(stmt);
	}

	public User getUserById(long userId) throws Exception {
		Connection connection = getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(SQL_GET_USER_BY_ID);
			stmt.setLong(1, userId);
			List<User> users = fetchData(stmt);
			if (users.isEmpty()) {
				return null;
			}
			return users.get(0);
		}
		finally {
			DbUtils.closeQuietly(connection);
		}
	}

	public User getUserByName(String userName) throws Exception {
		Connection connection = getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(SQL_GET_USER_BY_NAME);
			stmt.setString(1, userName);
			
			List<User> users = fetchData(stmt);
			if (users.isEmpty()) {
				return null;
			}
			return users.get(0);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	private List<User> fetchData(PreparedStatement stmt) throws Exception {
		ResultSet rs = null;
		try {
			List<User> users = new ArrayList<User>();
			rs = stmt.executeQuery();
			while (rs.next()) {
				User u = new User(rs.getLong("id"), rs.getString("name"), rs.getString("mail"),
						rs.getDate("created_at"), rs.getDate("updated_at"));
				users.add(u);
				if (log.isDebugEnabled())
					log.debug("getAllUsers() Retrieve User: " + u);
			}
			return users;
		} catch (SQLException e) {
			throw new Exception("Error reading user data", e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
		}
	}

	public long insertUser(User user) throws Exception {
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		Connection connection = getConnection();
		try {
			stmt = connection.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getMail());
			stmt.setDate(3, getSqlDate());
			stmt.setDate(4, getSqlDate());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("insertUser(): Creating user failed, no rows affected." + user);
				throw new Exception("Users Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("insertUser():  Creating user failed, no ID obtained." + user);
				throw new Exception("Users Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting User :" + user);
			throw new Exception("Error creating user data", e);
		} finally {
			DbUtils.closeQuietly(connection, stmt, generatedKeys);
		}

	}

	public int updateUser(Long userId, User user) throws Exception {
		PreparedStatement stmt = null;
		Connection connection = getConnection();
		try {
			stmt = connection.prepareStatement(SQL_UPDATE_USER);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getMail());
			stmt.setDate(3, getSqlDate());
			stmt.setLong(4, userId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			log.error("Error Updating User :" + user);
			throw new Exception("Error update user data", e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(stmt);
		}
	}

	public int deleteUser(long userId) throws Exception {
		PreparedStatement stmt = null;
		Connection connection = getConnection();
		try {
			stmt = connection.prepareStatement(SQL_DELETE_USER_BY_ID);
			stmt.setLong(1, userId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			log.error("Error Deleting User :" + userId);
			throw new Exception("Error Deleting User ID:" + userId, e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(stmt);
		}
	}

	private java.sql.Date getSqlDate() {
		return new java.sql.Date(new Date().getTime());
	}

}
