package com.crossbrowsertesting.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Screenshots extends ApiFactory{

	public List<String> browserLists; // only need browserlist right now. may need the actual list of available browsers in the future
	public List<String> loginProfiles;
	public Screenshots(String username, String apikey) {
		super("screenshots", username, apikey);
		init();	
	}
	public void init() {
		browserLists = new LinkedList<String>();
		loginProfiles = new LinkedList<String>();

		populateBrowserLists();
		populateSavedLoginProfiles();
		populateSavedSeleniumScripts();
	}
	private void populateBrowserLists() {
		browserLists.add(""); //add blank one
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
		if (loginProfiles.size() < 1 || loginProfiles.get(0).isEmpty()) {
			loginProfiles.add(""); // add a blank one
		}
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
		if (loginProfiles.size() < 1 || loginProfiles.get(0).isEmpty()) {
			loginProfiles.add(""); // add a blank one
		}
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
	private HashMap<String, String> runScreenshotTest(HashMap<String, String> params) {
		/*
		 * really runs the screenshots test
		 */
		String json = "";
		json = req.post("/", params);
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
