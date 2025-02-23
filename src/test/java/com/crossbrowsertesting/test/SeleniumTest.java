package com.crossbrowsertesting.test;

import com.crossbrowsertesting.api.Selenium;
import org.json.JSONObject;
import org.junit.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Queue;

/**
 * Unit test for Selenium
 */
public class SeleniumTest extends APITestFactory{

	private static Selenium se;

    @BeforeClass
    public static void set() {
		// Let's run a test just to make sure that we have least one test out there
		try {
			se = new Selenium(username, apikey);
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability("name", "CICommonTest");
			caps.setCapability("build", "1.0");
			caps.setCapability("browserName", "Internet Explorer");
			caps.setCapability("version", "11");
			caps.setCapability("platform", "Windows 10");
			caps.setCapability("screenResolution", "1366x768");
			RemoteWebDriver driver = new RemoteWebDriver(new URL("http://" + username + ":" + apikey +"@hub.crossbrowsertesting.com:80/wd/hub"), caps);
			driver.get("http://crossbrowsertesting.github.io/selenium_example_page.html");
			driver.quit();
		} catch (MalformedURLException e) {
			Assume.assumeNoException("Could not start selenium test", e);
		}

    }
    @AfterClass
    public static void clear() {
    	se = null;
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

        // check mobile
		Assert.assertTrue(se.operatingSystems2.get("iPadAir-iOS8Sim").isMobile());
        Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").getApiName(),"iPadAir-iOS8Sim");
        Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").getName(),"iPad Air / 8.1 Simulator");
		Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").getDeviceName(),"iPad Air Simulator");
		Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").getPlatformName(),"iOS");
		Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").getPlatformVersion(),"8.1");
		Assert.assertTrue(se.operatingSystems2.get("iPadAir-iOS8Sim").getPlatform ().isEmpty());
		Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").browsers2.get("MblSafari8.0").getIconClass(), "safari-mobile");
		Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").browsers2.get("MblSafari8.0").getBrowserName(), "Safari");
		Assert.assertTrue(se.operatingSystems2.get("iPadAir-iOS8Sim").browsers2.get("MblSafari8.0").getVersion().isEmpty());
		Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").resolutions2.get("1536x2048").getDeviceOrientation(), "portrait");
		Assert.assertEquals(se.operatingSystems2.get("iPadAir-iOS8Sim").resolutions2.get("1536x2048").getScreenResolution(), "1536x2048");

		// check desktop
		Assert.assertFalse(se.operatingSystems2.get("Mac10.13").isMobile());
		Assert.assertEquals(se.operatingSystems2.get("Mac10.13").getApiName(),"Mac10.13");
		Assert.assertEquals(se.operatingSystems2.get("Mac10.13").getName(),"Mac OSX 10.13");
		Assert.assertEquals(se.operatingSystems2.get("Mac10.13").getPlatform(),"Mac OSX 10.13");
		Assert.assertTrue(se.operatingSystems2.get("Mac10.13").getPlatformVersion().isEmpty());
		Assert.assertTrue(se.operatingSystems2.get("Mac10.13").getPlatformName().isEmpty());
		Assert.assertTrue(se.operatingSystems2.get("Mac10.13").getDeviceName().isEmpty());
		Assert.assertEquals(se.operatingSystems2.get("Mac10.13").browsers2.get("Safari11").getIconClass(), "safari");
		Assert.assertEquals(se.operatingSystems2.get("Mac10.13").browsers2.get("Safari11").getBrowserName(), "Safari");
		Assert.assertEquals(se.operatingSystems2.get("Mac10.13").browsers2.get("Safari11").getVersion(), "11");
		Assert.assertTrue(se.operatingSystems2.get("Mac10.13").resolutions2.get("1024x768").getDeviceOrientation().isEmpty());
		Assert.assertEquals(se.operatingSystems2.get("Mac10.13").resolutions2.get("1024x768").getScreenResolution(), "1024x768");
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
		try {
			// fail case
			Queue<Map<String, String>> allSeTestsInfo = se.getSeleniumTestInfo2("FakeNotRealAtAllTest", "-9", "FakeBrowser", "FakeOS", "FakeResolution");
			Assert.assertEquals(allSeTestsInfo.size(), 0);
			Assert.assertNull(allSeTestsInfo.poll());
		} catch (IOException e) {
			Assert.fail("Caught Exception");
		}
    }
    @Test
    public void testGetSeleniumTestIdCase2() {
    	try {
			// Case 2
			String testId = se.getSeleniumTestId("Jenkins Demo", "5", "Chrome35", "Mac10.9", "1024x768");
			// 2nd case should return the latest id that doesnt have jenkins in the client_platform
			Assert.assertFalse(testId.isEmpty());
			Assert.assertNotEquals("6800215", testId);
		} catch (IOException e) {
			Assert.fail("Caught Exception");
		}	
    }
	@Test
	public void testGetSeleniumTestIdCase3() {
		try {
			String secondTestId = se.getSeleniumTestId("Jenkins Demo", "5", "Chrome35", "Mac10.9", "1024x768");
			// Case 3
			String thirdTestId = se.getSeleniumTestId("FakeNotRealAtAllTest", "-9", "FakeBrowser", "FakeOS", "FakeResolution");
			// 3rd case should return the same id as the 2nd case
			Assert.assertFalse(thirdTestId.isEmpty());
			Assert.assertEquals(secondTestId, thirdTestId);
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
