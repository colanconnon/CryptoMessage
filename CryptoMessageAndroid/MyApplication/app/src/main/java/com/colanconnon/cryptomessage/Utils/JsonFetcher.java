package com.colanconnon.cryptomessage.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by colan on 12/14/14.
 */
public class JsonFetcher {
    public static JSONObject getJSONFromJSONPOST(JSONObject jsonObject, String URL) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream input = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String json = sb.toString();
            Log.e("tag", json);
            JSONObject jsonObject1 = new JSONObject(json);
            return jsonObject1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONFromUrl(List<NameValuePair> Params, String URL) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setEntity(new UrlEncodedFormEntity(Params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream input = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String json = sb.toString();
            Log.e("testing1234", json);
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject JSONPostToUrl(JSONObject jsonObject, String URL) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            httpPost.setEntity(stringEntity);
            //set this for django rest framework token based authentication
            //httpPost.setHeader("Authorization", "Token " + token);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            InputStream input = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String json = sb.toString();
            Log.e("testing1234", json);
            JSONObject jsonObject1 = new JSONObject(json);
            jsonObject1.put("statusCode", statusCode);
            return jsonObject1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONFromGetUrl(List<NameValuePair> Params, String URL) {
        try {
            if (!URL.endsWith("?")) {
                URL += "?";
            }

            String paramString = URLEncodedUtils.format(Params, "utf-8");

            URL += paramString;
            System.out.println(URL);

            URI uri = new URI(URL);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(uri);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream input = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String json = sb.toString();

            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONArray JSONGetArrayRequestWithAuthHeader(String URL, Context context){
        //NEED CONTEXT FOR RETRIEVING THE TOKEN
        try {
            Log.e("json", URL);
            URI uri = new URI(URL);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(uri);
            SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
            String token = prefs.getString("token", null);

            if(token == null){
                Log.e("jsonfetcher", "ERROR");
                return null;
            }
            httpGet.setHeader("Authorization","Token " + token);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream input = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String json = sb.toString();

            JSONArray jsonArray = new JSONArray(json);
            return jsonArray;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject JSONGetObjectRequestWithAuthHeader(String URL, Context context){
        //NEED CONTEXT FOR RETRIEVING THE TOKEN
        try {
            Log.e("json", URL);
            URI uri = new URI(URL);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(uri);
            SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
            String token = prefs.getString("token", null);

            if(token == null){
                Log.e("jsonfetcher", "ERROR");
                return null;
            }
            httpGet.setHeader("Authorization","Token " + token);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream input = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String json = sb.toString();

            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject JSONPOSTRequestWithAuthHeader(String URL, Context context,JSONObject jsonObject){
        //NEED CONTEXT FOR RETRIEVING THE TOKEN
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            httpPost.setEntity(stringEntity);
            SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
            String token = prefs.getString("token", null);

            if(token == null){
                Log.e("jsonfetcher", "ERROR");
                return null;
            }
            httpPost.setHeader("Authorization","Token " + token);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream input = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String json = sb.toString();
            Log.e("JSON", json);
            JSONObject jsonObject1 = new JSONObject(json);
            return jsonObject1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
