package com.revolut.moneytransfers.dal;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import org.h2.tools.RunScript;


import com.revolut.moneytransfers.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author venky
 *
 */
public class RepoHelper {
	private static final String h2_driver = Utils.getStringProperty("h2_driver");
	private static final String h2_connection_url = Utils.getStringProperty("h2_connection_url");
	private static final String h2_user = Utils.getStringProperty("h2_user");
	private static final String h2_password = Utils.getStringProperty("h2_password");
	private static Logger log = Logger.getLogger(RepoHelper.class);

	public RepoHelper() {
		DbUtils.loadDriver(h2_driver);
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(h2_connection_url, h2_user, h2_password);

	}
	
	public static void populateTestData() {
		log.info("Populating Test User Table and data ..... ");
		Connection conn = null;
		try {
			conn = getConnection();
			RunScript.execute(conn, new FileReader("src/test/resources/test.sql"));
		} catch (SQLException e) {
			log.error("populateTestData(): Error populating user data: ", e);
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			log.error("populateTestData(): Error finding test script file ", e);
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

}
