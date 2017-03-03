package com.crossbrowsertesting.test;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import com.crossbrowsertesting.api.LocalTunnel;


/**
 * Unit test for LocalTunnel
 */
public class LocalTunnelTest extends APITestFactory {

	private LocalTunnel unnamedTunnel;
	private LocalTunnel namedTunnel;
	private String tunnelName = "TestCICommon";
	
	@Before
	public void set() {
		unnamedTunnel = new LocalTunnel(username, apikey);
		namedTunnel = new LocalTunnel(username, apikey, tunnelName);
		try {
			unnamedTunnel.start();
			namedTunnel.start();
		} catch (IOException e) {
			Assume.assumeNoException("Could not start the tunnel", e);
		}
	}
	@After
	public void clear() {
	}
	@Test
	public void testQueryTunnel() {
		//System.out.println(unnamedTunnel.tunnelProcess.isAlive());
		//System.out.println(unnamedTunnel.tunnelProcess.exitValue());
		Assert.assertTrue(unnamedTunnel.queryTunnel());
		Assert.assertTrue(namedTunnel.queryTunnel());
	}
    @Test
    public void testGetUnamedTunnelId() {
    	unnamedTunnel.queryTunnel();
    	if (unnamedTunnel.tunnelID <= 0) {
    		Assert.fail("TunnelId = "+unnamedTunnel.tunnelID);
    	}
    	Assert.assertNotEquals(unnamedTunnel.tunnelID, namedTunnel.tunnelID);
    }
    @Test
    public void testGetNameTunnelId() {
    	namedTunnel.queryTunnel();
    	if (namedTunnel.tunnelID <= 0) {
    		Assert.fail("TunnelId = "+namedTunnel.tunnelID);
    	}
    	Assert.assertNotEquals(namedTunnel.tunnelID, unnamedTunnel.tunnelID);
    }
}
