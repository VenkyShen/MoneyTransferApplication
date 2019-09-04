package com.revolut.moneytransfers.controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionStatus;
import com.revolut.moneytransfers.service.TransactionManager;

/**
 * @author venky
 *
 */
@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)

public class TransactionController {

	private final TransactionManager transactionManager = new TransactionManager();

	@POST
	public Response transferFund(Transaction transaction) throws Exception {
		TransactionStatus status = transactionManager.transferAccountBalance(transaction);
		if (TransactionStatus.SUCCESS == status) {
			return Response.status(Response.Status.OK).build();
		} else {
			// transaction failed
			throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
		}
	}

}
