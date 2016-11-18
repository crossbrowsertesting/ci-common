package com.crossbrowsertesting.api;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocalTunnel {
	private Request req;
	public boolean isTunnelRunning = false;
	@Deprecated
	public boolean jenkinsStartedTunnel = false; // variable name change
	public boolean pluginStartedTheTunnel = false;
	public Process tunnelProcess;
	public int tunnelID;
	private String username, apikey;
	
	public LocalTunnel(String username, String apikey) {
		this.username = username;
		this.apikey = apikey;
		req = new Request("tunnels", username, apikey);
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
	public void start(String nodePath, String localTunnelPath) throws IOException {
		/*
		 * Runs a subprocess that starts the node local tunnel
		 */
		String tunnelCommand = nodePath + " " + localTunnelPath + " --username " + username + " --authkey " +apikey;
		tunnelProcess = Runtime.getRuntime().exec(tunnelCommand);
		jenkinsStartedTunnel = true;
		pluginStartedTheTunnel = true;	
	}
	public void start() throws IOException {
		/*
		 * Runs a subprocess that starts the node local tunnel
		 */
		String tunnelCommand = "cbt_tunnels --username " + username + " --authkey " +apikey;
		tunnelProcess = Runtime.getRuntime().exec(tunnelCommand);
		jenkinsStartedTunnel = true;
		pluginStartedTheTunnel = true;
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
