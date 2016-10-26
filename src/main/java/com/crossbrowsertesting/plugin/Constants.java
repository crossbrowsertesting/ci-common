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
	
	
	public static final String TEAMCITY_CONTRIBUTER = "teamcity";
	public static final String JENKINS_CONTRIBUTER = "jenkins";
	
	private Constants(){
	    throw new AssertionError();
	  }
}
