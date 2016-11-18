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
	
	public OperatingSystem(String api_name, String name) {
		super(api_name, name);			
	}
}