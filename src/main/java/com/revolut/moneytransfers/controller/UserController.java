package com.revolut.moneytransfers.controller;

import com.revolut.moneytransfers.dal.UserDal;
import com.revolut.moneytransfer.model.User;

import org.apache.log4j.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author venky
 *
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

	private final UserDal userDal = new UserDal();

	private static Logger log = Logger.getLogger(UserController.class);

	
	@GET
	@Path("/{userName}")
	public Response getUserByName(@PathParam("userName") String userName) throws Exception {
		if (log.isDebugEnabled())
			log.debug("Request Received for get User by Name " + userName);
		final User user = userDal.getUserByName(userName);
		if (user == null) {
			throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
		}
		GenericEntity<User> entity = new GenericEntity<User>(user){};
	    return Response.ok(entity).build();
	}

	@GET
	@Path("/all-users")
	public Response getAllUser() throws Exception {
		List<User> allUsers = userDal.getAllUsers();
		GenericEntity<List<User>> entity = new GenericEntity<List<User>>(allUsers){};
		return Response.ok(entity).build();
	}

	@POST
	@Path("/create")
	public Response createUser(User user) throws Exception {
		if (userDal.getUserByName(user.getName()) != null) {
			throw new WebApplicationException("User name already exist", Response.Status.BAD_REQUEST);
		}
		final long uId = userDal.insertUser(user);
		User userById = userDal.getUserById(uId);
		GenericEntity<User> entity = new GenericEntity<User>(userById){};
	    return Response.ok(entity).build();
	}

	@PUT
	@Path("/{userId}")
	public Response updateUser(@PathParam("userId") long userId, User user) throws Exception {
		final int updateCount = userDal.updateUser(userId, user);
		if (updateCount == 1) {
			return Response.status(Response.Status.OK).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("/{userId}")
	public Response deleteUser(@PathParam("userId") long userId) throws Exception {
		int deleteCount = userDal.deleteUser(userId);
		if (deleteCount == 1) {
			return Response.status(Response.Status.OK).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
