package com.crossbrowsertesting.api;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PluginCheck extends ApiFactory{	
	private String contributer;
	private String contributerVersion;
	private String pluginVersion;
	
	public PluginCheck(String contributer, String contributerVersion, String pluginVersion) {
		super("plugins");
		this.contributer = contributer;
		this.contributerVersion = contributerVersion;
		this.pluginVersion = pluginVersion;
	}
	public void init() {
		
	}
	public boolean needToUpgradeFake() throws IOException{
		return false;
	}
	public boolean needToUpgrade() throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("contributer", contributer);
		params.put("contributer_version", contributerVersion);
		params.put("plugin_version", pluginVersion);
		
		String json = req.get("", params);
		JSONObject jo = new JSONObject(json);
		return !jo.getBoolean("safe");
	}
	

}
