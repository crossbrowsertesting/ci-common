package com.crossbrowsertesting.api;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crossbrowsertesting.configurations.*;

public class Selenium extends ApiFactory{
	/*
	 * List of browsers for selenium testing
	 */
	
	@Deprecated
	public List<OperatingSystem> operatingSystems = new LinkedList<OperatingSystem>();
	public Map<String, OperatingSystem> operatingSystems2 = new HashMap<String, OperatingSystem>(); //getting from a Map is O(1)
	
	public Selenium() {
		super("selenium");
		String json="";
		try {
			json = req.get("/browsers");
		}catch (IOException ioe) {}
		try {
			populateConfigurations(json);
		}catch (JSONException jsone) {}
		
	}
	public Selenium(String username, String apikey) {
		super("selenium", username, apikey);
		String json="";
		try {
			json = req.get("/browsers");
		}catch (IOException ioe) {}
		try {
			populateConfigurations(json);
		}catch (JSONException jsone) {}
		
	}
	
	@SuppressWarnings("deprecation")
	public void populateConfigurations(String json) throws JSONException {
		JSONArray j_configurations = new JSONArray(json);
		for(int i=0; i<j_configurations.length();i++) {
			//parse out the OS info
			JSONObject j_config = j_configurations.getJSONObject(i);
			String os_api_name = j_config.getString("api_name");
			String os_name = j_config.getString("name");
			OperatingSystem operatingSystem = new OperatingSystem(os_api_name, os_name);
			//parse out the browser info for the OS
			JSONArray j_browsers = j_config.getJSONArray("browsers");
			for(int j=0;j<j_browsers.length();j++) {
				JSONObject j_browser = j_browsers.getJSONObject(j);
				String browser_api_name = j_browser.getString("api_name");
				String browser_name = j_browser.getString("name");
				String browser_icon_class = j_browser.getString("icon_class");
				Browser browser = new Browser(browser_api_name, browser_name, browser_icon_class);
				operatingSystem.browsers.add(browser);
				operatingSystem.browsers2.put(browser_api_name, browser);
			}
			//parse out the resolution info for the OS
			JSONArray resolutions = j_config.getJSONArray("resolutions");
			for(int j=0;j<resolutions.length();j++) {
				JSONObject j_resolution = resolutions.getJSONObject(j);
				String resolution_name = j_resolution.getString("name");
				Resolution resolution = new Resolution(resolution_name);
				operatingSystem.resolutions.add(resolution);
				operatingSystem.resolutions2.put(resolution_name, resolution);
				
			}
			operatingSystems.add(operatingSystem);
			operatingSystems2.put(os_api_name, operatingSystem);
		}
	}
	@Deprecated
	public OperatingSystem getConfig(String configName) {
		/*
		 * Gets the config from os api name
		 */
		OperatingSystem c = new OperatingSystem("","");
		
    	for (int i=0;i<operatingSystems.size();i++) {
    		if (configName.equals(operatingSystems.get(i).getApiName())) {
                c = operatingSystems.get(i);
    		}
    	}
    	return c;
	}
	@Deprecated
	private Browser getBrowserInfo(OperatingSystem config, String browserApiName) {
		Browser configBrowser = null;
        for (int i=0 ; i<config.browsers.size() ; i++) {
        	configBrowser = config.browsers.get(i);
            if (configBrowser.getApiName().equals(browserApiName)) {
            	return configBrowser;
        	}
    	}
        return configBrowser;
	}
	@Deprecated
	public String getIconClass(String operatingSystemApiName, String browserApiName) {
		OperatingSystem config = getConfig(operatingSystemApiName);
		Browser browser = getBrowserInfo(config, browserApiName);
		return browser.getIconClass();
	}
	@Deprecated
	private String getBrowserName(OperatingSystem config, String browserApiName) {
		Browser browser = getBrowserInfo(config, browserApiName);
		return browser.getName();
	}
	@Deprecated
	public Queue<Map<String, String>> getSeleniumTestInfo(String name, String build, String browserApiName, String osApiName, String resolution) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("build", build);
		OperatingSystem config = getConfig(osApiName);
        String os = config.getName();
        String browser = getBrowserName(config, browserApiName);
		params.put("os", os);
		params.put("browser", browser);
		params.put("resolution",resolution);
		String json = req.get("", params);
		return parseIdAndPublicUrl(json);
	}
	public Queue<Map<String, String>> getSeleniumTestInfo2(String name, String build, String browserApiName, String osApiName, String resolution) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("build", build);
		OperatingSystem os = operatingSystems2.get(osApiName);
		params.put("os", os.getName());
		params.put("browser", os.browsers2.get(browserApiName).getName());
		params.put("resolution",resolution);
		String json = req.get("", params);
		return parseIdAndPublicUrl(json);
	}
	private Queue<Map<String, String>> parseIdAndPublicUrl(String json) {
		//got the test now need to parse out the id and publicUrl
		JSONObject j = new JSONObject(json);
		JSONArray seleniumTests = j.getJSONArray("selenium");
		Queue<Map<String, String>> tests = new LinkedList<Map<String, String>>();
		for(int i=0; i< seleniumTests.length();i++) {
			JSONObject seleniumTest = seleniumTests.getJSONObject(i);
			int seleniumTestId = seleniumTest.getInt("selenium_test_id");
			String publicUrl = seleniumTest.getString("show_result_public_url");
			Map<String, String> testInfo = new HashMap<String, String>();
			testInfo.put("selenium_test_id", Integer.toString(seleniumTestId));
			testInfo.put("show_result_public_url", publicUrl);
			tests.add(testInfo);
		}
		return tests;
	}
	private String apiSetAction(String seleniumTestId, String action, String param, String value) throws IOException {
		/*
		 * param and value are the additional parameters for actions
		 * 
		 * example: to set the score as a fail...
		 * action="set_score", param="score", value="fail"
		 */
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("action", action);
		params.put(param, value);
		return req.put("/"+seleniumTestId, params);
	}
	public void markPassOrFail(String seleniumTestId, boolean pass) throws IOException{
		/*
		 * true = pass, false = fail
		 */
		if (pass) {
			apiSetAction(seleniumTestId, "set_score", "score", "pass");
		} else {
			apiSetAction(seleniumTestId, "set_score", "score", "fail");
		}	
	}
	public void updateContributer(String seleniumTestId, String contributer, String contributerVersion, String pluginVersion) throws IOException {
		/*
		 * contributer looks like "jenkins1.5|v0.21"
		 */
		String fullContributer = contributer+contributerVersion+"|v"+pluginVersion;
		apiSetAction(seleniumTestId, "set_contributer", "contributer", fullContributer);	
	}
}
