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

	LocalTunnel tunnel;
	
	@Before
	public void set() {
		tunnel = new LocalTunnel(username, apikey);
		try {
			tunnel.start();
		} catch (IOException e) {
			Assume.assumeNoException("Could not start the tunnel", e);
		}
	}
	@After
	public void clear() {
		/*
		try {
			tunnel.stop();
		} catch (IOException e) {
			Assume.assumeNoException("Could not stop the tunnel", e);
		} catch (InterruptedException e) {
			Assume.assumeNoException("Could not stop the tunnel", e);
		}
		tunnel = null;
		*/
	}
	/*
    LocalTunnel herp = new LocalTunnel("","","aTunnel");
    
	public void testLaunchAndTestLocalNamedTunnel() {
        Assert.assertTrue( herp.isTunnelRunning );
    }
    public void testLaunchAndTestLocalUnnamedTunnel() {
        LocalTunnel derp = new LocalTunnel("","");

        Assert.assertTrue( derp.isTunnelRunning );
    }
    */
	@Test
	public void testQueryTunnel() {
		System.out.println(tunnel.tunnelProcess.isAlive());
		System.out.println(tunnel.tunnelProcess.exitValue());
		Assert.assertTrue(tunnel.queryTunnel());
	}
    @Test
    public void testGetUnamedTunnelId() {
    	tunnel.queryTunnel();
    	System.out.println(tunnel.isTunnelRunning);
    	if (tunnel.tunnelID <= 0) {
    		Assert.fail("TunnelId = "+tunnel.tunnelID);
    	}
    }
}
