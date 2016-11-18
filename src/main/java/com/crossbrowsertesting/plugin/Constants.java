package com.crossbrowsertesting.plugin;

public final class Constants {
	/*
	 * Class to define all of the constant values
	 * This class cannot be constructed
	 * All values are static
	 */
	
	// Environment variables
	public static final String USERNAME = "CBT_USERNAME";
	public static final String APIKEY = "CBT_APIKEY";
	public static final String BUILDNAME = "CBT_BUILD_NAME";
	public static final String BUILDNUMBER = "CBT_BUILD_NUMBER";
	public static final String OPERATINGSYSTEM = "CBT_OPERATING_SYSTEM";
	public static final String BROWSER = "CBT_BROWSER";
	public static final String RESOLUTION = "CBT_RESOLUTION";
	public static final String BROWSERNAME = "CBT_BROWSERNAME";
	
	// Contributers
	public static final String TEAMCITY_CONTRIBUTER = "teamcity";
	public static final String JENKINS_CONTRIBUTER = "jenkins";
	
	// Local Tunnel messages
	public static final String TUNNEL_START_FAIL_MSG = "Failed to start Local Tunnel";
	public static final String TUNNEL_STOP_FAIL_MSG = "Failed to stop Local Tunnel";
	public static final String TUNNEL_START_MSG = "Started Local Tunnel";
	public static final String TUNNEL_STOP_MSG = "Stopped Local Tunnel";
	public static final String TUNNEL_NO_NEED_TO_START_MSG = "Local Tunnel is already running. No need to start a new one.";
	
	// Selenium Messages
	public static final String SELENIUM_START_MSG = "\n---------------------\nSELENIUM TEST RESULTS\n---------------------";
	
	private Constants(){
	    throw new AssertionError();
	  }
}
