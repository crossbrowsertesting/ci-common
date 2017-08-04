package com.crossbrowsertesting.test;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;

import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.crossbrowsertesting.api.Selenium;

/**
 * Unit test for Selenium
 */
public class SeleniumTest extends APITestFactory{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */

	private Selenium se;
	HashMap<String, String> caps;

    @Before
    public void set() {
		se = new Selenium(username, apikey);
		caps = new HashMap<String, String>();
		caps.put("name","CICommonTest");
		caps.put("build", "1.0");
		caps.put("browser", "Safari8");
		caps.put("os", "Mac10.10");
		caps.put("resolution", "1024x768");
    }
    @After
    public void clear() {
    	se = null;
    	caps = null;
    }
    public void runSeleniumTest() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("name", this.caps.get("name"));
        caps.setCapability("build", this.caps.get("build"));
        caps.setCapability("browser_api_name", this.caps.get("browser"));
        caps.setCapability("os_api_name", this.caps.get("os"));
        caps.setCapability("screen_resolution", this.caps.get("resolution"));
        RemoteWebDriver driver = new RemoteWebDriver(new URL("http://" + username + ":" + apikey +"@hub.crossbrowsertesting.com:80/wd/hub"), caps);
        driver.get("http://crossbrowsertesting.github.io/selenium_example_page.html");
        driver.quit();
    }
    public JSONObject getSeTestInfo(String seleniumTestId) throws IOException {
    	String json = se.getRequest().get("/"+seleniumTestId);
    	return new JSONObject(json);
    }

    /*
     * Checks that browsers are not empty
     */
    @SuppressWarnings("deprecation")
	@Test
	public void testGetSeleniumBrowser() {
        Assert.assertTrue( !se.operatingSystems.isEmpty() );
        Assert.assertTrue( !se.operatingSystems2.isEmpty() );
        Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").getApiName(),"iPadAir-iOS8Sim");
        Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").getName(),"iPad Air / 8.1 Simulator");

    }
    
    @Test
    public void testGetSeleniumTestInfo() {
    	try {
			Queue<Map<String, String>> allSeTestsInfo = se.getSeleniumTestInfo2("Jenkins Selenium Demo", "5", "Chrome35", "Mac10.9", "1024x768");
			Assert.assertEquals(allSeTestsInfo.size(), 1);
			Map<String, String> seTest = allSeTestsInfo.poll();
			Assert.assertEquals("6800215", seTest.get("selenium_test_id"));
			Assert.assertEquals("https://app.crossbrowsertesting.com/public/ie63a397dda49cbe/selenium/6800215", seTest.get("show_result_public_url"));
		} catch (IOException e) {
			Assert.fail("Caught Exception");
		}
    }
    @Test
    public void testGetSeleniumTestId() {
		// Let's run a test just to make sure that we have least one test out there
		try {
			runSeleniumTest();
		} catch (MalformedURLException e) {
			Assume.assumeNoException("Could not start selenium test", e);
		}
    	try {
    		String testId = "";
    		// Case 1
			testId = se.getSeleniumTestId("Jenkins Selenium Demo", "5", "Chrome35", "Mac10.9", "1024x768");
			// 1st case should return that exact id though it already has the client_platform set
			Assert.assertEquals("6800215", testId);
			// Case 2
			testId = se.getSeleniumTestId("Jenkins Demo", "5", "Chrome35", "Mac10.9", "1024x768");
			// 2nd case should return the latest id that doesnt have jenkins in the client_platform
			Assert.assertTrue(!testId.isEmpty());
			Assert.assertNotEquals("6800215", testId);
			// Case 3
			String thirdTestId = se.getSeleniumTestId("FakeNotRealAtAllTest", "-9", "FakeBrowser", "FakeOS", "FakeResolution");
			// 3rd case should return the same id as the 2nd case
			Assert.assertTrue(!thirdTestId.isEmpty());
			Assert.assertEquals(testId, thirdTestId);
		} catch (IOException e) {
			Assert.fail("Caught Exception");
		}	
    }
    
    @Test
    public void testMarkPassOrFail() {
    	try {
			JSONObject seTest = getSeTestInfo("6800215");
			String originalTestScore = seTest.getString("test_score");
			if (originalTestScore.equals("pass")) {
				// lets try to set it to fail
				se.markPassOrFail("6800215", false);
			}else {
				// if its currently unset or fail, lets try to set it to pass
				se.markPassOrFail("6800215", true);
			}
			// now lets check
			seTest = getSeTestInfo("6800215");
			String newTestScore = seTest.getString("test_score");
			Assert.assertNotEquals(originalTestScore, newTestScore);
		} catch (IOException e) {
			Assert.fail("Caught Exception when trying to get the selenium test info");
		}    	
    }
}
