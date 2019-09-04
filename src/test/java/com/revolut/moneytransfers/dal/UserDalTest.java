package com.revolut.moneytransfers.dal;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.revolut.moneytransfer.model.User;

public class UserDalTest {

	public static final UserDal userDal = new UserDal();

	@Before
	public final void setup() {
		System.out.println("Before ..... " + new java.util.Date());
		RepoHelper.populateTestData();
	}

	@Test
	public void testGetAllUsers() throws Exception {
		List<User> allUsers = userDal.getAllUsers();
		assertTrue(allUsers.size() > 1);
	}

	@Test
	public void testGetUserById() throws Exception {
		User u = userDal.getUserById(2L);
		assertTrue(u.getName().equalsIgnoreCase("Smith"));
	}

	@Test
	public void testGetNonExistingUserById() throws Exception {
		User u = userDal.getUserById(500L);
		assertTrue(u == null);
	}

	@Test
	public void testGetNonExistingUserByName() throws Exception {
		User u = userDal.getUserByName("abcdeftg");
		assertTrue(u == null);
	}

	@Test
	public void testCreateUser() throws Exception {
		User u = new User(null, "Sachin", "sachin@yahoo.com", new Date(new java.util.Date().getTime()),
				new Date(new java.util.Date().getTime()));
		long id = userDal.insertUser(u);
		User uAfterInsert = userDal.getUserById(id);
		assertTrue(uAfterInsert.getName().equalsIgnoreCase("Sachin"));
		assertTrue(u.getMail().equalsIgnoreCase("sachin@yahoo.com"));
	}

	@Test
	public void testUpdateUser() throws Exception {
		User u = new User(null, "Stokes", "stokes@yahoo.com", new Date(new java.util.Date().getTime()),
				new Date(new java.util.Date().getTime()));
		int rowCount = userDal.updateUser(1L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 1);
		assertTrue(userDal.getUserById(1L).getMail().equals("stokes@yahoo.com"));
	}

	@Test
	public void testUpdateNonExistingUser() throws Exception {
		User u = new User(null, "Sachin", "sachin@yahoo.com", new Date(new java.util.Date().getTime()),
				new Date(new java.util.Date().getTime()));
		int rowCount = userDal.updateUser(500L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 0);
	}

	@Test
	public void testDeleteUser() throws Exception {
		int rowCount = userDal.deleteUser(1L);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(userDal.getUserById(1L) == null);
	}

	@Test
	public void testDeleteNonExistingUser() throws Exception {
		int rowCount = userDal.deleteUser(500L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

}
