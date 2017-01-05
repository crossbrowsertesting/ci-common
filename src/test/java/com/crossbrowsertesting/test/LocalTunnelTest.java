package com.crossbrowsertesting.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.crossbrowsertesting.api.LocalTunnel;

/**
 * Unit test for Selenium
 */
public class LocalTunnelTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    LocalTunnel herp = new LocalTunnel("tunneluser1","password","aTunnelName");
    LocalTunnel derp = new LocalTunnel("tunneluser1","password");
	
    public LocalTunnelTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( LocalTunnelTest.class );
    }

    /*
     * Checks that browsers are not empty
     */
    @SuppressWarnings("deprecation")
	public void testLaunchAndTestLocalNamedTunnel() {
        assertTrue( herp.isTunnelRunning );
    }
    public void testLaunchAndTestLocalUnnamedTunnel() {
        assertTrue( derp.isTunnelRunning );
    }
}
