package com.crossbrowsertesting.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.crossbrowsertesting.api.Selenium;

/**
 * Unit test for Selenium
 */
public class SeleniumTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
	Selenium se = new Selenium();
	
    public SeleniumTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SeleniumTest.class );
    }

    /*
     * Checks that browsers are not empty
     */
    @SuppressWarnings("deprecation")
	public void testGetSeleniumBrowser() {
        assertTrue( !se.operatingSystems.isEmpty() );
        assertTrue( !se.operatingSystems2.isEmpty() );
    }
}
