package com.crossbrowsertesting.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
	private String username, apikey = "";
	private String tunnelname = null;
	private File tunnelBinary = null;
	
	public LocalTunnel(String username, String apikey, String tunnelname) {
		super("tunnels", username, apikey);
		this.tunnelname = tunnelname;
		setupClass(username, apikey);
		init();
	}
	public LocalTunnel(String username, String apikey) {
		super("tunnels", username, apikey);
		setupClass(username, apikey);
		init();
	}
	private void setupClass(String username, String apikey) {
		this.username = username;
		this.apikey = apikey;
		
		// lets get the full path to the cbt_tunnels binary
		Path tunnelPath = null;

		if (System.getProperty("os.name").toLowerCase().contains("mac")) { // mac
			tunnelPath = Paths.get("cbt_tunnel", "v0.1.0", "cbt-tunnels-macos");
		}else if (System.getProperty("os.name").toLowerCase().contains("win")) { // windows
			tunnelPath = Paths.get("cbt_tunnel", "v0.1.0", "cbt-tunnels-win.exe");
		}else if (System.getProperty("os.name").toLowerCase().contains("nix") ||
				System.getProperty("os.name").toLowerCase().contains("nux") ||
				System.getProperty("os.name").toLowerCase().contains("aix")) { // linux / unix ?
			tunnelPath = Paths.get("cbt_tunnel", "v0.1.0", "cbt-tunnels-ubuntu");
		}
		if (tunnelPath != null) {
			ClassLoader classLoader = this.getClass().getClassLoader();
			this.tunnelBinary = new File(classLoader.getResource(tunnelPath.toString()).getFile());
		}
		if (this.tunnelBinary != null && !this.tunnelBinary.canExecute() && (System.getProperty("os.name").toLowerCase().contains("mac") ||
				System.getProperty("os.name").toLowerCase().contains("nix") ||
				System.getProperty("os.name").toLowerCase().contains("nux") ||
				System.getProperty("os.name").toLowerCase().contains("aix"))) { // binary needs to be executable on *nix systems and mac
			this.tunnelBinary.setExecutable(true);
		}
	}
	public void init() {
		queryTunnel();
	}
	@Deprecated
	public boolean queryTunnelOld() throws JSONException {
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

	public boolean queryTunnel(){
		if(this.tunnelname != null && !this.tunnelname.equals("") && !this.tunnelname.isEmpty()){ // named tunnel
			tunnelID = getTunnelID(this.tunnelname);
		}else{ // unnamed tunnel
			tunnelID = getTunnelID();
		} 
		//make sure tunnelID is not -1
		String json = "";
		try{
			json = req.get("/"+Integer.toString(tunnelID));
		}catch(IOException ioe){
			//ioe.printStackTrace();
			return false;
		}
		try{
			JSONObject res = new JSONObject(json);
			boolean isActive = res.getBoolean("active");
			isTunnelRunning = isActive;
			return isActive;
		}catch(JSONException jsone){
			return false;
		}
	}

	private int getTunnelID() throws JSONException {
		String json = "";
		try{
			json = req.get("?active=true");
		}catch(IOException ioe){
			ioe.printStackTrace();
			return -1;
		}
		try{
			JSONObject res = new JSONObject(json);
			JSONArray tunnels = res.getJSONArray("tunnels");
			JSONObject unnamedTunnel = null;
			for(int i=0;i<tunnels.length();i++){
				JSONObject tunnel = tunnels.getJSONObject(i);
				if(tunnel.isNull("tunnel_name")){
					unnamedTunnel = tunnel;
					break;
				}
			}
			if(unnamedTunnel!=null) {
				int tunnelID = unnamedTunnel.getInt("tunnel_id");
				return tunnelID;
			}else{
				return -1;
			}
		}catch(JSONException jsone){
			jsone.printStackTrace();
			return -1;
		}
	}

	private int getTunnelID(String tunnelname) throws JSONException {
		String json = "";
		try{
			json = req.get("?active=true");
		}catch(IOException ioe) {
			ioe.printStackTrace();
			return -1;
		}
		try {
			JSONObject res = new JSONObject(json);
			JSONArray tunnels = res.getJSONArray("tunnels");
			JSONObject namedTunnel = null;
			for(int i = 0; i < tunnels.length() ; i++) {
				JSONObject tunnel = tunnels.getJSONObject(i);
				if(!tunnel.isNull("tunnel_name") && tunnel.getString("tunnel_name").equals(tunnelname)){
					namedTunnel = tunnel;
					break;
				}
			}
			if(namedTunnel != null){
				int tunnelID = namedTunnel.getInt("tunnel_id");
				return tunnelID;
			}else{
				return -1;
			}
		}catch(JSONException jsone){
			jsone.printStackTrace();
			return -1;
		}
	}

	private void start(String tunnelLaunchCommand, Map<String, String> params) throws IOException {
		/*
		 * Actually runs the tunnel process.
		 * The others just expose common parameters for the tunnel
		 */
		params.put(" --username ", username);
		params.put(" --authkey ", apikey);
		if (this.tunnelname != null && !this.tunnelname.equals("")) { 
			params.put(" --tunnelname ", tunnelname);
		}
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
		String[] tunnelCommand = (tunnelLaunchCommand + tunnelParams).split("\\s+");
		//tunnelProcess = Runtime.getRuntime().exec(tunnelCommand);
//		tunnelProcess = new ProcessBuilder().command(tunnelCommand).inheritIO().start();
//		tunnelProcess = new ProcessBuilder().command(tunnelCommand).inheritIO().start(); // prints the output
		tunnelProcess = new ProcessBuilder().command(tunnelCommand).start(); // doesnt print the output

		jenkinsStartedTunnel = true;
		pluginStartedTheTunnel = true;
	}
	public void start(String localTunnelPath) throws IOException{
		/*
		 * Runs a subprocess that starts the node local tunnel using a custom path
		 */
		start(localTunnelPath, new HashMap<String, String>());
	}
	@Deprecated
	public void start() throws IOException {
		/*
		 * Runs a subprocess that starts the node local tunnel
		 */
		start("cbt_tunnels", new HashMap<String, String>());
	}
	public void start(boolean useBinary) throws IOException {
		/*
		 * Runs a subprocess that starts the node local tunnel
		 */
		if (useBinary && tunnelBinary.exists()) { // use the locked down binary
			start(tunnelBinary.getAbsolutePath(), new HashMap<String, String>());
		} else { // use the npm installed version... must be in the PATH
			start("cbt_tunnels", new HashMap<String, String>());
		}
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
