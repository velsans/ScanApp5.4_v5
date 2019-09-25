package com.zebra.utilities;

import android.util.Log;

import com.google.gson.GsonBuilder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by DEV 27 on 11/05/2017.
 */

public class Communicator {


    public String POST_Obect(String url, final Object object) {
        String Errormessage = null;
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            String stringInput = new GsonBuilder().create().toJson(object);
            Log.e("stringInput", "stringInput" + stringInput);
            StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(object));
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip");

            HttpParams params = defaultHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, Common.NETWORK_TIME_OUT);
            HttpConnectionParams.setSoTimeout(params,  Common.NETWORK_TIME_OUT);

            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            Common.HttpResponceCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                String resultString = convertStreamToString(inputStream);
                //Log.e(">>>>>>>>>", "resultString>>>" + resultString);
                // resultString = resultString.replace("\n", "");
                inputStream.close();
                return resultString;//new GsonBuilder ().create ().fromJson (resultString);
            }
        } catch (UnsupportedEncodingException e) {//To change body of catch statement use File | Settings | File Templates.
            Errormessage = e.toString();
        } catch (ClientProtocolException e) {//To change body of catch statement use File | Settings | File Templates.
            Errormessage = e.toString();
        } catch (ConnectTimeoutException Cx) {
            Common.HttpResponceCode = 408;
            Errormessage = Cx.toString();//"Action TimeOut, Please Contact Admin";
        } catch (IOException e) {//To change body of catch statement use File | Settings | File Templates.
            Errormessage = e.toString();
        }
        return Errormessage;
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return stringBuilder.toString();
    }

  /*  public String POST_ObectHeader(String url, final Object object) {
        String Errormessage = null;
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(object));
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip");
            httpPost.setHeader("LoginToken", Common.LoginToken);
            httpPost.setHeader("UserId", String.valueOf(Common.UserId));
            httpPost.setHeader("DeviceTillId", String.valueOf(Common.DeviceTillID));

            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            Common.HttpResponceCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                String resultString = convertStreamToString(inputStream);
                //Log.e(">>>>>>>>>", "resultString>>>" + resultString);
                // resultString = resultString.replace("\n", "");
                inputStream.close();
                return resultString;//new GsonBuilder ().create ().fromJson (resultString);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            Errormessage = e.toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            Errormessage = e.toString();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            Errormessage = e.toString();
        }
        return Errormessage;
    }*/

    /*Direct URL*/
    public static String direct_executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
