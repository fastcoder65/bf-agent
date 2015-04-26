package com.betfair.aping.util;

/**
 * Returns session token 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import my.pack.util.AccountConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientSSO {

	private static final int PORT = 443;
	private static final Logger log = LoggerFactory.getLogger(HttpClientSSO.class);
	
	public static String getSessionTokenResponse(String appKey, String userName, String password) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String responseString = null;
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			KeyManager[] keyManagers = getKeyManagers("pkcs12",
					new FileInputStream(new File(AccountConstants.PATH_TO_PRIVATE_KEY)),
					"1q2w3e4r5t");
			ctx.init(keyManagers, null, new SecureRandom());
			SSLSocketFactory factory = new SSLSocketFactory(ctx,
					new StrictHostnameVerifier());

			ClientConnectionManager manager = httpClient.getConnectionManager();
			manager.getSchemeRegistry().register(
					new Scheme("https", PORT, factory));
			HttpPost httpPost = new HttpPost(
					AccountConstants.ENDPOINT_TO_CERTLOGIN);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			nvps.add(new BasicNameValuePair("username", userName));
			nvps.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			httpPost.setHeader("X-Application", appKey);
			log.info("executing request {}",  httpPost.getRequestLine());

			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			log.info("Response status line: {}",response.getStatusLine());
			if (entity != null) {
				responseString = EntityUtils.toString(entity);
				log.info("Session token: {}", responseString);
			}
		} catch (Exception e) {
			log.error("Exception while get sessionToken: {}", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return responseString;
	}

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