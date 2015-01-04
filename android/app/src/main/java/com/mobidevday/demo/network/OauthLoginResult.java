package com.mobidevday.demo.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by david on 1/3/15.
 */
public class OauthLoginResult {

    @SerializedName("access_token")
    private String mAccessToken;

    @SerializedName("token_type")
    private String mTokenType;

    @SerializedName("refresh_token")
    private String mRefreshToken;

    @SerializedName("expires_in")
    private int mExpires;

    @SerializedName("scope")
    private String mScope;

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public void setTokenType(String mTokenType) {
        this.mTokenType = mTokenType;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String mRefreshToken) {
        this.mRefreshToken = mRefreshToken;
    }

    public int getExpires() {
        return mExpires;
    }

    public void setExpires(int mExpires) {
        this.mExpires = mExpires;
    }

    public String getScope() {
        return mScope;
    }

    public void setScope(String mScope) {
        this.mScope = mScope;
    }
}
