package com.crossbrowsertesting.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class APITestFactory {
	public static String username,
					apikey = null;
	
	@BeforeClass
    public static void getEnvironmentVariables() {
    	username = System.getenv("CBT_USERNAME");
    	apikey = System.getenv("CBT_AUTHKEY");
		Assume.assumeNotNull(username, apikey);
    }
	@AfterClass
	public static void clearCredentials() {
		username = null;
		apikey = null;
	}
	@Before
	public abstract void set();
	@After
	public abstract void clear();

}
