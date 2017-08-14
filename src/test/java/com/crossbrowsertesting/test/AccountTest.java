package com.crossbrowsertesting.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.crossbrowsertesting.api.Account;

public class AccountTest extends APITestFactory{

	private static Account a;

	@Test
	public void testAccount() {
		a.testConnection();
		Assert.assertTrue(a.connectionSuccessful);
	}

	@BeforeClass
	public static void set() {
		a = new Account(username, apikey);
	}

	@AfterClass
	public static void clear() {
		a = null;	
	}

}
