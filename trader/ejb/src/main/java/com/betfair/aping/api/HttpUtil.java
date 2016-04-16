package com.betfair.aping.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.apache.commons.httpclient.Header;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import com.betfair.aping.ApiNGDemo;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.JsonResponseHandler;
import com.betfair.aping.util.RescriptResponseHandler;
import org.apache.http.protocol.HTTP;

public class HttpUtil {

    private final String HTTP_HEADER_X_APPLICATION = "X-Application";
    private final String HTTP_HEADER_X_AUTHENTICATION = "X-Authentication";
    private final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private final String HTTP_HEADER_ACCEPT = "Accept";
    private final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    public HttpUtil() {
        super();
    }

    private boolean isSet(Object sysProperty) {
        return sysProperty != null
                && sysProperty.toString().trim().length() > 0;
    }

    private HttpClient createHttpClientOrProxy() {

        HttpClientBuilder hcBuilder = HttpClients.custom();

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setSocketTimeout(
                        new Integer(ApiNGDemo.getProp().getProperty("TIMEOUT"))
                                .intValue())
                .setConnectTimeout(
                        new Integer(ApiNGDemo.getProp().getProperty("TIMEOUT"))
                                .intValue()).build();

        // Set HTTP proxy, if specified in system properties
        if (isSet(System.getProperty("http.proxyHost"))) {
            int port = 80;
            if (isSet(System.getProperty("http.proxyPort"))) {
                port = Integer.parseInt(System.getProperty("http.proxyPort"));
            }
            org.apache.http.HttpHost proxy = new org.apache.http.HttpHost(
                    System.getProperty("http.proxyHost"), port);

            org.apache.http.client.CredentialsProvider credsProvider = new BasicCredentialsProvider();

            credsProvider.setCredentials(new AuthScope(proxy.getHostName(),
                    proxy.getPort()), new UsernamePasswordCredentials(
                    "fastcoder65", "imxDr5OZimxDr5OZ"));

            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
                    proxy);

            hcBuilder.setRoutePlanner(routePlanner);
            hcBuilder.setDefaultRequestConfig(requestConfig)
                    .setDefaultCredentialsProvider(credsProvider);
        }

        CloseableHttpClient httpClient = hcBuilder.build();

        return httpClient;
    }

    private String sendPostRequest(String param, String operation,
                                   String appKey, String ssoToken, String aURL,
                                   ResponseHandler<String> reqHandler) {
        String jsonRequest = param;

        HttpPost post = new HttpPost(aURL);

        String resp = null;
        try {
            post.setHeader(HTTP_HEADER_CONTENT_TYPE, ApiNGDemo.getProp()
                    .getProperty("APPLICATION_JSON"));
            post.setHeader(HTTP_HEADER_ACCEPT,
                    ApiNGDemo.getProp().getProperty("APPLICATION_JSON"));
            post.setHeader(HTTP_HEADER_ACCEPT_CHARSET, ApiNGDemo.getProp()
                    .getProperty("ENCODING_UTF8"));
            post.setHeader(HTTP_HEADER_X_APPLICATION, appKey);
            post.setHeader(HTTP_HEADER_X_AUTHENTICATION, ssoToken);

            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

            if (jsonRequest != null)
                post.setEntity(new StringEntity(jsonRequest, ApiNGDemo
                        .getProp().getProperty("ENCODING_UTF8")));

            HttpClientBuilder hcBuilder = HttpClients.custom();

            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setSocketTimeout(
                            new Integer(ApiNGDemo.getProp().getProperty(
                                    "TIMEOUT")).intValue())
                    .setConnectTimeout(
                            new Integer(ApiNGDemo.getProp().getProperty(
                                    "TIMEOUT")).intValue()).build();

            // Set HTTP proxy, if specified in system properties

            org.apache.http.HttpHost proxy = null;
            HttpClientContext context = HttpClientContext.create();

// http.proxyHost = 54.75.241.179
// http.proxyPort = 3128

            if (isSet(System.getProperty("http.proxyHost"))) {
                int port = 80;
                if (isSet(System.getProperty("http.proxyPort"))) {
                    port = Integer.parseInt(System
                            .getProperty("http.proxyPort"));
                }

                proxy = new org.apache.http.HttpHost(
                        System.getProperty("http.proxyHost"), port);

                CredentialsProvider credsProvider = new BasicCredentialsProvider();

                credsProvider.setCredentials(new AuthScope(proxy.getHostName(),
                        proxy.getPort()), new UsernamePasswordCredentials(
                        "fastcoder65", "imxDr5OZimxDr5OZ"));

                AuthCache authCache = new BasicAuthCache();

                DigestScheme asAuth = new DigestScheme();
                // Suppose we already know the realm name

                asAuth.overrideParamter("realm", "MyPrivateEc2Proxy");
                // Suppose we already know the expected nonce value
                asAuth.overrideParamter("nonce", "XPFezyhHcEWult89wHfh31Kei6O");

                authCache.put(proxy, asAuth);

                context.setCredentialsProvider(credsProvider);
                context.setAuthCache(authCache);

                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
                        proxy);

                hcBuilder.setRoutePlanner(routePlanner);
                hcBuilder.setDefaultRequestConfig(requestConfig)
                        .setDefaultCredentialsProvider(credsProvider);
            }

            CloseableHttpClient httpClient = hcBuilder.build();
            long startTime = System.currentTimeMillis();

            resp = httpClient.execute(post, reqHandler, context);

            long endTime = System.currentTimeMillis();

            for (org.apache.http.Header hdr : post.getAllHeaders()) {
                log.fine("found header: " + hdr.getName() + " = " + hdr.getValue());
            }

            log.fine("web request " + aURL + " executed in " + (endTime - startTime) /
                    1000.0 + " second(s).");
        } catch (org.apache.http.conn.HttpHostConnectException e1) {
            log.severe("method 'sendPostRequest' error: " + e1.getMessage());
        } catch (UnsupportedEncodingException e1) {
            // Do something
            //e1.printStackTrace();
            log.severe("method 'sendPostRequest' error: " + e1.getMessage());

        } catch (ClientProtocolException e) {
            // Do something
            // e.printStackTrace();
            log.severe("method 'sendPostRequest' error: " + e.getMessage());

        } catch (IOException ioE) {
            // Do something
            log.severe("method 'sendPostRequest' error: " + ioE.getMessage());
        }

        return resp;

    }

    public String sendPostRequestRescript(String param, String operation,
                                          String appKey, String ssoToken) throws APINGException {
        String apiNgURL = ApiNGDemo.getProp().getProperty("APING_URL")
                + ApiNGDemo.getProp().getProperty("RESCRIPT_SUFFIX")
                + operation + "/";

        return sendPostRequest(param, operation, appKey, ssoToken, apiNgURL,
                new RescriptResponseHandler());

    }

    public String sendPostRequestJsonRpc(String param, String operation,
                                         String appKey, String ssoToken) {
        String apiNgURL = ApiNGDemo.getProp().getProperty("APING_URL")
                + ApiNGDemo.getProp().getProperty("JSON_RPC_SUFFIX");

        return sendPostRequest(param, operation, appKey, ssoToken, apiNgURL,
                new JsonResponseHandler());

    }

    public String sendKeepAlivePostRequest(String appKey, String ssoToken) {
        String apiNgURL = "https://identitysso.betfair.com/api/keepAlive"; // ApiNGDemo.getProp().getProperty("INTER_KA_URL");

        return sendPostRequest(null, null, appKey, ssoToken, apiNgURL,
                new JsonResponseHandler());
    }

    public String sendLogoutRequest(String appKey, String ssoToken) {
        String apiNgURL = "https://identitysso.betfair.com/api/logout"; // ApiNGDemo.getProp().getProperty("INTER_KA_URL");

        return sendPostRequest(null, null, appKey, ssoToken, apiNgURL,
                new JsonResponseHandler());
    }

}
