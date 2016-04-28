package com.betfair.aping.util;

/**
 * Returns session token 
 */

import my.pack.util.AccountConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


// import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientSSO {

	private static final int PORT = 443;
	private static final Logger log = Logger.getLogger(HttpClientSSO.class.getName());

	private static void printLog(String logMessage) {
		if (log.isLoggable(Level.FINE)) {
			log.fine (logMessage);
		}
	}

	public static void printLog(Level level , String logMessage, Throwable t) {
		if (log.isLoggable(level)) {
			log.log(level, logMessage, t);
		}
	}

	public static void logError(String logMessage, Throwable t) {
		printLog(Level.SEVERE, logMessage, t);
	}

	private static boolean isSet(Object sysProperty) {
		return sysProperty != null
				&& sysProperty.toString().trim().length() > 0;
	}

	public static String getSessionTokenResponse(String appKey, String userName, String password) {

		String responseString = null;
		HttpClientBuilder hcBuilder = HttpClients.custom();

		org.apache.http.HttpHost proxy = null;

		// Set HTTP proxy, if specified in system properties
		if (isSet(System.getProperty("http.proxyHost"))) {
			int port = 80;
			if (isSet(System.getProperty("http.proxyPort"))) {
				port = Integer.parseInt(System.getProperty("http.proxyPort"));
			}
			proxy = new org.apache.http.HttpHost(
					System.getProperty("http.proxyHost"), port);

			org.apache.http.client.CredentialsProvider credsProvider = new BasicCredentialsProvider();

			credsProvider.setCredentials(
					new AuthScope(proxy.getHostName(), proxy.getPort(),
							AuthScope.ANY_REALM, AuthScope.ANY_SCHEME),

					new UsernamePasswordCredentials("fastcoder65",
							"imxDr5OZimxDr5OZ"));

			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
					proxy);

			hcBuilder.setRoutePlanner(routePlanner);
			hcBuilder.setDefaultCredentialsProvider(credsProvider);
			hcBuilder.setProxy(proxy);
		}

		
		 CloseableHttpClient httpClient = null;

		try {
			
			SSLContext ctx = SSLContext.getInstance("TLS");

			KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(new File(AccountConstants.PATH_TO_PRIVATE_KEY)),"1q2w3e4r5t");

			ctx.init(keyManagers, null, new SecureRandom());

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);

			RequestConfig.Builder requestBuilder = RequestConfig.custom();
			requestBuilder = requestBuilder.setConnectTimeout(15 * 1000);
			requestBuilder = requestBuilder.setConnectionRequestTimeout(15 * 1000);

			hcBuilder.setDefaultRequestConfig(requestBuilder.build());

			httpClient = hcBuilder.setSSLSocketFactory(sslsf).build();


			HttpClientContext localContext = null;

			if (proxy != null) {
				// Create AuthCache instance
				AuthCache authCache = new BasicAuthCache();
				// Generate DIGEST scheme object, initialize it and add it to
				// the local
				// auth cache
				DigestScheme digestAuth = new DigestScheme();
				// Suppose we already know the realm name
				digestAuth.overrideParamter("realm", "MyPrivateEc2Proxy");
				// Suppose we already know the expected nonce value
				 digestAuth.overrideParamter("nonce", "XPFezyhHcEWult89wHfh31Kei6O");
				authCache.put(proxy, digestAuth);

				// Add AuthCache to the execution context
				localContext = HttpClientContext.create();
				localContext.setAuthCache(authCache);

			}  

		//	RequestConfig requestConfig = localContext.getRequestConfig();


			HttpPost httpPost = new HttpPost(AccountConstants.ENDPOINT_TO_CERTLOGIN);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			nvps.add(new BasicNameValuePair("username", userName));
			nvps.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			httpPost.setHeader("X-Application", appKey);
			httpPost.setHeader("Accept-Encoding", "gzip, deflate");
			httpPost.setHeader("Keep-Alive", "1, timeout=120, max=100");


			printLog("executing request " + httpPost.getRequestLine());

			HttpResponse response = httpClient.execute(httpPost, localContext);
			
			// HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			printLog("Response status line: " + response.getStatusLine());
			if (entity != null) {
				responseString = EntityUtils.toString(entity);
				printLog("Session token: " + responseString);
			}
		} catch (Exception e) {
			logError("Exception while get sessionToken: ", e);
		} finally {
			try {
				if ( httpClient != null )
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return responseString;
	}


	public static String getSessionTokenResponse2(String appKey,
			String userName, String password) {

		String responseString = null;
		HttpClientBuilder hcBuilder = HttpClients.custom();

		org.apache.http.HttpHost proxy = null;

		// Set HTTP proxy, if specified in system properties
		if (isSet(System.getProperty("http.proxyHost"))) {
			int port = 80;
			if (isSet(System.getProperty("http.proxyPort"))) {
				port = Integer.parseInt(System.getProperty("http.proxyPort"));
			}
			proxy = new org.apache.http.HttpHost(System.getProperty("http.proxyHost"), port);
			org.apache.http.client.CredentialsProvider credsProvider = new BasicCredentialsProvider();

			credsProvider.setCredentials(
					new AuthScope(proxy.getHostName(), proxy.getPort(),
							AuthScope.ANY_REALM, AuthScope.ANY_SCHEME),

					new UsernamePasswordCredentials("fastcoder65",
							"imxDr5OZimxDr5OZ"));

			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
					proxy);

			hcBuilder.setRoutePlanner(routePlanner);
			hcBuilder.setDefaultCredentialsProvider(credsProvider);
		}

		CloseableHttpClient httpClient = hcBuilder.build();

		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream ( new File ( AccountConstants.PATH_TO_PRIVATE_KEY )), "1q2w3e4r5t");
			ctx.init(keyManagers, null, new SecureRandom());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);

			// SSLSocketFactory factory = new SSLSocketFactory(ctx, new
			// StrictHostnameVerifier());

			// ClientConnectionManager manager =
			// httpClient.getConnectionManager();
			// HttpClientConnectionManager manager =
			// httpClient.getConnectionManager();
			
			Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("https", sslsf)
					.build();
			

			HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
			
			HttpClients.custom().setConnectionManager(cm).build();

			HttpClientContext localContext = null;

			if (proxy != null) {
				// Create AuthCache instance
				AuthCache authCache = new BasicAuthCache();
				// Generate DIGEST scheme object, initialize it and add it to
				// the local
				// auth cache
				DigestScheme digestAuth = new DigestScheme();
				// Suppose we already know the realm name
				digestAuth.overrideParamter("realm", "MyPrivateEc2Proxy");
				// Suppose we already know the expected nonce value
				digestAuth.overrideParamter("nonce",
						"XPFezyhHcEWult89wHfh31Kei6O");
				authCache.put(proxy, digestAuth);

				// Add AuthCache to the execution context
				localContext = HttpClientContext.create();
				localContext.setAuthCache(authCache);

			}  
			
			RequestConfig config = RequestConfig.custom()
	                .setProxy(proxy)
	                .build();

			HttpPost httpPost = new HttpPost(
					AccountConstants.ENDPOINT_TO_CERTLOGIN);
			httpPost.setConfig(config);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			nvps.add(new BasicNameValuePair("username", userName));
			nvps.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			httpPost.setHeader("X-Application", appKey);
			httpPost.setHeader("Accept-Encoding", "gzip, deflate");
			httpPost.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

			printLog("executing request {}" + httpPost.getRequestLine());

			HttpResponse response = httpClient.execute(httpPost, localContext);
			HttpEntity entity = response.getEntity();

			printLog("Response status line: " + response.getStatusLine());
			if (entity != null) {
				responseString = EntityUtils.toString(entity);
				printLog("Session token: " + responseString);
			}
		} catch (Exception e) {
			logError("Exception while get sessionToken: ", e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return responseString;
	}

	/*
	 * public static String getSessionTokenResponse_old(String appKey, String
	 * userName, String password) {
	 * 
	 * 
	 * DefaultHttpClient httpClient = new DefaultHttpClient();
	 * 
	 * String responseString = null; try { SSLContext ctx =
	 * SSLContext.getInstance("TLS"); KeyManager[] keyManagers =
	 * getKeyManagers("pkcs12", new FileInputStream(new
	 * File(AccountConstants.PATH_TO_PRIVATE_KEY)),"1q2w3e4r5t");
	 * ctx.init(keyManagers, null, new SecureRandom());
	 * 
	 * SSLSocketFactory factory = new SSLSocketFactory(ctx, new
	 * StrictHostnameVerifier());
	 * 
	 * ClientConnectionManager manager = httpClient.getConnectionManager();
	 * 
	 * manager.getSchemeRegistry().register(new Scheme("https", PORT, factory));
	 * 
	 * HttpPost httpPost = new HttpPost(
	 * AccountConstants.ENDPOINT_TO_CERTLOGIN); List<NameValuePair> nvps = new
	 * ArrayList<NameValuePair>();
	 * 
	 * nvps.add(new BasicNameValuePair("username", userName)); nvps.add(new
	 * BasicNameValuePair("password", password));
	 * 
	 * httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	 * httpPost.setHeader("X-Application", appKey);
	 * log.info("executing request {}", httpPost.getRequestLine());
	 * 
	 * HttpResponse response = httpClient.execute(httpPost); HttpEntity entity =
	 * response.getEntity();
	 * 
	 * log.info("Response status line: {}",response.getStatusLine()); if (entity
	 * != null) { responseString = EntityUtils.toString(entity);
	 * log.info("Session token: {}", responseString); } } catch (Exception e) {
	 * log.error("Exception while get sessionToken: {}", e); } finally {
	 * httpClient.getConnectionManager().shutdown(); } return responseString; }
	 */

	private static KeyManager[] getKeyManagers(String keyStoreType,
			InputStream keyStoreFile, String keyStorePassword) throws Exception {
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(keyStore, keyStorePassword.toCharArray());
		return kmf.getKeyManagers();
	}
}