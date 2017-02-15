package com.crossbrowsertesting.api;

import java.io.IOException;

import org.json.JSONObject;

public class Account extends ApiFactory {
	public boolean connectionSuccessful = false;

	public Account(String username, String apikey) {
		super("account", username, apikey);
	}
	@Override
	public void init() {
		testConnection();
	}
	public boolean testConnection() {
		int userId = 0;
		String email = "";
		try {
			String json = req.get("");
			JSONObject results = new JSONObject(json);
			userId = results.getInt("user_id");
			email = results.getString("email");
			if (userId > 0 && email != null && !email.equals("")) {
				connectionSuccessful = true;
				return true;
			} else {
				connectionSuccessful = false;
				return false;
			}
			
		} catch (IOException e) {
			connectionSuccessful = false;
			return false;
		}
	}	
}
