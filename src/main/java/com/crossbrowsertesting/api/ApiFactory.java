package com.crossbrowsertesting.api;

public abstract class ApiFactory {
	// Abract class for things common in API Requests
	protected Request req;
	private String url;
	
	public ApiFactory(String url) {
		this.url = url;
		this.req = new Request(url);
	}
	
	public ApiFactory(String url, String username, String password) {
		this.url = url;
		this.req = new Request(url, username, password);
	}
	
	public void setRequest(String username, String apikey) {
		req = new Request(url, username, apikey);
	}
	
	public Request getRequest() {
		return req;
	}
	
}
