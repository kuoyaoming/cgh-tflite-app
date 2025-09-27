package com.cgh.org.audio.Http;

import android.os.Handler;
import android.os.Message;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTPS Post 請求 - 使用 OkHttp 替代 Apache HTTP
 */
public class HttpsPostThread extends Thread {

    private Handler handler;
    private String httpUrl;
    private File file;
    private int mWhat;

    public static final int ERROR = 404;
    public static final int SUCCESS = 200;

    public HttpsPostThread(Handler handler, String httpUrl, File file, int what) {
        this.handler = handler;
        this.httpUrl = httpUrl;
        this.file = file;
        this.mWhat = what;
    }

    @Override
    public void run() {
        try {
            // 創建 OkHttpClient
            OkHttpClient client = createOkHttpClient();
            
            // 創建 MultipartBody
            RequestBody requestBody = createMultipartBody();
            
            // 創建 Request
            Request request = new Request.Builder()
                    .url(httpUrl)
                    .post(requestBody)
                    .build();

            // 執行請求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String errorMsg = "Network error: " + e.getMessage();
                    handler.sendMessage(Message.obtain(handler, ERROR, errorMsg));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    int statusCode = response.code();
                    
                    if (statusCode == 200) {
                        handler.sendMessage(Message.obtain(handler, SUCCESS, result));
                    } else {
                        handler.sendMessage(Message.obtain(handler, ERROR, result));
                    }
                }
            });

        } catch (Exception e) {
            String errorMsg = "Request error: " + e.getMessage();
            handler.sendMessage(Message.obtain(handler, ERROR, errorMsg));
        }
    }

    private OkHttpClient createOkHttpClient() {
        // 創建日誌攔截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    private RequestBody createMultipartBody() {
        // 創建文件部分
        RequestBody fileBody = RequestBody.create(
                MediaType.parse("application/octet-stream"), 
                file
        );

        // 創建 MultipartBody
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
    }
}