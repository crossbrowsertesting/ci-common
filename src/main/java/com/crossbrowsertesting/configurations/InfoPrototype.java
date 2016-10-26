package com.crossbrowsertesting.configurations;

abstract class InfoPrototype {
	/*
	 * Almost all of the JSON Objects have a "name" and "api_name"
	 * Doing it this way just because I'm lazy
	 */
	private String name;
	private String api_name;

	public InfoPrototype(String api_name, String name) {
		this.api_name = api_name;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public String getApiName() {
		return api_name;
	}

	public String toString() {
		return api_name;
	}
}
