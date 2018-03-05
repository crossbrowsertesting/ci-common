package com.crossbrowsertesting.configurations;

public class Resolution extends InfoPrototype {

	private String screenResolution;
	private String deviceOrientation = "";

	@Deprecated
	public Resolution(String name) {
		super(name, name);
		this.screenResolution = name;
	}
	public Resolution(String name, String device) {
		super(name, name, device);
		this.screenResolution = name;
	}
	public Resolution(String name, String device, String deviceOrientation) {
		super(name, name, device);
		this.deviceOrientation = deviceOrientation;
		this.screenResolution = name;
	}

	public String getDeviceOrientation() {
		if (isMobile()) {
			return deviceOrientation;
		} else {
			return "";
		}
	}
	public String getScreenResolution() {
		return screenResolution;
	}
	public void setDeviceOrientation(String deviceOrientation) {
		this.deviceOrientation = deviceOrientation;
	}
}
