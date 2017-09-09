package com.crossbrowsertesting.api;

import com.crossbrowsertesting.configurations.Browser;
import com.crossbrowsertesting.configurations.OperatingSystem;
import com.crossbrowsertesting.configurations.Resolution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class TestTypeApiFactory extends ApiFactory{

    @Deprecated
    public List<OperatingSystem> operatingSystems;
    public Map<String, OperatingSystem> operatingSystems2; //getting from a Map is O(1)
    public String configurationsAsJson = "";

    public TestTypeApiFactory(String url) {
        super(url);
    }
    public TestTypeApiFactory(String url, String username, String password) {
        super(url, username, password);
    }

    public void init() {
        operatingSystems = new LinkedList<OperatingSystem>();
        operatingSystems2 = new HashMap<String, OperatingSystem>();
        configurationsAsJson = "";
        populateBrowsers();
    }
    @Deprecated
    protected void populateConfigurations(String json) throws JSONException {
        configurationsAsJson = json; // for TeamCity
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
    private void populateBrowsers() {
        String json="";
        json = req.get("/browsers");
        try {
            operatingSystems = new LinkedList<OperatingSystem>();
            operatingSystems2 = new HashMap<String, OperatingSystem>();
        }catch (JSONException jsone) {}
        configurationsAsJson = json; // for TeamCity
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
    protected Browser getBrowserInfo(OperatingSystem config, String browserApiName) {
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
    protected String getBrowserName(OperatingSystem config, String browserApiName) {
        Browser browser = getBrowserInfo(config, browserApiName);
        return browser.getName();
    }
}
