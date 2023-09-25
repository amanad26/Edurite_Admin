package com.abmtech.eduriteadmin.apis;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static ApiInterface getClient(Context context) {

        try {
            boolean connected;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //we are connected to a network
            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
            if (!connected) {
                Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES);

//        if (BaseUrl.Development.equalsIgnoreCase(Constants.Key.Debug)) {
        client.addInterceptor((Interceptor) interceptor);
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrls.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        return retrofit.create(ApiInterface.class);
    }


    public static ApiInterface getPaymentClient(Context context) {

        try {
            boolean connected;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //we are connected to a network
            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
            if (!connected) {
                Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
            Log.d("Connect", "getClient() called with: connected = [" + connected + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES);

//        if (BaseUrl.Development.equalsIgnoreCase(Constants.Key.Debug)) {
        client.addInterceptor((Interceptor) interceptor);
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrls.PAYMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        return retrofit.create(ApiInterface.class);
    }

}
