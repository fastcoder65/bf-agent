package com.betfair.aping.api;

import com.betfair.aping.ApiNGDemo;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.JsonResponseHandler;
import com.betfair.aping.util.RescriptResponseHandler;
import my.pack.util.AccountConstants;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//@Lock(LockType.WRITE)
//@AccessTimeout(value=60, unit = TimeUnit.SECONDS )
@Startup
@Singleton
public class HttpUtil implements TimedObject {

    private final String HTTP_HEADER_X_APPLICATION = "X-Application";
    private final String HTTP_HEADER_X_AUTHENTICATION = "X-Authentication";
    private final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private final String HTTP_HEADER_ACCEPT = "Accept";
    private final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    @Resource
    private EJBContext context;

    @PostConstruct
    private void create() {
        log.info("** HttpUtil @PostConstruct , hashcode=" + this.hashCode());
        httpClient = createHttpClientOrProxy();
        createTimer(context,  3600 * 1000);
    }

    private void createTimer(EJBContext context, long duration) {
        TimerService timerService;
        try {
            timerService = context.getTimerService();

            String timerInfo = "closeIdleConTimer on "+ duration +" , hashcode=" + this.hashCode();

            timerService.createIntervalTimer(1000, duration, new TimerConfig(timerInfo, false));

        } catch (Exception e) {
            String msg = e.getMessage();
            log.severe("error creating timer: " + ((msg != null) ? msg : ""));
        }
    }

    public void ejbTimeout(javax.ejb.Timer timer) {

        //timer.cancel();
        String timerInfo = (String) timer.getInfo();

        log.info(timerInfo + ", close Idle http Connections..");

        clientConnectionMaanager.closeIdleConnections(600, TimeUnit.MILLISECONDS);
        clientConnectionMaanager.closeExpiredConnections();

    }

    private CloseableHttpClient httpClient = null; // createHttpClientOrProxy();
    private PoolingHttpClientConnectionManager clientConnectionMaanager = null;

    private boolean isSet(Object sysProperty) {
        return sysProperty != null
                && sysProperty.toString().trim().length() > 0;
    }

    private CloseableHttpClient createHttpClientOrProxy() {

        CloseableHttpClient httpClient = null;

        HttpClientBuilder hcBuilder = HttpClients.custom();

        // Set HTTP proxy, if specified in system properties

        if (isSet(System.getProperty("http.proxyHost"))) {
            int port = 80;
            if (isSet(System.getProperty("http.proxyPort"))) {
                port = Integer.parseInt(System.getProperty("http.proxyPort"));
            }

            org.apache.http.HttpHost proxy = new org.apache.http.HttpHost(System.getProperty("http.proxyHost"), port);

            org.apache.http.client.CredentialsProvider credsProvider = new BasicCredentialsProvider();

            credsProvider.setCredentials(new AuthScope(proxy.getHostName(),
                    proxy.getPort()), new UsernamePasswordCredentials("fastcoder65", "imxDr5OZimxDr5OZ"));

            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

            hcBuilder.setRoutePlanner(routePlanner);
            hcBuilder.setDefaultCredentialsProvider(credsProvider);
        }

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setSocketTimeout(
                        new Integer(ApiNGDemo.getProp().getProperty("TIMEOUT"))
                                .intValue())
                .setConnectTimeout(
                        new Integer(ApiNGDemo.getProp().getProperty("TIMEOUT"))
                                .intValue()).build();

        hcBuilder.setDefaultRequestConfig(requestConfig);

        try {
            SSLContext ctx = SSLContext.getInstance("TLS");

            KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(new File(AccountConstants.PATH_TO_PRIVATE_KEY)), "1q2w3e4r5t");

            ctx.init(keyManagers, null, new SecureRandom());

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);

            hcBuilder.setSSLSocketFactory(sslsf);

            if (clientConnectionMaanager == null) {
                clientConnectionMaanager = new PoolingHttpClientConnectionManager();
                clientConnectionMaanager.setMaxTotal(100);
                clientConnectionMaanager.setDefaultMaxPerRoute(20);
            }
            //PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            if (clientConnectionMaanager != null)
            hcBuilder.setConnectionManager(clientConnectionMaanager);

            httpClient = hcBuilder.build();

        } catch (Exception e) {
            log.log(Level.SEVERE, "error initializing httpClient, ", e);
        }

        return httpClient;
    }

    private KeyManager[] getKeyManagers(String keyStoreType,
                                               InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }

    public String sendPostRequest(String param, String operation,
                                   String appKey, String ssoToken, String aURL,
                                   ResponseHandler<String> reqHandler) {
        String jsonRequest = param;

        HttpPost post = new HttpPost(aURL);

        String resp = null;
       // CloseableHttpResponse response = null;

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
            post.setHeader(HTTP.CONN_KEEP_ALIVE, "timeout=60000");

            if (jsonRequest != null)
                post.setEntity(new StringEntity(jsonRequest, ApiNGDemo
                        .getProp().getProperty("ENCODING_UTF8")));


            HttpClientContext context = HttpClientContext.create();

            long startTime = System.currentTimeMillis();

            resp = httpClient.execute(post, reqHandler, context);

            long endTime = System.currentTimeMillis();

            for (org.apache.http.Header hdr : post.getAllHeaders()) {
                log.fine("found header: " + hdr.getName() + " = " + hdr.getValue());
            }


            log.fine("web request " + aURL + " executed in " + (endTime - startTime) / 1000.0 + " second(s).");

        } catch (org.apache.http.conn.HttpHostConnectException e1) {
            log.severe("!!! method 'sendPostRequest': request on: "+ aURL + ", operation: " + operation + ", error: " + e1.getMessage());
        } catch (UnsupportedEncodingException e1) {
            log.severe("method 'sendPostRequest' error: " + e1.getMessage());

        } catch (ClientProtocolException e) {
            log.severe("method 'sendPostRequest' error: " + e.getMessage());

        } catch (IOException ioE) {
            log.severe("method 'sendPostRequest' error: " + ioE.getMessage());
        }
        return resp;
    }

    @PreDestroy
    public void destroy () {
        log.info("HttpUtil destroy, httpClient: " + httpClient);
      if (httpClient != null) {
          try {

              httpClient.close();
              log.info("HttpUtil destroy, httpClient closed.");
          } catch (IOException ioe) {
            log.severe(ioe.getMessage());
          }


      }
    }

    public String sendPostRequestRescript(String param, String operation,
                                          String appKey, String ssoToken) throws APINGException {
        String apiNgURL = ApiNGDemo.getProp().getProperty("APING_URL")
                + ApiNGDemo.getProp().getProperty("RESCRIPT_SUFFIX")
                + operation + "/";

        return sendPostRequest(param, operation, appKey, ssoToken, apiNgURL,
                new RescriptResponseHandler());

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
