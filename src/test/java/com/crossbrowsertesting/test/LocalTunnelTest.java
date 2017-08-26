package com.crossbrowsertesting.test;

import com.crossbrowsertesting.api.LocalTunnel;
import org.junit.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class LocalTunnelTest extends APITestFactory {

    static LocalTunnel unnamedTunnel;
    static LocalTunnel namedTunnel;
    private static String tunnelName = "cicommontest";

    @BeforeClass
    public static void set() {
        unnamedTunnel = new LocalTunnel(username, apikey);
        namedTunnel = new LocalTunnel(username, apikey, tunnelName);
    }

    @AfterClass
    public static void clear() {
        unnamedTunnel = null;
        namedTunnel = null;
    }
    @Test
    public void testQueryUnnamedTunnel() throws IOException, URISyntaxException {
        unnamedTunnel.start(true);
        try {
            TimeUnit.SECONDS.sleep(30);
            unnamedTunnel.queryTunnel();
            Assert.assertTrue(unnamedTunnel.isTunnelRunning);
            Assert.assertTrue(unnamedTunnel.tunnelID > 0);
            Assert.assertTrue(unnamedTunnel.pluginStartedTheTunnel);
            unnamedTunnel.stop();
            TimeUnit.SECONDS.sleep(30);
            unnamedTunnel.queryTunnel();
            Assert.assertFalse(unnamedTunnel.isTunnelRunning);
        } catch (InterruptedException e) {
            Assume.assumeNoException("Could not wait for tunnels to start", e);
        }
    }
    @Test
    public void testQueryNamedTunnel() throws IOException, URISyntaxException {
        namedTunnel.start(true);
        try {
            TimeUnit.SECONDS.sleep(30);
            namedTunnel.queryTunnel();
            Assert.assertTrue(namedTunnel.isTunnelRunning);
            Assert.assertTrue(namedTunnel.tunnelID > 0);
            Assert.assertTrue(namedTunnel.pluginStartedTheTunnel);
            namedTunnel.stop();
            TimeUnit.SECONDS.sleep(30);
            namedTunnel.queryTunnel();
            Assert.assertFalse(namedTunnel.isTunnelRunning);
        } catch (InterruptedException e) {
            Assume.assumeNoException("Could not wait for tunnels to start", e);
        }
    }
}
