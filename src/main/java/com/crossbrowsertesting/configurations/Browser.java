package com.crossbrowsertesting.configurations;

public class Browser extends InfoPrototype {

	private String icon_class;
	
	public Browser(String api_name, String name, String icon_class) {
		super(api_name, name);
		this.icon_class = icon_class;
		// TODO Auto-generated constructor stub
	}
	public String getIconClass() {
		return icon_class;
	}

}
