package net.bir2.multitrade.util.url;

import java.util.logging.*;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

/**
 * https://sso-m.marathonbet.com/sso; https://192.168.110.57/sso;
 * http://192.168.110.57/sso Created by IntelliJ IDEA. User:
 * Mikhail.Tchivozercev Date: 08.04.2008 Time: 13:41:16 Instances of this class
 * read contents of passed to constructor URL and can return it as String if
 * client invoke getResourceAsString function.
 */

public class URLContentGetter {
	
//	private static final Logger log = Logger.getLogger(URLContentGetter.class);

	@Inject
    private Logger log;

	
	private static final URLContentGetter __instance = new URLContentGetter();
	private static final String TRUST_ALL_CERTS = "trustAll";
	private static final int HTTP_CON_TIMEOUT = 20 * 1000; // 10 s - default
															// http connect
															// timeout
	private static final int READ_TIMEOUT = 10 * 1000; // 5 s - default stream
														// read timeout

	private static final String PROP_CON_TIMEOUT = "net.bir.contimeout";
	private static final String PROP_READ_TIMEOUT = "net.bir.readTimeout";

	private void commonExceptionProcess(String errorMessage, Exception e)
			throws URLContentGetterException {
		throw new URLContentGetterException(errorMessage + ", "
				+ e.getMessage());
	}

	private void throwLocalException(String errorMessage)
			throws URLContentGetterException {
		throw new URLContentGetterException(errorMessage);
	}

	private URLContentGetter() {
	}

	public static URLContentGetter getInstance() {
		return __instance;
	}

	public String getURLContentAsString(String path)
			throws URLContentGetterException {
		
		String sConTimeout = System.getProperty(PROP_CON_TIMEOUT);
		int httpTimeout = (sConTimeout != null) ? Integer.valueOf(sConTimeout)
				: HTTP_CON_TIMEOUT;
		String sReadTimeout = System.getProperty(PROP_READ_TIMEOUT);
		int readTimeout = (sReadTimeout != null) ? Integer
				.valueOf(sReadTimeout) : READ_TIMEOUT;
		return getURLContentAsString(path, httpTimeout, readTimeout);
	}

	public String getHttpUrlContentAsString(String path, String data2send,
			String httpMethod) throws URLContentGetterException {
		String sConTimeout = System.getProperty(PROP_CON_TIMEOUT);
		int httpTimeout = (sConTimeout != null) ? Integer.valueOf(sConTimeout)
				: HTTP_CON_TIMEOUT;
		String sReadTimeout = System.getProperty(PROP_READ_TIMEOUT);
		int readTimeout = (sReadTimeout != null) ? Integer
				.valueOf(sReadTimeout) : READ_TIMEOUT;
		return getHttpUrlContentAsString(path, data2send, httpMethod,
				httpTimeout, readTimeout);

	}

	public String getHttpUrlContentAsString(String path, String data2send,
			String httpMethod, int connectionTimeout, int readTimeout)
			throws URLContentGetterException {

		InputStream inURLContent = null;
		InputStreamReader inDecodedURLContent = null;
		BufferedReader urlReader = null;
		String result = null;
		HttpURLConnection connection = null;

		log.log(Level.WARNING, "requested URL: \"" + path + '\"');
		log.log(Level.WARNING, "data sent: \"" + data2send + '\"');

		try {
			URL url = null;
			try {
				url = new URL(path);
			} catch (MalformedURLException e) {
				commonExceptionProcess("Bad URL \"" + path + '\"', e);
			}

			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					if (urlHostName != null
							&& urlHostName.equals(session.getPeerHost())) {
						return true;
					} else {
						log.log(Level.WARNING, "Warning: URL Host: " + urlHostName + " vs. "
								+ session.getPeerHost());
						return false;
					}
				}
			};

			String sTrustAll = System.getProperty(TRUST_ALL_CERTS);
			if (sTrustAll != null && "true".equals(sTrustAll)) {
				try {
					trustAllHttpsCertificates();
				} catch (Exception e) {
					log.severe(e.getMessage());
				}
			}

			try {
				if (sTrustAll != null && "true".equals(sTrustAll)) {
					HttpsURLConnection.setDefaultHostnameVerifier(hv);
				}

				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(connectionTimeout);
				connection.setReadTimeout(readTimeout);

				connection.setRequestMethod(httpMethod);
				connection
						.setRequestProperty("Content-type", "text/xml; utf-8");

				connection.setDoInput(true);
				connection.setDoOutput(true);
				if (data2send != null) {
					connection.setRequestProperty("Content-Length", String.valueOf(data2send.getBytes().length));

					OutputStream os = connection.getOutputStream();

					PrintWriter out = null;
					try {
						out = new PrintWriter(os);
						out.print(data2send);
					} finally {
						if (out != null) {
							out.flush();
							out.close();
						}
					}
				}
				inURLContent = connection.getInputStream();

			} catch (IOException e) {
				commonExceptionProcess("Can't open URL \"" + path + '\"', e);
			}

			if (inURLContent == null)
				throwLocalException(" No input from URL");

			try {
				inDecodedURLContent = new InputStreamReader(inURLContent,
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				commonExceptionProcess(" Can't find decoding character scheme",
						e);
			}

			urlReader = new BufferedReader(inDecodedURLContent);

            StringBuilder sb = new StringBuilder(2048);
			while (true) {
				String line = null;
				try {
					line = urlReader.readLine();
				} catch (IOException e) {
					commonExceptionProcess(" Error while reading URL content",
							e);
				}
				if (line == null)
					break;
				sb.append(line);
				sb.append('\n');
			}
			result = sb.toString();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			// Release resources
			if (urlReader != null)
				try {
					urlReader.close();
				} catch (IOException e) {
					log.severe(" Can't close BufferedReader" + e.getMessage());
				}
			if (inDecodedURLContent != null)
				try {
					inDecodedURLContent.close();
				} catch (IOException e) {
					log
							.severe(" Can't close InputStreamReader"
									+ e.getMessage());
				}
			if (inURLContent != null)
				try {
					inURLContent.close();
				} catch (IOException e) {
					log.severe(" Can't close InputStream" + e.getMessage());
				}
		}

		log.log(Level.WARNING, "given response: \"" + result + '\"');
		return result;
	}

	public String getURLContentAsString(String path, int connectionTimeout,
			int readTimeout) throws URLContentGetterException {

		InputStream inURLContent = null;
		InputStreamReader inDecodedURLContent = null;
		BufferedReader urlReader = null;
		String result = null;
		try {
			URL url = null;
			try {
				url = new URL(path);
			} catch (MalformedURLException e) {
				commonExceptionProcess("Bad URL \"" + path + '\"', e);
			}

			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					if (urlHostName != null
							&& urlHostName.equals(session.getPeerHost())) {
						return true;
					} else {
						log.log(Level.WARNING, "Warning: URL Host: " + urlHostName + " vs. "
								+ session.getPeerHost());
						return false;
					}
				}
			};

			String sTrustAll = System.getProperty(TRUST_ALL_CERTS);
			if (sTrustAll != null && "true".equals(sTrustAll)) {
				try {
					trustAllHttpsCertificates();
				} catch (Exception e) {
					log.severe(e.getMessage());
				}
			}
			try {
				if (sTrustAll != null && "true".equals(sTrustAll)) {
					HttpsURLConnection.setDefaultHostnameVerifier(hv);
				}
				URLConnection urlCon = url.openConnection();
				urlCon.setConnectTimeout(connectionTimeout);
				urlCon.setReadTimeout(readTimeout);
				urlCon.setDoInput(true);
				urlCon.setDoOutput(false);
				inURLContent = urlCon.getInputStream();
			} catch (IOException e) {
				commonExceptionProcess("Can't open URL \"" + path + '\"', e);
			}

			if (inURLContent == null)
				throwLocalException(" No input from URL");

			try {
				inDecodedURLContent = new InputStreamReader(inURLContent,
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				commonExceptionProcess(" Can't find decoding character scheme",
						e);
			}

			urlReader = new BufferedReader(inDecodedURLContent);

            StringBuilder sb = new StringBuilder(2048);
			while (true) {
				String line = null;
				try {
					line = urlReader.readLine();
				} catch (IOException e) {
					commonExceptionProcess(" Error while reading URL content",
							e);
				}
				if (line == null)
					break;
				sb.append(line);
				sb.append('\n');
			}
			result = sb.toString();
		} finally {
			// Release resources
			if (urlReader != null)
				try {
					urlReader.close();
				} catch (IOException e) {
					log.severe(" Can't close BufferedReader" + e.getMessage());
				}
			if (inDecodedURLContent != null)
				try {
					inDecodedURLContent.close();
				} catch (IOException e) {
					log
							.severe(" Can't close InputStreamReader"
									+ e.getMessage());
				}
			if (inURLContent != null)
				try {
					inURLContent.close();
				} catch (IOException e) {
					log.severe(" Can't close InputStream" + e.getMessage());
				}
		}
		return result;
	}

	public static class MyITM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {

			/*
			 * for (X509Certificate cert : certs) log.debug(cert.getVersion());
			 */

			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			/*
			 * for (X509Certificate cert : certs) log.debug(cert.getVersion());
			 */

			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {

		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {

		}
	}

	private static void trustAllHttpsCertificates() throws Exception {

		// Create a trust manager that does not validate certificate chains:

		javax.net.ssl.TrustManager[] trustAllCerts =

		new javax.net.ssl.TrustManager[1];

		javax.net.ssl.TrustManager tm = new MyITM();

		trustAllCerts[0] = tm;

		javax.net.ssl.SSLContext sc =

		javax.net.ssl.SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, null);

		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

		sc.getSocketFactory());

	}

}
