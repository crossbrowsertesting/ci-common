package com.crossbrowsertesting.api;

public abstract class ApiFactory {
	// Abract class for things common in API Requests
	protected UnirestRequest req;
	private String url;
	
	public ApiFactory(String url) {
		this.url = url;
		this.req = new UnirestRequest(url);
	}
	
	public ApiFactory(String url, String username, String password) {
		this.url = url;
		this.req = new UnirestRequest(url, username, password);
	}
	
	public void setRequest(String username, String apikey) {
		req = new UnirestRequest(url, username, apikey);
	}
	
	public UnirestRequest getRequest() {
		return req;
	}
	public boolean useProxy() {
		return req.useProxy;
	}
	public int proxyPort() {
		return req.proxyPort;
	}
	public String proxyUrl() {
		return req.proxyUrl;
	}
	public boolean useProxyCredentials() {
		return req.useProxyCredentials;
	}
	public String proxyUsername() {
		return req.proxyUsername;
	}
	public String proxyPassword() {
		return req.proxyPassword;
	}
	public abstract void init();
}
