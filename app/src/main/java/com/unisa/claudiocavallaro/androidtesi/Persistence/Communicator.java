package com.unisa.claudiocavallaro.androidtesi.Persistence;

import com.unisa.claudiocavallaro.androidtesi.Activity.ActivityHome;
import com.unisa.claudiocavallaro.androidtesi.Model.ApiKeys;
import com.unisa.claudiocavallaro.androidtesi.Model.Preferenza;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Communicator {

    private static final String URL = "http://progettotesi.ddns.net:33425/";

    public void getPref(final ActivityHome activityHome, String id, String pref){
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();

        Interface api = retrofit.create(Interface.class);
        Call<Preferenza> call = api.getPhonePref(id, pref);

        call.enqueue(new Callback<Preferenza>() {
            @Override
            public void onResponse(Call<Preferenza> call, Response<Preferenza> response) {
                if (response.isSuccessful()){
                    System.out.println(response.code());
                }

                Preferenza lista = response.body();

                activityHome.reset();
            }

            @Override
            public void onFailure(Call<Preferenza> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void getKey(final ActivityHome activityHome){
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();

        Interface api = retrofit.create(Interface.class);

        Call<ApiKeys> call = api.getKeys();
        call.enqueue(new Callback<ApiKeys>() {
            @Override
            public void onResponse(Call<ApiKeys> call, Response<ApiKeys> response) {
                if (response.isSuccessful()){
                    System.out.println(response.code());
                }

                ApiKeys apiKeys = response.body();
                activityHome.setKey(apiKeys);
            }

            @Override
            public void onFailure(Call<ApiKeys> call, Throwable t) {
                activityHome.setErrorView();
                t.printStackTrace();
            }
        });
    }
}
