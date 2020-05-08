package com.crossbrowsertesting.api;

import com.fizzed.jne.JNE;
import com.fizzed.jne.Options;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LocalTunnel extends ApiFactory {
	public boolean isTunnelRunning = false;
	@Deprecated
	public boolean jenkinsStartedTunnel = false; // variable name change
	public boolean pluginStartedTheTunnel = false;
	public Process tunnelProcess = null;
	public int tunnelID = -1;
	private String username, apikey, tunnelname = "";

	private final static Logger log = Logger.getLogger(LocalTunnel.class.getName());

	private final String TUNNEL_VERSION = "v1.2.2";
	private final String NODE_VERSION = "v6.11.2";
	//private Path tunnelPath;
	
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
	}
	public void init() {
		queryTunnel();
	}
	@Deprecated
	public boolean queryTunnelOld() throws JSONException {
		String json="";
		try {
			json = req.get("?num=1&active=true");
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
		if (tunnelID < 0) {
			if (!this.tunnelname.equals("") && this.tunnelname != null) {
				tunnelID = getTunnelID(this.tunnelname);
			} else {
				tunnelID = getTunnelID();
			}
			log.finer("tunnelId: " + tunnelID);
		}

		if (tunnelID > -1) {
			String json = "";
			json = req.get("/"+Integer.toString(tunnelID));
			log.finest(json);

			try {
				JSONObject res = new JSONObject(json);
				boolean isActive = res.getBoolean("active");
				log.finer("isActive: "+isActive);
				isTunnelRunning = isActive;
				return isActive;
			} catch(JSONException jsone) {
				log.fine("got jsonexception");
				return false;
			}
		} else {
			return false;
		}
	}

	private int getTunnelID() throws JSONException {
		String json = "";
		json = req.get("?num=1&active=true");
		if (json.isEmpty()) {
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
			json = req.get("?active=true");
		if (json.isEmpty()) {
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
			if(namedTunnel!=null){
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
	private String buildParamString(Map<String, String> params) {
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
		return tunnelParams;
	}
	private void run(ProcessBuilder tunnelProcessBuilder) throws IOException {
		//tunnelProcess = Runtime.getRuntime().exec(tunnelCommand);
//		tunnelProcess = new ProcessBuilder().command(tunnelCommand).inheritIO().start(); // prints the output
		log.fine("starting local tunnel");
		tunnelProcess = tunnelProcessBuilder.start(); // doesnt print the output
		jenkinsStartedTunnel = true;
		pluginStartedTheTunnel = true;
	}
	private void start(String tunnelLaunchCommand, Map<String, String> params) throws IOException {
		/*
		 * Actually runs the tunnel process.
		 * The others just expose common parameters for the tunnel
		 */
		String tunnelParams = buildParamString(params);
		ArrayList<String> tunnelCommand = new ArrayList();
		tunnelCommand.add(tunnelLaunchCommand);
		tunnelCommand.addAll(Arrays.asList(tunnelParams.split("\\s+")));
		log.finer("tunnel launch command: \""+tunnelLaunchCommand + tunnelParams+"\"");
		run(new ProcessBuilder().command(tunnelCommand));
	}
	private void start(String node, String cmd_start_js, Map<String, String> params) throws IOException {
		/*
		 * Actually runs the tunnel process.
		 * The others just expose common parameters for the tunnel
		 * Only using for linux right now
		 */
		String tunnelParams = buildParamString(params);
		ArrayList<String> tunnelCommand = new ArrayList();
		tunnelCommand.add(node);
		tunnelCommand.add(cmd_start_js);
		tunnelCommand.addAll(Arrays.asList(tunnelParams.split("\\s+")));
		log.finer("tunnel launch command: \""+node+ " " + cmd_start_js + " " + tunnelParams+"\"");
		run(new ProcessBuilder().command(tunnelCommand));
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
	public void start(boolean useBinary) throws IOException, URISyntaxException {
		// default bypass to true
		start(useBinary, true);
	}

	public void start(boolean useBinary, boolean bypass) throws URISyntaxException, IOException {
		/*
		 * Runs a subprocess that starts the node local tunnel
		 * either uses an installed version or the bundled binary
		 */
		HashMap params = new HashMap<String, String>();
		params.put(" --bypass ", Boolean.toString(bypass));
		if (useBinary) { // use the locked down binary

			// old way
			/*
			if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				log.fine("this is linux/unix system. we need to extract node version: " + NODE_VERSION + "and the local tunnel source code. Both will delete on exit");
				Options localtunnelSearchOptions = new Options();
				String tunnelPath = "/cbt_tunnels/" + TUNNEL_VERSION;
				localtunnelSearchOptions = localtunnelSearchOptions.setResourcePrefix(tunnelPath.toString());
				File sourceZip = JNE.findFile("source.zip", localtunnelSearchOptions);
				try {
					ZipFile zipFile = new ZipFile(sourceZip);
					log.fine("about to extract local tunnel source code");
					zipFile.extractAll(sourceZip.getParent());
				} catch (ZipException e) {
					log.fine("error extracting source code");
				}
				File localTunnelSource = new File(sourceZip.getParent(), "source");
				log.fine("done extracting local tunnel source: "+localTunnelSource.getPath());
				Options nodeBinarySearchOptions = new Options();
				String nodePath = "/node/" + NODE_VERSION;
				nodeBinarySearchOptions = nodeBinarySearchOptions.setResourcePrefix(nodePath.toString());
				File nodeZip = JNE.findFile("node.zip", nodeBinarySearchOptions);
				try {
					ZipFile zipFile = new ZipFile(nodeZip);
					log.fine("about to extract node version: "+NODE_VERSION);
					zipFile.extractAll(nodeZip.getParent());
				} catch (ZipException e) {
					log.fine("error extracting node");
				}
				File nodeDir = new File(nodeZip.getParent(), "node");
				File nodeBinary = new File(nodeDir, "bin/node");
				nodeBinary.setExecutable(true);

				File npmBinary = new File(nodeDir, "lib/node_modules/npm/bin/npm-cli.js");

				ArrayList<String> tunnelDependenciesInstallCommand = new ArrayList();
				tunnelDependenciesInstallCommand.add(nodeBinary.getPath());
				tunnelDependenciesInstallCommand.add(npmBinary.getPath());
				tunnelDependenciesInstallCommand.add("install");
				tunnelDependenciesInstallCommand.add(localTunnelSource.toString());
				tunnelDependenciesInstallCommand.add("--prefix");
				tunnelDependenciesInstallCommand.add(localTunnelSource.toString());
				log.fine("about to install cbt_tunnel dependencies");
				Process installCBTTunnelDependencies = new ProcessBuilder().command(tunnelDependenciesInstallCommand).start();
				try {
					log.fine("waiting for dependencies to finish installing");
					installCBTTunnelDependencies.waitFor();
					log.info("finished installing dependencies. return code: "+ installCBTTunnelDependencies.exitValue());
				} catch (InterruptedException e) {
					log.finer("error while waiting");
				}
				File cmd_start_js = new File(localTunnelSource, "cmd_start.js");
				start(nodeBinary.getPath(), cmd_start_js.getPath(), params);

			} else {
			*/
				Options binarySearchOptions = new Options();
				String tunnelPath = "/cbt_tunnels/" + TUNNEL_VERSION;
				binarySearchOptions = binarySearchOptions.setResourcePrefix(tunnelPath.toString()); // instead of using the default /jne we're going to use /cbt_tunnels/v0.1.0
				File binary = JNE.requireExecutable("cbt_tunnels", binarySearchOptions);
				start(binary.getPath(), params);
			//}

		}
		else { // use the npm installed version... must be in the PATH
			start("cbt_tunnels", params);
		}
	}

	public void stop() throws IOException, InterruptedException {
		/*
		 * Stops the tunnel if the plugin started it
		 */
		queryTunnel();
		log.fine("about to kill local tunnel");
		@SuppressWarnings("unused")
		String json = req.delete("/"+Integer.toString(tunnelID));
		if (pluginStartedTheTunnel && tunnelProcess != null) {
			tunnelProcess.destroy();
		}
		log.fine("done killing local tunnel");
	}
}
