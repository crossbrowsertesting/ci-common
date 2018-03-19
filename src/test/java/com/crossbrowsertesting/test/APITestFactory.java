package com.crossbrowsertesting.test;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;

public abstract class APITestFactory {
	public static String username;
	public static String apikey;


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
}
