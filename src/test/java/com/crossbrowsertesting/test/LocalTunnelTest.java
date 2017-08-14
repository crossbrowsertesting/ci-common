package com.crossbrowsertesting.test;

import com.crossbrowsertesting.api.LocalTunnel;
import org.junit.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Ignore
public class LocalTunnelTest extends APITestFactory {

    static LocalTunnel unnamedTunnel;
    static LocalTunnel namedTunnel;
    private static String tunnelName = "cicommontest";

    @BeforeClass
    public static void set() {
        // unnamed tunnel
        unnamedTunnel = new LocalTunnel(username, apikey);
        try {
            unnamedTunnel.start(true);
        } catch (URISyntaxException | IOException e) {
            Assume.assumeNoException("Could not start unnamed tunnel", e);
        }

        // named tunnel
        namedTunnel = new LocalTunnel(username, apikey, tunnelName);
        try {
            namedTunnel.start(true);
        } catch (URISyntaxException | IOException e) {
            Assume.assumeNoException("Could not start named tunnel", e);
        }

        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            Assume.assumeNoException("Could not wait for tunnels to start", e);
        }

    }

    @AfterClass
    public static void clear() {
        // unnamed tunnel
        try {
            unnamedTunnel.stop();
        } catch (IOException | InterruptedException e) {
            Assume.assumeNoException("Could not stop unnamed tunnel", e);
        }
        unnamedTunnel = null;

        // named tunnel
        try {
            namedTunnel.stop();
        } catch (IOException | InterruptedException e) {
            Assume.assumeNoException("Could not stop named tunnel", e);
        }
        namedTunnel = null;
    }
    @Test
    public void testQueryUnnamedTunnel() {
        unnamedTunnel.queryTunnel();
        Assert.assertTrue(unnamedTunnel.isTunnelRunning);
    }
    @Test
    public void testQueryNamedTunnel() {
        namedTunnel.queryTunnel();
        Assert.assertTrue(namedTunnel.isTunnelRunning);
    }
}
