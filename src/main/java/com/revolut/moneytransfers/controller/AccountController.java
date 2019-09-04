package com.revolut.moneytransfers.controller;

/**
 * @author venky
 *
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfers.dal.AccountDal;
import com.revolut.moneytransfers.utils.MoneyUtils;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {
	
	private final Logger log = Logger.getLogger(AccountController.class);
	
    private final AccountDal accountDal = new AccountDal();
    
    @GET
    @Path("/all")
    public Response getAllAccounts() throws Exception {
        List<Account> allAccounts = accountDal.getAllAccounts();
        GenericEntity<List<Account>> entity = new GenericEntity<List<Account>>(allAccounts) {};
        return Response.ok(entity).build();
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") long accountId) throws Exception {
    	Account accountById = accountDal.getAccountById(accountId);
    	GenericEntity<Account> entity = new GenericEntity<Account>(accountById) {};
    	return Response.ok(entity).build();
    }
    
    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") long accountId) throws Exception {
        final Account account = accountDal.getAccountById(accountId);

        if(account == null){
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getBalance();
    }
    
    @PUT
    @Path("/create")
    public Response createAccount(Account account) throws Exception {
        final long accountId = accountDal.createAccount(account);
        Account accountById = accountDal.getAccountById(accountId);
		GenericEntity<Account> entity = new GenericEntity<Account>(accountById) {};
        return Response.ok(entity).build();
    }

    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public Response deposit(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount) throws Exception {
        if (amount.compareTo(MoneyUtils.ZERO) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }

        accountDal.updateAccountBalance(accountId,amount.setScale(4, RoundingMode.HALF_EVEN));
        Account accountById = accountDal.getAccountById(accountId);
        GenericEntity<Account> entity = new GenericEntity<Account>(accountById) {};
        return Response.ok(entity).build();
    }

    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public Response withdraw(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount) throws Exception {

        if (amount.compareTo(MoneyUtils.ZERO) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        if (log.isDebugEnabled())
            log.debug("Withdraw service: delta change to account  " + delta + " Account ID = " +accountId);
        accountDal.updateAccountBalance(accountId,delta.setScale(4, RoundingMode.HALF_EVEN));
        Account accountById = accountDal.getAccountById(accountId);
        GenericEntity<Account> entity = new GenericEntity<Account>(accountById) {};
        return Response.ok(entity).build();
    }


    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId) throws Exception {
        int deleteCount = accountDal.deleteAccountById(accountId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
