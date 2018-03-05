package com.crossbrowsertesting.configurations;

public class Browser extends InfoPrototype {

	private String icon_class;
	private String browserName;
	private String version;


	@Deprecated
	public Browser(String api_name, String name, String icon_class) {
		super(api_name, name);
		this.icon_class = icon_class;
	}
	// for Screenshots
	public Browser(String api_name, String name, String icon_class, String device) {
		super(api_name, name, device);
		this.icon_class = icon_class;
	}
	// for Selenium Mobile
	public Browser(String api_name, String name, String icon_class, String device, String browserName) {
		super(api_name, name, device);
		this.icon_class = icon_class;
		this.browserName = browserName;
	}
	// for Selenium Desktop
	public Browser(String api_name, String name, String icon_class, String device, String browserName, String version) {
		super(api_name, name, device);
		this.icon_class = icon_class;
		this.browserName = browserName;
		this.version = version;
	}
	public String getIconClass() {
		return icon_class;
	}
	public String getBrowserName() {
		return browserName;
	}
	public String getVersion() {
		if (!isMobile()) {
			return version;
		} else {
			return "";
		}
	}

	public void setVersion(String version) {
		if (!isMobile()) {
			this.version = version;
		}
	}

}
