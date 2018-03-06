package com.crossbrowsertesting.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class Account extends ApiFactory {
	public boolean connectionSuccessful = false;

	private final static Logger log = Logger.getLogger(Account.class.getName());

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
			String json = req.get("");
			try {
				JSONObject results = new JSONObject(json);
				userId = results.getInt("user_id");
				email = results.getString("email");
				if (userId > 0 && email != null && !email.equals("")) {
					log.info("successful connection");
					connectionSuccessful = true;
					return true;
				} else {
					log.info("unsuccessful connection");
					connectionSuccessful = false;
					return false;
				}
			} catch(JSONException je) {
				log.warning("caught exeception parsing JSON");
				connectionSuccessful = false;
				return false;
			}

	}
	public boolean sendMixpanelEvent(String eventName) {
		// used for analytics with MixPanel
		String json = "";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("event_name", eventName);
			json = req.post("/sendMixpanelEvent", params);
		if (json.isEmpty()) {
			return false;
		} else {
			return true;
		}
		
	}
}
