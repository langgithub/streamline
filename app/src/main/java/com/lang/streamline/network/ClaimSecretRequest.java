package com.lang.streamline.network;

import com.google.gson.annotations.SerializedName;

public class ClaimSecretRequest {

    @SerializedName("token")
    private String token;

    @SerializedName("md5")
    private String md5;

    public ClaimSecretRequest(String token, String md5) {
        this.token = token;
        this.md5 = md5;
    }
}
