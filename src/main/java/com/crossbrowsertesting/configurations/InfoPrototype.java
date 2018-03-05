package com.crossbrowsertesting.configurations;

abstract class InfoPrototype {
	/*
	 * Almost all of the JSON Objects have a "name" and "api_name"
	 * Doing it this way just because I'm lazy
	 */
	private String name;
	private String api_name;
	private String device = "";

	public InfoPrototype(String api_name, String name) {
		this.api_name = api_name;
		this.name = name;
	}
	public InfoPrototype(String api_name, String name, String device) {
		this.api_name = api_name;
		this.name = name;
		this.device = device;
	}
	public String getName() {
		return name;
	}
	public String getApiName() {
		return api_name;
	}

	public boolean isMobile() {
		if (device.equals("mobile")) {
			return true;
		} else {
			return false;
		}
	}
	public String toString() {
		return api_name;
	}
}
