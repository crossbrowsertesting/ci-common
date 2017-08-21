package com.crossbrowsertesting.api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;

import java.util.Collections;
import java.util.Map;

public class UnirestRequest {
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
    private HttpRequest req;

    UnirestRequest(String path, String username, String password) {
        this.username = username;
        this.password = password;
        init(path);
    }
    public UnirestRequest(String path) {
        init(path);
    }
    private void init(String path) {
        requestURL += path;
        // ignore cookies
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        Unirest.setHttpClient(httpclient);
    }
    public void setProxy(String url, int port) {
        this.proxyUrl = url;
        this.proxyPort = port;
        this.useProxy = true;
        Unirest.setProxy(new HttpHost(this.proxyUrl, this.proxyPort));
    }
    public void setProxyCredentials(String username, String password) {
        this.proxyUsername = username;
        this.proxyPassword = password;
        this.useProxyCredentials = true;

        // work around to set credentials
        // https://stackoverflow.com/questions/40864167/how-to-set-credentials-for-unirest-proxy
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.proxyUsername, this.proxyPassword));
        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(new HttpHost(this.proxyUrl, this.proxyPort));
        clientBuilder.setDefaultCredentialsProvider(credsProvider);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        Lookup<AuthSchemeProvider> authProviders = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .build();
        clientBuilder.setDefaultAuthSchemeRegistry(authProviders);
        Unirest.setHttpClient(clientBuilder.build()); //method name change from reference
    }
    private String doRequest(final String METHOD, HttpRequest req, Map<String, String> params) {
        if (username != null && password != null) {
            req = req.basicAuth(username, password);
        }
        try {
            if (params != null) {
                if (METHOD.toUpperCase().equals("GET")) {
                    req = req.queryString(Collections.<String, Object>unmodifiableMap(params));
                } else {
                    return ((HttpRequestWithBody) req).fields(Collections.<String, Object>unmodifiableMap(params)).asJson().getBody().toString();
                }
            }
            return req.asJson().getBody().toString();
        } catch (UnirestException e) {
            return "";
        }
    }
    public String get(String urlStr) {
        return get(urlStr, null);
    }
    public String get(String urlStr, Map<String, String> params) {
        String url = requestURL + urlStr;
        HttpRequest req = Unirest.get(url);
        return doRequest("GET", req, params);
    }
    public String post(String urlStr) {
        return post(urlStr, null);
    }
    public String post(String urlStr, Map<String, String> params) {
        String url = requestURL + urlStr;
        HttpRequestWithBody req = Unirest.post(url);
        return doRequest("POST", req, params);
    }
    public String put(String urlStr) {
        return put(urlStr, null);
    }
    public String put(String urlStr, Map<String, String> params) {
        String url = requestURL + urlStr;
        HttpRequestWithBody req = Unirest.put(url);
        return doRequest("PUT", req, params);
    }
    public String delete(String urlStr) {
        return delete(urlStr, null);
    }
    public String delete(String urlStr, Map<String, String> params) {
        String url = requestURL + urlStr;
        HttpRequestWithBody req = Unirest.delete(url);
        return doRequest("DELETE", req, params);
    }
}
