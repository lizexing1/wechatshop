package com.lizx.wechatshop.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClientUtils {

    private static HttpClient client = null;
    static {
        client = new DefaultHttpClient();
    }


    public static String httpGet(String url){
        String result = "";
        try {


            HttpGet get = new HttpGet(url);

            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){
               result = EntityUtils.toString(response.getEntity(),"UTF-8");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String httpPost(String url,String param){
        String result = "";
        try {
            HttpPost post = new HttpPost(url);

            StringEntity stringEntity = new StringEntity(param,"UTF-8");
            post.setEntity(stringEntity);

            HttpResponse response = client.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
