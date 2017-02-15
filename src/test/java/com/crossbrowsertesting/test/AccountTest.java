package com.crossbrowsertesting.test;

import org.junit.Assert;
import org.junit.Test;
import com.crossbrowsertesting.api.Account;

public class AccountTest extends APITestFactory{

	private Account a;

	@Test
	public void testAccount() {
		a.testConnection();
		Assert.assertTrue(a.connectionSuccessful);
	}

	@Override
	public void set() {
		a = new Account(username, apikey);
	}

	@Override
	public void clear() {
		a = null;	
	}

}
