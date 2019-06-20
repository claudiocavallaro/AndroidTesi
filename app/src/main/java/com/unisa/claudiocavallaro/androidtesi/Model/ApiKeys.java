package com.unisa.claudiocavallaro.androidtesi.Model;

import com.google.gson.annotations.SerializedName;

public class ApiKeys {

    @SerializedName("host")
    private String host;

    @SerializedName("accessKey")
    private String accessKey;

    @SerializedName("accessSecret")
    private String accessSecret;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public ApiKeys(){}
}
