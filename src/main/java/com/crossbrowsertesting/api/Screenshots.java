package com.crossbrowsertesting.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class Screenshots extends TestTypeApiFactory{

	public List<String> browserLists;
	public List<String> loginProfiles;

	public Screenshots(String username, String apikey) {
		super("screenshots", username, apikey);
		init();	
	}
	public void init() {
		super.init();
		browserLists = new LinkedList<String>();
		loginProfiles = new LinkedList<String>();
		populateBrowserLists();
		populateSavedLoginProfiles();
		populateSavedSeleniumScripts();
	}
	private void populateBrowserLists() {
		String json="";
		json = req.get("/browserlists");
		JSONArray j_browserLists = new JSONArray(json);
		for(int i=0; i<j_browserLists.length();i++) {
			JSONObject j_browserList = j_browserLists.getJSONObject(i);
			String browser_list_name = j_browserList.getString("browser_list_name");
			browserLists.add(browser_list_name);
		}
	}
	private void populateSavedLoginProfiles() {
		String json="";
		json = req.get("/loginprofiles/");
		JSONArray j_loginProfiles = new JSONArray(json);
		for (int i=0; i<j_loginProfiles.length();i++) {
			JSONObject j_loginProfile = j_loginProfiles.getJSONObject(i);
			String loginProfileName = j_loginProfile.getString("profile_name");
			loginProfiles.add(loginProfileName);
		}
	}
	private void populateSavedSeleniumScripts() {
		String json="";
		json = req.get("/seleniumscripts");
		JSONArray j_seleniumScripts = new JSONArray(json);
		for (int i=0; i<j_seleniumScripts.length();i++) {
			JSONObject j_seleniumScript = j_seleniumScripts.getJSONObject(i);
			String seleniumScriptName = j_seleniumScript.getString("script_name");
			loginProfiles.add(seleniumScriptName);
		}
	}
	public HashMap<String, String> runScreenshotTest(String selectedBrowserList, String url) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("url", url);
		params.put("browser_list_name", selectedBrowserList);
		return runScreenshotTest(params);
	}
	public HashMap<String, String> runScreenshotTest(String browserList, String url, String loginProfile) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("url", url);
		params.put("browser_list_name", browserList);
		params.put("login", loginProfile);
		return runScreenshotTest(params);
	}
	private HashMap<String, Object> addMultipleBrowsers(List<Map<String, String>> browsers, HashMap<String, Object> params) {
		List<String> browsersParam = new LinkedList<String>();
		ListIterator<Map<String, String>> browserIterator = browsers.listIterator();
		while(browserIterator.hasNext()) {
			Map<String, String> browser = browserIterator.next();
			String browserString = browser.get("os_api_name") + "|" + browser.get("browser_api_name") + "|" + browser.get("resolution");
			browsersParam.add(browserString);
		}
		params.put("browsers", browsersParam);
		return params;
	}
	public HashMap<String, String> runScreenshot(List<Map<String, String>> browsers, String url) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		params = addMultipleBrowsers(browsers, params);
		return runScreenshotTest(params, true);
	}
	public HashMap<String, String> runScreenshot(List<Map<String, String>> browsers, String url, String loginProfile) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		params.put("login", loginProfile);
		params = addMultipleBrowsers(browsers, params);
		return runScreenshotTest(params, true);
	}
	@Deprecated
	private HashMap<String, String> runScreenshotTest(HashMap<String, String> params) {
		/*
		 * really runs the screenshots test
		 */
		String json = "";
		json = req.post("/", params);
		return parseResults(json);
	}
	private HashMap<String, String> runScreenshotTest(HashMap<String, Object> params, boolean paramsContainsMultipleBrowsers) {
		/*
		 * really runs the screenshots test
		 */
		String json = "";
		json = req.post("/", params, paramsContainsMultipleBrowsers);
		return parseResults(json);
	}
	private HashMap<String, String> parseResults(String json) {
		/*
		 * parse the json returned from running the screenshots test
		 */
		HashMap<String, String> results = new HashMap<String, String>();
		try {
			JSONObject screenshotResults = new JSONObject(json);
			JSONArray screenshotVersions = screenshotResults.getJSONArray("versions");
			JSONObject latestScreenshotVersion = screenshotVersions.getJSONObject(screenshotVersions.length()-1);
			
			results.put("screenshot_test_id", Integer.toString(screenshotResults.getInt("screenshot_test_id")));
			results.put("url", screenshotResults.getString("url"));
			results.put("version_id", Integer.toString(latestScreenshotVersion.getInt("version_id")));
			results.put("download_results_zip_public_url", latestScreenshotVersion.getString("download_results_zip_public_url"));
			results.put("show_results_public_url", latestScreenshotVersion.getString("show_results_public_url"));
			results.put("active", Boolean.toString(latestScreenshotVersion.getBoolean("active")));
		} catch (Exception e) {
			results.put("error", e.toString());
			results.put("json", json);
		}
		return results;
	}

	public boolean testIsRunning(String screenshotsTestId) throws IOException {
		String json = req.get("/"+screenshotsTestId);
		HashMap<String, String> results = parseResults(json);
		return Boolean.parseBoolean(results.get("active"));
	}
}
