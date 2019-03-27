package com.zck.ydre;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyOKhttp {

    private static OkHttpClient client;
     static final String USERAGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";

    public static String httpRequest(String url) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .header("User-Agent", USERAGENT)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
