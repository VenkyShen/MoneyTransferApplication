package com.revolut.moneytransfers.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author venky
 *
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class PingController {


	@GET
	@Path("/ping")
	public Response ping() throws Exception {
		return Response.status(Response.Status.OK.getStatusCode(), "It works!").build();
	}
	    
	
}
