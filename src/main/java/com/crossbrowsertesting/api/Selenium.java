package com.crossbrowsertesting.api;

import com.crossbrowsertesting.configurations.Browser;
import com.crossbrowsertesting.configurations.OperatingSystem;
import com.crossbrowsertesting.configurations.Resolution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class Selenium extends TestTypeApiFactory{
	/*
	 * List of browsers for selenium testing
	 */

	public Selenium() {
		super("selenium");
		init();

	}
	public Selenium(String username, String apikey) {
		super("selenium", username, apikey);
		init();
	}
	@Override
	public void init() {
		operatingSystems = new LinkedList<OperatingSystem>();
		operatingSystems2 = new HashMap<String, OperatingSystem>();
		configurationsAsJson = "";
		populateBrowsers();
	}
	@Override
	void populateBrowsers() {
		String json="";
		json = req.get("/browsers");
		try {
			operatingSystems = new LinkedList<OperatingSystem>();
			operatingSystems2 = new HashMap<String, OperatingSystem>();
		}catch (JSONException jsone) {}
		configurationsAsJson = json; // for TeamCity
		if (json != null && !json.isEmpty() && String.valueOf(json.charAt(0)).equals("[")) {
			JSONArray j_configurations = new JSONArray(json);
			for (int i = 0; i < j_configurations.length(); i++) {
				//parse out the OS info
				JSONObject j_config = j_configurations.getJSONObject(i);
				String os_api_name = j_config.getString("api_name");
				String os_name = j_config.getString("name");
				String device = j_config.getString("device");

				OperatingSystem operatingSystem = new OperatingSystem(os_api_name, os_name, device);
				if (!operatingSystem.isMobile()) {
					// set desktop caps
					operatingSystem.setPlatform(j_config.getJSONObject("caps").getString("platform"));
				} else {
					// set mobile caps
					JSONObject caps = j_config.getJSONObject("caps");
					operatingSystem.setDeviceName(caps.getString("deviceName"));
					operatingSystem.setPlatformName(caps.getString("platformName"));
					operatingSystem.setPlatformVersion(caps.getString("platformVersion"));
				}
				//parse out the browser info for the OS
				JSONArray j_browsers = j_config.getJSONArray("browsers");
				for (int j = 0; j < j_browsers.length(); j++) {
					JSONObject j_browser = j_browsers.getJSONObject(j);
					String browser_api_name = j_browser.getString("api_name");
					String browser_name = j_browser.getString("name");
					String browser_icon_class = j_browser.getString("icon_class");
					JSONObject browser_caps = j_browser.getJSONObject("caps");
					Browser browser = new Browser(browser_api_name, browser_name, browser_icon_class, device, browser_caps.getString("browserName"));
					if (!browser.isMobile()) {
						// set extra desktop caps
						browser.setVersion(browser_caps.getString("version"));
					}
					operatingSystem.browsers.add(browser);
					operatingSystem.browsers2.put(browser_api_name, browser);
				}
				//parse out the resolution info for the OS
				JSONArray resolutions = j_config.getJSONArray("resolutions");
				for (int j = 0; j < resolutions.length(); j++) {
					JSONObject j_resolution = resolutions.getJSONObject(j);
					String resolution_name = j_resolution.getString("name");
					JSONObject resolution_caps = j_resolution.getJSONObject("caps");
					Resolution resolution = new Resolution(resolution_name, device);
					if (operatingSystem.isMobile()) {
						// set mobile caps
						resolution.setDeviceOrientation(resolution_caps.getString("deviceOrientation"));
					}
					// desktops have a 'screenResolution' cap but it has the same value as 'name'
					operatingSystem.resolutions.add(resolution);
					operatingSystem.resolutions2.put(resolution_name, resolution);

				}
				operatingSystems.add(operatingSystem);
				operatingSystems2.put(os_api_name, operatingSystem);
			}
		}
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
	public Map<String, String> getSeleniumTestInfoWithJenkinsCaps(String name, String build, String browserApiName, String osApiName, String resolution) throws IOException {
		// Get the test info history
		Map<String, String> params = new HashMap<String, String>();
		OperatingSystem os = operatingSystems2.get(osApiName);
		if (os != null) {
			params.put("os", os.getName());
			params.put("browser", os.browsers2.get(browserApiName).getName());
		}
		params.put("resolution",resolution);
		String historyJson = req.get("", params);
		JSONObject history = new JSONObject(historyJson);
		JSONArray seleniumTests = history.getJSONArray("selenium");

		for(int i = 0; i < seleniumTests.length(); i++) {
			JSONObject test = seleniumTests.getJSONObject(i);
			JSONObject caps = test.getJSONObject("caps");
			if(caps.has("jenkinsName") && caps.has("jenkinsBuild")) {
				String jenkinsName = caps.getString("jenkinsName");
				String jenkinsBuild = caps.getString("jenkinsBuild");
				if(jenkinsName.equals(name) && jenkinsBuild.equals(build)) {
					int seleniumTestId = test.getInt("selenium_test_id");
					String publicUrl = test.getString("show_result_public_url");
					Map<String, String> testInfo = new HashMap<String, String>();
					testInfo.put("selenium_test_id", Integer.toString(seleniumTestId));
					testInfo.put("show_result_public_url", publicUrl);
					return testInfo;
				}
			}
		}
		return null;
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
