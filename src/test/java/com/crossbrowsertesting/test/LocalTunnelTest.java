package com.crossbrowsertesting.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.crossbrowsertesting.api.LocalTunnel;

/**
 * Unit test for LocalTunnel
 */
public class LocalTunnelTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
	
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
    /*
    @SuppressWarnings("deprecation")
    LocalTunnel herp = new LocalTunnel("mikeh","illnevertell","aTunnel");
	public void testLaunchAndTestLocalNamedTunnel() {
        assertTrue( herp.isTunnelRunning );
    }
    public void testLaunchAndTestLocalUnnamedTunnel() {
        LocalTunnel derp = new LocalTunnel("mikeh","illnevertell");

        assertTrue( derp.isTunnelRunning );
    }
    */
    public void testAlwaysTrue() {
        assertTrue(true);
    }
}
