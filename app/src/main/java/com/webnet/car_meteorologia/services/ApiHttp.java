package com.webnet.car_meteorologia.services;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.webnet.car_meteorologia.config.ConfigApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


public class ApiHttp {
    private String Tag = ApiHttp.class.getName();
    private String url;
    HttpURLConnection conn;
    Context context;

    public ApiHttp(Context context, String url) {
        this.context = context;
        this.setUrl(ConfigApp.getUrlServer() + "/api/" + url);
        System.setProperty("http.keepAlive", "false");
    }

    public ApiHttp(Context context) {
        this.context = context;
    }

    private void setAuthorization(HttpURLConnection httpConn) {
       /* byte[] auth = (getUser() + ":" + getPwd()).getBytes();
        String basic = Base64.encodeToString(auth, Base64.NO_WRAP);
        httpConn.setRequestProperty("Authorization", "Basic " + basic);
        */
       // httpConn.setRequestProperty("Id-User", getIMEI());
        httpConn.setUseCaches(false);
        // httpConn.setAllowUserInteraction(false);
        // conn.setConnectTimeout(5000);

    }

    public String Get(String jsonParam) throws JSONException {
        return RestFull(ApiHttp.RequestMethod.GET, jsonParam, 0);
    }

    public String Get(String jsonParam, int Timeout) throws JSONException {
        return RestFull(ApiHttp.RequestMethod.GET, jsonParam, Timeout);
    }

    public String Post(String jsonParam) throws JSONException {
        return (RestFull(ApiHttp.RequestMethod.POST, jsonParam, 0));
    }

    public String Post(String jsonParam, int Timeout) throws JSONException {
        return (RestFull(ApiHttp.RequestMethod.POST, jsonParam, Timeout));
    }

    public String GetGeocoder(double Latitude, double Longitude) throws JSONException {

        String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + Latitude + ","
                + Longitude + "&sensor=false&language=es";
        try {

            StringBuilder sb = new StringBuilder();
            URL mURL = new URL(googleMapUrl);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.connect();
            int HttpResult = conn.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                if (sb != null && sb.toString() != "null\n") {
                    JSONObject googleMapResponse = new JSONObject(URLDecoder.decode(sb.toString(), "UTF-8"));
                    // many nested loops.. not great -> use expression instead
                    // loop among all results
                    JSONArray results = (JSONArray) googleMapResponse.get("results");
                    String Address = null;
                    String cityName = null;
                    String CountryName = null;
                    String Departamento = null;
                    for (int i = 0; i < results.length(); i++) {
                        // loop among all addresses within this result
                        JSONObject result = results.getJSONObject(i);

                        if (result.has("formatted_address")) {
                            Address = result.getString("formatted_address");
                        }

                        if (result.has("address_components")) {
                            JSONArray addressComponents = result.getJSONArray("address_components");
                            // loop among all address component to find a 'locality' or 'sublocality'
                            for (int j = 0; j < addressComponents.length(); j++) {
                                JSONObject addressComponent = addressComponents.getJSONObject(j);

                                if (result.has("types")) {
                                    JSONArray types = addressComponent.getJSONArray("types");
                                    // search for locality and sublocality

                                    for (int k = 0; k < types.length(); k++) {
                                        if ("locality".equals(types.getString(k)) && cityName == null) {
                                            if (addressComponent.has("long_name")) {
                                                cityName = addressComponent.getString("long_name");
                                            } else if (addressComponent.has("short_name")) {
                                                cityName = addressComponent.getString("short_name");
                                            }
                                        }
                                       /* if ("sublocality".equals(types.getString(k))) {
                                            if (addressComponent.has("long_name")) {
                                                cityName = addressComponent.getString("long_name");
                                            } else if (addressComponent.has("short_name")) {
                                                cityName = addressComponent.getString("short_name");
                                            }
                                        }*/

                                        if ("administrative_area_level_1".equals(types.getString(k))) {
                                            if (addressComponent.has("long_name")) {
                                                Departamento = addressComponent.getString("long_name");
                                            } else if (addressComponent.has("short_name")) {
                                                Departamento = addressComponent.getString("short_name");
                                            }
                                        }

                                        if ("country".equals(types.getString(k))) {
                                            if (addressComponent.has("long_name")) {
                                                CountryName = addressComponent.getString("long_name");
                                            } else if (addressComponent.has("short_name")) {
                                                CountryName = addressComponent.getString("short_name");
                                            }
                                        }
                                    }
                                    if (cityName != null && CountryName != null) {

                                        JSONObject mRequestParams = new JSONObject();
                                        mRequestParams.put("City", cityName);
                                        mRequestParams.put("Address", Address);
                                        mRequestParams.put("Country", CountryName);
                                        mRequestParams.put("Departamento", Departamento);
                                        return mRequestParams.toString();
                                    }


                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String RestFull(String mRequestMethod, String jsonParam, int Timeout) throws JSONException {
        String result = null;
        String resultLog = "";
        try {
            URL mURL = null;
            StringBuilder sb = new StringBuilder();

            if (jsonParam == null && ApiHttp.RequestMethod.GET.equals(mRequestMethod)) {
                mURL = new URL(getUrl());
            } else if (jsonParam != null && ApiHttp.RequestMethod.GET.equals(mRequestMethod)) {
                mURL = new URL(getUrl() + "?" + jsonParam);
            } else if (jsonParam != null && ApiHttp.RequestMethod.POST.equals(mRequestMethod)) {
                mURL = new URL(getUrl());
            }

            Log.d(Tag, "" + mURL);
            conn = (HttpURLConnection) mURL.openConnection();
            setAuthorization(conn);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestMethod(mRequestMethod);
            //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                conn.setRequestProperty("connection", "close");
            }
            if (Timeout != 0) {
                conn.setConnectTimeout(Timeout);
                conn.setReadTimeout(Timeout);
            }

            if (ApiHttp.RequestMethod.POST.equals(mRequestMethod)) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
            }

            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            if (jsonParam != null && ApiHttp.RequestMethod.POST.equals(mRequestMethod)) {
                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("utf-8"));
                os.close();
                Log.d(Tag, jsonParam.toString());
            }

            int HttpResult = conn.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                if (sb != null && sb.toString() != "null\n"){
                    try{
                        result = (URLDecoder.decode(sb.toString(), "UTF-8"));
                    }catch (Exception ex){
                        result = sb.toString();
                    }
                }
                resultLog = result;

            } else if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                resultLog = "Sin contenido";
                result = null;
            } else if (HttpResult == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                resultLog = conn.getResponseMessage();
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    if (sb != null && sb.toString() != "null\n")
                        result = (URLDecoder.decode(sb.toString(), "UTF-8"));

                    resultLog = resultLog + " -- " + result;
                }catch (Exception ex){}
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_UNAUTHORIZED) {
                resultLog = conn.getResponseMessage();
                result = "401";
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_NOT_FOUND) {
                resultLog = conn.getResponseMessage();
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_UNAVAILABLE) {
                resultLog = conn.getResponseMessage();
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_CLIENT_TIMEOUT || HttpResult == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                resultLog = conn.getResponseMessage();
                throw new IOException(conn.getResponseMessage());
            } else {
                result = null;
            }

        } catch (IOException e) {
            Log.e(Tag, e.getMessage());
            resultLog = resultLog + ", EXCEPTION: " + e.getMessage();
        } finally {
            conn.disconnect();
        }

        System.out.println("result: " + result);



        return result;
    }

    public String RestFullGoogle(String mRequestMethod, String jsonParam, int Timeout) throws JSONException {
        String result = null;
        try {
            URL mURL = null;
            StringBuilder sb = new StringBuilder();

            if (jsonParam == null && ApiHttp.RequestMethod.GET.equals(mRequestMethod)) {
                mURL = new URL(getUrl());
            } else if (jsonParam != null && ApiHttp.RequestMethod.GET.equals(mRequestMethod)) {
                mURL = new URL(getUrl() + "?" + jsonParam);
            } else if (jsonParam != null && ApiHttp.RequestMethod.POST.equals(mRequestMethod)) {
                mURL = new URL(getUrl());
            }

            Log.d(Tag, "" + mURL);
            conn = (HttpURLConnection) mURL.openConnection();
            setAuthorization(conn);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestMethod(mRequestMethod);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                conn.setRequestProperty("connection", "close");
            }
            if (Timeout != 0) {
                conn.setConnectTimeout(Timeout);
                conn.setReadTimeout(Timeout);
            }

            if (ApiHttp.RequestMethod.POST.equals(mRequestMethod)) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
            }

            conn.connect();

            if (jsonParam != null && ApiHttp.RequestMethod.POST.equals(mRequestMethod)) {
                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("utf-8"));
                os.close();
                Log.d(Tag, jsonParam.toString());
            }

            int HttpResult = conn.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                if (sb != null && sb.toString() != "null\n")
                    result = (URLDecoder.decode(sb.toString(), "UTF-8"));

            } else if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                result = null;
            } else if (HttpResult == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_UNAVAILABLE) {
                throw new IOException(conn.getResponseMessage());
            } else if (HttpResult == HttpURLConnection.HTTP_CLIENT_TIMEOUT || HttpResult == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                throw new IOException(conn.getResponseMessage());
            } else {
                result = null;
            }

        } catch (IOException e) {
            Log.e(Tag, e.getMessage());
        } finally {
            conn.disconnect();
        }

        System.out.println("result: " + result);
        return result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private class RequestMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
    }

}
