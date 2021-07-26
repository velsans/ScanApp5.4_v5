package com.zebra.main.api;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.zebra.BuildConfig;
import com.zebra.utilities.Common;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofitHttpExternal = null, retrofitHttpLocalORLive = null;
    private static ApiClient instance = null;
    // Keep your services here, build them in buildRetrofit method later
    private ApiInterface apiInterface;

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    // Build retrofit once when creating a single instance
    private ApiClient() {
        // Implement a method to build your retrofit
        //getExternalApiInterface();
        getApiInterface();
    }

    public ApiInterface getUserService() {
        return this.apiInterface;
    }

/*    public static ApiInterface getExternalApiInterface() {
        if (retrofitHttpExternal == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(Common.NETWORK_API_TIME_OUT, TimeUnit.SECONDS);
            httpClient.connectTimeout(Common.NETWORK_API_TIME_OUT, TimeUnit.SECONDS);
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                // set your desired log level
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                // add logging as last interceptor
                httpClient.addInterceptor(logging);
            }
            retrofitHttpExternal = new Retrofit
                    .Builder()
                    .baseUrl(ServiceURL.LIVE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofitHttpExternal.create(ApiInterface.class);
    }*/

    public static ApiInterface getApiInterface() {
        if (retrofitHttpLocalORLive == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                // set your desired log level
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                // add logging as last interceptor
                httpClient.addInterceptor(logging);
            }
            retrofitHttpLocalORLive = new Retrofit
                    .Builder()
                    .baseUrl(ServiceURL.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofitHttpLocalORLive.create(ApiInterface.class);
    }


}
