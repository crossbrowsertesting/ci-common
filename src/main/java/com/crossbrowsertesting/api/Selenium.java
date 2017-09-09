package com.crossbrowsertesting.api;

import com.crossbrowsertesting.configurations.OperatingSystem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class Selenium extends TestTypeApiFactory{
	/*
	 * List of browsers for selenium testing
	 */

	public Selenium() {
		super("selenium");
		super.init();
		
	}
	public Selenium(String username, String apikey) {
		super("selenium", username, apikey);
		super.init();
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
	public String getSeleniumTestId(String name, String build, String browserApiName, String osApiName, String resolution) throws IOException {
		/*
		 * ------------ WARNING ---------------------
		 * this may return the wrong selenium test id. it's really only intended for analytics
		 * use getSeleniumTestInfo or getSeleniumTestInfo2 for an actual selenium_test_id if you have valid parameters
		 */
		String seleniumTestId = "";
		Map<String, String> params = new HashMap<String, String>();
		boolean done = false;
		for (int tryCount = 1; tryCount <= 3 && !done; tryCount++) {
			seleniumTestId = "";
			params.put("name", name);
			params.put("build", build);
			OperatingSystem os = operatingSystems2.get(osApiName);
			try {
				params.put("os", os.getName());
				params.put("browser", os.browsers2.get(browserApiName).getName());
				params.put("resolution",resolution);
			} catch (NullPointerException npe) { // if we get a NullPointer some where lets just assume its the 3rd case
				params.put("os", "");
				params.put("resolution", "");
				params.put("browser", "");
			}
			switch (tryCount) {
				case 1:
					// use all the configuration params on the first try
					break;
				case 2:
					// use without the name and build number
					params.remove("name");
					params.remove("build");
					break;
				case 3:
					// last try... just use any test that doesnt already have jenkins in the client_platform
					params.clear();
					break;
			}
			String json = req.get("", params);
			//got the test now need to parse out the id and publicUrl
			JSONObject j = new JSONObject(json);
			JSONArray seleniumTests = j.getJSONArray("selenium");
			for(int i=0; i < seleniumTests.length() && !done;i++) {
				JSONObject seleniumTest = seleniumTests.getJSONObject(i);
				String clientPlatform = seleniumTest.getString("client_platform");
				String tmp_seleniumTestId = Integer.toString(seleniumTest.getInt("selenium_test_id"));
				if (tryCount == 1 && !tmp_seleniumTestId.isEmpty()) {
					done = true;
					seleniumTestId = tmp_seleniumTestId;
				} else if (tryCount > 1 && (!tmp_seleniumTestId.isEmpty() && !clientPlatform.contains("jenkins"))) {
					done = true;
					seleniumTestId = tmp_seleniumTestId;
				}
			}
		}
		return seleniumTestId;
	}
	public Queue<Map<String, String>> getSeleniumTestInfo2(Map<String, String> params) throws IOException {
		// you can use the get request params
		String json = req.get("", params);
		return parseIdAndPublicUrl(json);
	}
	public Queue<Map<String, String>> getSeleniumTestInfo2(String name, String build, String browserApiName, String osApiName, String resolution) throws IOException {
		// you can specify the params one at a time
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("build", build);
		OperatingSystem os = operatingSystems2.get(osApiName);
		if (os != null) {
			params.put("os", os.getName());
			params.put("browser", os.browsers2.get(browserApiName).getName());
		}
		params.put("resolution",resolution);
		return getSeleniumTestInfo2(params);
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
