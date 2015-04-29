package com.betfair.aping.api;


import com.betfair.aping.ApiNGDemo;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.JsonResponseHandler;
import com.betfair.aping.util.RescriptResponseHandler;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HttpUtil {

    private final String HTTP_HEADER_X_APPLICATION = "X-Application";
    private final String HTTP_HEADER_X_AUTHENTICATION = "X-Authentication";
    private final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private final String HTTP_HEADER_ACCEPT = "Accept";
    private final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";

    public HttpUtil() {
        super();
    }

    private String sendPostRequest(String param, String operation, String appKey, String ssoToken, String URL, ResponseHandler<String> reqHandler){
        String jsonRequest = param;

        HttpPost post = new HttpPost(URL);
        String resp = null;
        try {
            post.setHeader(HTTP_HEADER_CONTENT_TYPE, ApiNGDemo.getProp().getProperty("APPLICATION_JSON"));
            post.setHeader(HTTP_HEADER_ACCEPT, ApiNGDemo.getProp().getProperty("APPLICATION_JSON"));
            post.setHeader(HTTP_HEADER_ACCEPT_CHARSET, ApiNGDemo.getProp().getProperty("ENCODING_UTF8"));
            post.setHeader(HTTP_HEADER_X_APPLICATION, appKey);
            post.setHeader(HTTP_HEADER_X_AUTHENTICATION, ssoToken);

            if (jsonRequest != null)
            post.setEntity(new StringEntity(jsonRequest, ApiNGDemo.getProp().getProperty("ENCODING_UTF8")));

            HttpClient httpClient = new DefaultHttpClient();

            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, new Integer(ApiNGDemo.getProp().getProperty("TIMEOUT")).intValue());
            HttpConnectionParams.setSoTimeout(httpParams, new Integer(ApiNGDemo.getProp().getProperty("TIMEOUT")).intValue());

            resp = httpClient.execute(post, reqHandler);

        } catch (UnsupportedEncodingException e1) {
            //Do something
        	e1.printStackTrace();

        } catch (ClientProtocolException e) {
            //Do something
        	e.printStackTrace();

        } catch (IOException ioE){
            //Do something
        	ioE.printStackTrace();
        }

        return resp;

    }

    public String sendPostRequestRescript(String param, String operation, String appKey, String ssoToken) throws APINGException{
        String apiNgURL = ApiNGDemo.getProp().getProperty("APING_URL") + ApiNGDemo.getProp().getProperty("RESCRIPT_SUFFIX")+operation+"/";

        return  sendPostRequest(param, operation, appKey, ssoToken, apiNgURL, new RescriptResponseHandler());

    }

    public String sendPostRequestJsonRpc(String param, String operation, String appKey, String ssoToken) {
        String apiNgURL = ApiNGDemo.getProp().getProperty("APING_URL") + ApiNGDemo.getProp().getProperty("JSON_RPC_SUFFIX");

        return sendPostRequest(param, operation, appKey, ssoToken, apiNgURL, new JsonResponseHandler());

    }

    public String sendKeepAlivePostRequest( String appKey, String ssoToken) {
        String apiNgURL = "https://identitysso.betfair.com/api/keepAlive"; // ApiNGDemo.getProp().getProperty("INTER_KA_URL");

        return sendPostRequest(null, null, appKey, ssoToken, apiNgURL, new JsonResponseHandler());
    }

    public String sendLogoutRequest( String appKey, String ssoToken) {
        String apiNgURL = "https://identitysso.betfair.com/api/logout"; // ApiNGDemo.getProp().getProperty("INTER_KA_URL");

        return sendPostRequest(null, null, appKey, ssoToken, apiNgURL, new JsonResponseHandler());
    }

}
