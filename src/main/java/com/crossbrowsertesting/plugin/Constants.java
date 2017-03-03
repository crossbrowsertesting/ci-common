package com.crossbrowsertesting.plugin;

public final class Constants {
	/*
	 * Class to define all of the constant values
	 * This class cannot be constructed
	 * All values are static
	 */
	
	// Environment variables
	public static final String USERNAME = "CBT_USERNAME";
	@Deprecated
	public static final String APIKEY = "CBT_APIKEY";
	public static final String AUTHKEY = "CBT_AUTHKEY";
	public static final String BUILDNAME = "CBT_BUILD_NAME";
	public static final String BUILDNUMBER = "CBT_BUILD_NUMBER";
	public static final String OPERATINGSYSTEM = "CBT_OPERATING_SYSTEM";
	public static final String BROWSER = "CBT_BROWSER";
	public static final String BROWSERS = "CBT_BROWSERS";
	public static final String RESOLUTION = "CBT_RESOLUTION";
	public static final String BROWSERNAME = "CBT_BROWSERNAME";
	
	// Display name for CI
	public static final String DISPLAYNAME = "Crossbrowsertesting.com";
	
	// Contributers
	public static final String TEAMCITY_CONTRIBUTER = "teamcity";
	public static final String JENKINS_CONTRIBUTER = "jenkins";
	
	// Local Tunnel messages
	public static final String TUNNEL_START_FAIL = "Failed to start Local Tunnel";
	public static final String TUNNEL_STOP_FAIL = "Failed to stop Local Tunnel";
	public static final String TUNNEL_START = "Started Local Tunnel";
	public static final String TUNNEL_STOP = "Stopped Local Tunnel";
	public static final String TUNNEL_NEED_TO_START = "Tunnel is currently not running. Need to start one.";
	public static final String TUNNEL_NO_NEED_TO_START = "Local Tunnel is already running. No need to start a new one.";
	public static final String TUNNEL_CONNECTED = "Tunnel is now connected.";
	public static final String TUNNEL_WAITING = "Waiting for the tunnel to establish a connection.";
	@Deprecated
	public static final String TUNNEL_USING = "Going to use tunnel";
	public static final String TUNNEL_USING_DEFAULT = "Going to use default tunnel";
	public static final String TUNNEL_USING_TUNNELNAME(String tunnelName) {
		return "Going to use tunnel with tunnelname \""+tunnelName+"\"";
	}
	public static final String AUTH_SUCCESS = "Successful Authentication";
	public static final String AUTH_FAIL = "Error: Bad username or authkey";
	
	// Legacy Tunnel Messages
	@Deprecated
	public static final String TUNNEL_NO_NEED_TO_START_MSG = "Local Tunnel is already running. No need to start a new one.";
	@Deprecated
	public static final String TUNNEL_STOP_FAIL_MSG = "Failed to stop Local Tunnel";
	@Deprecated
	public static final String TUNNEL_START_MSG = "Started Local Tunnel";
	@Deprecated
	public static final String TUNNEL_START_FAIL_MSG = "Failed to start Local Tunnel";
	@Deprecated
	public static final String TUNNEL_STOP_MSG = "Stopped Local Tunnel";
	// Selenium Messages
	public static final String SELENIUM_START_MSG = "\n---------------------\nSELENIUM TEST RESULTS\n---------------------";
	
	// Account Messages
	public static final String CREDENTIALS_INVALID_MSG = "Invalid username or apikey";
	
	private Constants(){
	    throw new AssertionError();
	  }
}
