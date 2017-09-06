package com.crossbrowsertesting.test;

import com.crossbrowsertesting.api.Screenshots;
import org.junit.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ScreenshotTest extends APITestFactory {
    private static Screenshots ss;
    private static String browserListName = "All Safaris";
    private static String url = "http://mybrowserinfo.com";
    private static Map<String, String> ssResults;

    @BeforeClass
    public static void set() {
        ss = new Screenshots(username, apikey);
        ssResults = ss.runScreenshotTest(browserListName, url);
    }
    @AfterClass
    public static void clear() {
        ss = null;
    }

    @Test
    public void testPopulateItems() {
        Assert.assertFalse(ss.loginProfiles.isEmpty());
        Assert.assertFalse(ss.browserLists.isEmpty());
    }
    @Test
    public void testRunScreenshotsTest() {
        Assert.assertTrue(ssResults.containsKey("screenshot_test_id") && !ssResults.get("screenshot_test_id").isEmpty());
        Assert.assertEquals(url, ssResults.get("url"));


    }
    @Test
    public void testIsActive() {
        try {
            Assert.assertNotNull(ss.testIsRunning(ssResults.get("screenshot_test_id")));
        } catch (IOException e) {
            Assume.assumeNoException(e);
        }
    }
}
