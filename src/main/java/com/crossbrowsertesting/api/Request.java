package com.crossbrowsertesting.api;

import java.io.*;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

/*
 * Instantiate a Request object to make api requests
 * Not intended to be directly imported and instantiated into your project
 * Might consider making it abstract and having API classes like "Selenium" or "Screenshots" extend from it
 */
@Deprecated
public class Request {

	String username = null;
	String password = null;

	//proxy settings
	boolean useProxy = false;
	String proxyUrl;
	int proxyPort;
	boolean useProxyCredentials = false;
	String proxyUsername;
	String proxyPassword;

	private String requestURL = "https://crossbrowsertesting.com/api/v3/";

	Request(String path, String username, String password) {
		this.username = username;
		this.password = password;

		requestURL += path;
	}
	public Request(String path) {
		requestURL += path;
	}
	public void setProxy(String url, int port) {
		this.proxyUrl = url;
		this.proxyPort = port;
		useProxy = true;
	}
	public void setProxyCredentials(String username, String password) {
		this.proxyUsername = username;
		this.proxyPassword = password;
		useProxyCredentials = true;
	}

	public String get(String urlStr) {
		/*
		 * Get request
		 * returns JSON as a string
		 */
		try {
			String requestString = requestURL + urlStr;
			URL url = new URL(requestString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (useProxy) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
				conn = (HttpURLConnection) url.openConnection(proxy);
				if (useProxyCredentials) {
					Authenticator.setDefault(new SimpleAuthenticator(proxyUsername, proxyPassword));
				}
			}
			conn.setRequestMethod("GET");
			if (username != null && password != null) {
				String userpassEncoding = Base64.encodeBase64String((username + ":" + password).getBytes());
				conn.setRequestProperty("Authorization", "Basic " + userpassEncoding);
			}
			if (conn.getResponseCode() != 200) {
				throw new IOException("Server Response: " + conn.getResponseCode() + " " + conn.getResponseMessage());
			}
			// Buffer the result into a string
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			return sb.toString();
		} catch(IOException ioe) {
			return ioe.getMessage();
		}
	}
	public String get(String urlStr, Map<String, String> params){
		urlStr += "?";
		int index = 1;
		try {

			for (Map.Entry<String, String> entry : params.entrySet()) {
				urlStr += entry.getKey() +"=" + URLEncoder.encode(entry.getValue(), "UTF-8");
				if (index < params.size()) {
    				urlStr += "&";
    			}
    			index++;
			}
		} catch (UnsupportedEncodingException e) {}
		return get(urlStr);
	}
	private String doRequestWithFormParams(String method, String urlStr, Map<String, Object> params) {
		/*
		 * any general request with form data parameters ie. POST, DELETE, PUT
		 * returns JSON as a string
		 */
		String urlParameters = "";
		try {
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (!urlParameters.isEmpty()) {
						urlParameters += "&";
					}
					if (value instanceof Collection) {
						for (String listValue : (Collection<String>) value) {
							if (!urlParameters.isEmpty()) {
								urlParameters += "&";
							}
							urlParameters += key + "=" + listValue;
						}
					} else {
						urlParameters += key + "=" + value;
					}
				}
			}
			/*
			String urlParameters = "";
			if (params != null) {
				int index = 1;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					urlParameters += entry.getKey() + "=" + entry.getValue();
					if (index < params.size()) {
						urlParameters += "&";
					}
					index++;
				}
			}
			*/
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			URL url = new URL(requestURL + urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (useProxy) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
				conn = (HttpURLConnection) url.openConnection(proxy);
				if (useProxyCredentials) {
					Authenticator.setDefault(new SimpleAuthenticator(proxyUsername, proxyPassword));
				}
			}
			conn.setRequestMethod(method);
			if (username != null && password != null) {
				String userpassEncoding = Base64.encodeBase64String((username + ":" + password).getBytes());
				conn.setRequestProperty("Authorization", "Basic " + userpassEncoding);
			}
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			//wr.writeBytes(urlParameters);
			wr.write(postData);
			wr.flush();
			wr.close();
			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}
			// Buffer the result into a string
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			return sb.toString();
		}catch (IOException ioe) {
			ioe.printStackTrace();
			return "";
		}
	}
	public String post(String urlStr, Map<String, String> params){
		return doRequestWithFormParams("POST", urlStr, Collections.<String, Object>unmodifiableMap(params));
	}
	public String post(String urlStr, Map<String, Object> params, boolean containsLists) {
		String url = requestURL + urlStr;
		return doRequestWithFormParams("POST", urlStr, params);
	}
	public String put(String urlStr, Map<String, String> params){
		return doRequestWithFormParams("PUT", urlStr, Collections.<String, Object>unmodifiableMap(params));
	}
	public String delete(String urlStr) {
		return delete(urlStr, null, false);
	}
	public String delete(String urlStr, Map<String, String> params){
		return doRequestWithFormParams("DELETE", urlStr, Collections.<String, Object>unmodifiableMap(params));
	}
	public String delete(String urlStr, Map<String, Object> params, boolean containsLists) {
		return doRequestWithFormParams("DELETE", urlStr, params);
	}

}
final class SimpleAuthenticator extends Authenticator {
	private String username;
	private String password;
	SimpleAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}
	protected PasswordAuthentication getPasswordAuthtication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}
}
