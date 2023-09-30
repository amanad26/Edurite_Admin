package com.abmtech.eduriteadmin.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.abmtech.eduriteadmin.apis.ApiInterface;
import com.abmtech.eduriteadmin.firebase.NotiModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendNotification {

    public static void sendNotification(String fcmId, Map<String, String> notification, Map<String, String> data) {
        Map<String, Object> map = new HashMap<>();

        map.put("to", "/topics/all");
//        map.put("to", "eTEeL-sFRNSZ9E9gggmXQi:APA91bGCzbVX4FyrBu7KPkkJoQb0MDpx4IDA7bAzmyxhX80bAgDWN_D1n5mafQ76Ur9GcMZJA4CA13rb_oOYCtOKWwWuXoY9d4ge5qJpHck2Ir5fLOFlqvLNdW3MCWffap5EzTvK5_g3");
        map.put("notification", notification);
        map.put("data", data);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(360, TimeUnit.MINUTES)
                .readTimeout(360, TimeUnit.MINUTES)
                .writeTimeout(360, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();


        ApiInterface apiService = retrofit.create(ApiInterface.class);

        apiService.sendNotification(
                "key=AAAAp4oSRqs:APA91bGNVt5FtxxsohN5DwvcCAwRql9vt4ccQHbHYCjHs6Dyfjq1aWcAmR0F8lrzhQleiJX5kyOEF6LgNr5YX07EbfgzaYavEuArwy9UYwo5vXrgR4n77NKzOx2JADApkILgRHZM6-ZN\t",
                "application/json",
                map).enqueue(new Callback<NotiModel>() {
            @Override
            public void onResponse(@NonNull Call<NotiModel> call, @NonNull Response<NotiModel> response) {
                Log.e("TAG", "onResponse() called with: call = [" + call + "], response = [" + response + "]");
            }

            @Override
            public void onFailure(@NonNull Call<NotiModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }


    public static void sendNotificationToSingleUser(String fcmId, Map<String, String> notification, Map<String, String> data) {
        Map<String, Object> map = new HashMap<>();

        map.put("to", fcmId);
//        map.put("to", "eTEeL-sFRNSZ9E9gggmXQi:APA91bGCzbVX4FyrBu7KPkkJoQb0MDpx4IDA7bAzmyxhX80bAgDWN_D1n5mafQ76Ur9GcMZJA4CA13rb_oOYCtOKWwWuXoY9d4ge5qJpHck2Ir5fLOFlqvLNdW3MCWffap5EzTvK5_g3");
        map.put("notification", notification);
        map.put("data", data);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(360, TimeUnit.MINUTES)
                .readTimeout(360, TimeUnit.MINUTES)
                .writeTimeout(360, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();


        ApiInterface apiService = retrofit.create(ApiInterface.class);

        apiService.sendNotification(
                "key=AAAAp4oSRqs:APA91bGNVt5FtxxsohN5DwvcCAwRql9vt4ccQHbHYCjHs6Dyfjq1aWcAmR0F8lrzhQleiJX5kyOEF6LgNr5YX07EbfgzaYavEuArwy9UYwo5vXrgR4n77NKzOx2JADApkILgRHZM6-ZN\t",
                "application/json",
                map).enqueue(new Callback<NotiModel>() {
            @Override
            public void onResponse(@NonNull Call<NotiModel> call, @NonNull Response<NotiModel> response) {
                Log.e("TAG", "onResponse() called with: call = [" + call + "], response = [" + response + "]");
            }

            @Override
            public void onFailure(@NonNull Call<NotiModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }


}
