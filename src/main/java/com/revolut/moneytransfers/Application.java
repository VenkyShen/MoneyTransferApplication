package com.revolut.moneytransfers;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.controller.PingController;
import com.revolut.moneytransfers.controller.TransactionController;
import com.revolut.moneytransfers.controller.UserController;
import com.revolut.moneytransfers.dal.RepoHelper;

/**
 * @author venky
 */
public class Application {

	private static Logger log = Logger.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
		log.info("Populate Data");
		RepoHelper.populateTestData();
		log.info("Data population Done");
		// Host service on jetty
		startService();
		log.info("Server Started Successfully");
		System.out.println("Server Started Successfully");
	}

	private static void startService() throws Exception {
		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");

		/*
		 * servletHolder.setInitParameter("jersey.config.server.provider.packages",
		 * "com.revolut.moneytransfers");
		 */

		servletHolder.setInitParameter("jersey.config.server.provider.classnames",
				UserController.class.getCanonicalName() + "," + AccountController.class.getCanonicalName() + ","
						+ TransactionController.class.getCanonicalName() + ","
						+ PingController.class.getCanonicalName());

		try {
			server.start();
			server.join();
		} finally {
			server.destroy();
		}
	}

}
