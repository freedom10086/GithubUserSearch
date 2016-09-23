package me.yluo.githubusersearch.api;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.yluo.githubusersearch.model.Repo;
import me.yluo.githubusersearch.model.User;

/**
 * Created by yang on 2016/9/23.
 * API client
 * 获得指定数据
 */
public class ApiClient {

    private static final String BASE_URL = "https://api.github.com";
    private static final String URL_SEARCH_USER = BASE_URL+"/search/users";
    //my github access token to avoid 403 forbidden
    //is tiken is wrong response code is 401
    private static final String TOKEN = "a595fa6c0ca565e6ea5342429d2c2cdbfbf513e0";

    private  final ExecutorService threadPool;
    private static ApiClient client;

    //单例 client
    public static ApiClient instance() {
        if(client==null){
            client = new ApiClient();
        }
        return client;
    }

    private ApiClient() {
        super();
        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * 获得user数据
     * GET /search/users
     * 简单的demo，如果是产品等可以使用gson来解析json
     */
    public void getUsers(final Map<String,String> params,final ApiResponse handler){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String res =  httpGet(URL_SEARCH_USER,params,handler);
                if(res!=null){
                    JSONObject object = null;
                    try {
                        List<User> temps = new ArrayList<>();
                        object = new JSONObject(res);
                        JSONArray array = object.getJSONArray("items");
                        for(int i=0;i<array.length();i++){
                            JSONObject b = array.getJSONObject(i);
                            int id = b.getInt("id");
                            String imgurl = b.getString("avatar_url");
                            String username = b.getString("login");
                            String repos_url = b.getString("repos_url");
                            temps.add(new User(id,username,imgurl,repos_url));
                        }
                        handler.sendMsg(ApiResponse.MSG_SUCCESS,temps);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendMsg(ApiResponse.MSG_FAILER,e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 获得repos数据
     * GET /users/:username/repos
     */
    public void getRepos(final String url, final ApiResponse handler){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String res =  httpGet(url,null,handler);
                if(res!=null){
                    try {
                        List<Repo> temps = new ArrayList<>();
                        JSONArray array = new JSONArray(res);
                        for(int i=0;i<array.length();i++){
                            JSONObject b = array.getJSONObject(i);
                            int id = b.getInt("id");
                            String name = b.getString("name");
                            String lang = b.getString("language");
                            temps.add(new Repo(id,name,lang));
                        }
                        handler.sendMsg(ApiResponse.MSG_SUCCESS,temps);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendMsg(ApiResponse.MSG_FAILER,e.getMessage());
                    }
                }
            }
        });
    }


    /**
     * 组装http请求参数
     */
    private static String encodeParameters(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder encodedParams = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (encodedParams.length() > 0) {
                encodedParams.append("&");
            }
            encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
            encodedParams.append('=');
            String v = entry.getValue() == null ? "" : entry.getValue();
            encodedParams.append(URLEncoder.encode(v, "utf-8"));
        }
        return encodedParams.toString();
    }


    /**
     * http get 方法获得数据
     */
    private static String httpGet(String url, Map<String,String> params,final ApiResponse handler) {
        HttpURLConnection conn = null;
        if(params==null){
            params = new HashMap<>();
        }
        params.put("access_token",TOKEN);
        try {
            url = url + "?"+encodeParameters(params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            // 利用string url构建URL对象
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(5000);
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                is.close();
                os.close();
                return os.toString();
            }else{
                handler.sendMsg(ApiResponse.MSG_FAILER,"http error response code is "+responseCode);
                return null;
            }
        } catch (Exception e) {
            handler.sendMsg(ApiResponse.MSG_FAILER,e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
