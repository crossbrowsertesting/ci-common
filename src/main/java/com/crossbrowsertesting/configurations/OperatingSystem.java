package com.crossbrowsertesting.configurations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatingSystem extends InfoPrototype{
	
	@Deprecated
	public List<Resolution> resolutions = new ArrayList<Resolution>();
	@Deprecated
	public List<Browser> browsers = new ArrayList<Browser>();
	
	// keys are api_names - values are names
	public Map<String, Resolution> resolutions2 = new HashMap<String, Resolution>(); 
	public Map<String, Browser> browsers2 = new HashMap<String, Browser>();
	private String platform;
	private String deviceName;
	private String platformVersion;
	private String platformName;


	@Deprecated
	public OperatingSystem(String api_name, String name) {
		super(api_name, name);
	}
	// for Screenshots
	public OperatingSystem(String api_name, String name, String device) {
		super(api_name, name, device);
	}
	// for Selenium Desktop
	public OperatingSystem(String api_name, String name, String device, String platform) {
		super(api_name, name, device);
		setPlatform(platform);
	}
	// for Selenium Mobile
	public OperatingSystem(String api_name, String name, String device, String deviceName, String platformVersion, String platformName) {
		super(api_name, name, device);
		setDeviceName(deviceName);
		setPlatformVersion(platformVersion);
		setPlatformName(platformName);
	}

	public String getPlatform() {
		if (!isMobile()) {
			return this.platform;
		} else {
			return "";
		}
	}
	public String getDeviceName() {
		if (isMobile()) {
			return this.deviceName;
		} else {
			return "";
		}
	}
	public String getPlatformVersion() {
		if (isMobile()) {
			return this.platformVersion;
		} else {
			return "";
		}
	}
	public String getPlatformName() {
		if (isMobile()) {
			return this.platformName;
		} else {
			return "";
		}
	}

	public void setPlatform(String platform) {
		if (!isMobile()) {
			this.platform = platform;
		}
	}
	public void setDeviceName(String deviceName) {
		if (isMobile()) {
			this.deviceName = deviceName;
		}
	}
	public void setPlatformName(String platformName) {
		if (isMobile()) {
			this.platformName = platformName;
		}
	}
	public void setPlatformVersion(String platformVersion) {
		if (isMobile()) {
			this.platformVersion = platformVersion;
		}
	}
}