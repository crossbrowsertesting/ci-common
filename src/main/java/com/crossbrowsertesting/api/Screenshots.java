package com.crossbrowsertesting.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Screenshots extends ApiFactory{

	public List<String> browserLists; // only need browserlist right now. may need the actual list of available browsers in the future
	
	public Screenshots(String username, String apikey) {
		super("screenshots", username, apikey);		
		
		init();	
	}
	public void init() {
		browserLists = new LinkedList<String>();
		populateBrowserLists();
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
	public HashMap<String, String> runScreenshotTest(String selectedBrowserList, String url) {
		String json = "";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("url", url);
		params.put("browser_list_name", selectedBrowserList);
			json = req.post("/", params);
		return parseResults(json);
	}
	private HashMap<String, String> parseResults(String json) {
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
	public boolean isTestRunning(String screenshotsTestId) throws IOException {
		String json = req.get("/"+screenshotsTestId);
		HashMap<String, String> results = parseResults(json);
		return Boolean.parseBoolean(results.get("active"));
	}
	
}
