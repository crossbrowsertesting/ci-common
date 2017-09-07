package com.crossbrowsertesting.test;

import com.crossbrowsertesting.api.Screenshots;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ScreenshotTest extends APITestFactory {
    private static Screenshots ss;
    private static String browserListName = "All Safaris";
    private static String url = "http://mybrowserinfo.com";
    private static Map<String, String> ssResults;

    @BeforeClass
    public static void set() {
        ss = new Screenshots(username, apikey);
    }
    @AfterClass
    public static void clear() {
        ss = null;
    }
    private Map<String, String> makeBrowserMap(String os, String browser, String resolution) {
        HashMap<String, String> configs = new HashMap();
        configs.put("os_api_name", os);
        configs.put("browser_api_name", browser);
        configs.put("resolution", resolution);
        return configs;
    }
    @Test
    public void testPopulateItems() {
        Assert.assertFalse(ss.loginProfiles.isEmpty());
        Assert.assertFalse(ss.browserLists.isEmpty());
        Assert.assertFalse(ss.loginProfiles.isEmpty());
        Assert.assertFalse(ss.operatingSystems.isEmpty());
        Assert.assertFalse(ss.operatingSystems2.isEmpty());
    }
    @Test
    public void testRunScreenshotsWithBrowserList() {
        ssResults = ss.runScreenshotTest(browserListName, url);
        Assert.assertFalse(ssResults.containsKey("error"));
        Assert.assertTrue(ssResults.containsKey("screenshot_test_id") && !ssResults.get("screenshot_test_id").isEmpty());
        Assert.assertEquals(url, ssResults.get("url"));
    }
    @Test
    public void testRunScreenshotsWithBrowsers() {
        LinkedList<Map<String, String>> browsers = new LinkedList();
        browsers.add(makeBrowserMap("Win10", "Edge14", "1024x768"));
        browsers.add(makeBrowserMap("Win8.1", "IE11", "1024x768"));
        ssResults = ss.runScreenshot(browsers, url);
        Assert.assertFalse(ssResults.containsKey("error"));
        Assert.assertTrue(ssResults.containsKey("screenshot_test_id") && !ssResults.get("screenshot_test_id").isEmpty());
        Assert.assertEquals(url, ssResults.get("url"));
    }
    @Test
    public void testGetLoginProfiles() {
        Assert.assertTrue(ss.browserLists.size() > 0); // should always at least be one
        Assert.assertTrue(ss.browserLists.get(0).isEmpty()); // first one should always be an empty string
        if (ss.browserLists.size() > 1) {
            for (int i=1; i < ss.browserLists.size();i++) {
                Assert.assertFalse(ss.browserLists.get(i).isEmpty());
            }
        }
    }
}
