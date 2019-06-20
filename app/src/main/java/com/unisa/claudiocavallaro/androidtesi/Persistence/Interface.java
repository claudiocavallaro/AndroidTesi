package com.unisa.claudiocavallaro.androidtesi.Persistence;

import com.unisa.claudiocavallaro.androidtesi.Model.ApiKeys;
import com.unisa.claudiocavallaro.androidtesi.Model.Preferenza;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Interface {

    @GET("api/phonepref")
    Call<Preferenza> getPhonePref(@Query("id") String id, @Query("pref") String pref);


    @GET("api/cell/phone/cdsgrrte/xxsgh/keys")
    Call<ApiKeys> getKeys();
}
