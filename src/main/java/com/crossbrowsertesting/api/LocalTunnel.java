package com.crossbrowsertesting.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocalTunnel extends ApiFactory {
	public boolean isTunnelRunning = false;
	@Deprecated
	public boolean jenkinsStartedTunnel = false; // variable name change
	public boolean pluginStartedTheTunnel = false;
	public Process tunnelProcess;
	public int tunnelID;
	private String username, apikey;
	
	public LocalTunnel(String username, String apikey) {
		super("tunnels", username, apikey);
		this.username = username;
		this.apikey = apikey;
		init();
	}
	public void init() {
		queryTunnel();
	}
	public boolean queryTunnel() throws JSONException {
		String json="";
		try {
			json = req.get("?num=1&active=true");
		}catch (IOException ioe) {}
		try {
			JSONObject res = new JSONObject(json);
			JSONArray tunnels = res.getJSONArray("tunnels");
			boolean isActive = false;
			for (int i=0; i<tunnels.length();i++) {
				JSONObject tunnel = tunnels.getJSONObject(i);
				tunnelID = tunnel.getInt("tunnel_id");
				isActive = tunnel.getBoolean("active");
			}
			isTunnelRunning = isActive;
			return isActive;
		}catch (JSONException jsone) {
			return false;
		}
	}
	private void start(String tunnelLaunchCommand, Map<String, String> params) throws IOException {
		/*
		 * Actually runs the tunnel process.
		 * The others just expose common parameters for the tunnel
		 */
		params.put(" --username ", username);
		params.put(" --authkey ", apikey);
		if (req.useProxy) {
			params.put(" --proxyPort ", Integer.toString(req.proxyPort));
			String proxyUrl = req.proxyUrl;
			if (req.useProxyCredentials) {
				proxyUrl = req.proxyUsername + ":" + req.proxyPassword + "@" + proxyUrl;
			}
			params.put(" --proxyIp ", proxyUrl);
		}
		String tunnelParams = "";
		for (Map.Entry<String, String> entry : params.entrySet()) {
			tunnelParams += entry.getKey() + entry.getValue();
		}
		String tunnelCommand = tunnelLaunchCommand + tunnelParams;
		System.out.println(tunnelCommand);
		tunnelProcess = Runtime.getRuntime().exec(tunnelCommand);
		jenkinsStartedTunnel = true;
		pluginStartedTheTunnel = true;
	}
	public void start(String localTunnelPath) throws IOException{
		/*
		 * Runs a subprocess that starts the node local tunnel
		 */
		start(localTunnelPath, new HashMap<String, String>());
	}
	public void start() throws IOException {
		/*
		 * Runs a subprocess that starts the node local tunnel
		 */
		start("cbt_tunnels", new HashMap<String, String>());
	}
	public void stop() throws IOException, InterruptedException {
		/*
		 * Stops the tunnel if the plugin started it
		 */
		queryTunnel();
		@SuppressWarnings("unused")
		String json = req.delete("/"+Integer.toString(tunnelID), null);
		if (pluginStartedTheTunnel) {
			tunnelProcess.destroy();
		}
	}
}
