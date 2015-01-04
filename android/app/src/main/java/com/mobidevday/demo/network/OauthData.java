package com.mobidevday.demo.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by david on 1/3/15.
 */
public class OauthData implements Parcelable {

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

    private long mLastResult;

    private String mCallResult;

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

    public long getLastResult() {
        return mLastResult;
    }

    public void setLastResult(long mLastResult) {
        this.mLastResult = mLastResult;
    }

    //Default constructor
    public OauthData(){

    }

    public String getCallResult() {
        return mCallResult;
    }

    public void setCallResult(String mCallResult) {
        this.mCallResult = mCallResult;
    }

    /*
 * Used to generate parcelable classes from a parcel
 */
    public static final Parcelable.Creator<OauthData> CREATOR
            = new Parcelable.Creator<OauthData>() {
        public OauthData createFromParcel(Parcel in) {
            return new OauthData(in);
        }

        public OauthData[] newArray(int size) {
            return new OauthData[size];
        }
    };

    /*
     * Needed to implement parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }


    public OauthData(Parcel item){
        mAccessToken = item.readString();
        mTokenType = item.readString();
        mRefreshToken = item.readString();
        mExpires = item.readInt();
        mScope = item.readString();
        mLastResult = item.readLong();
        mCallResult = item.readString();
    }

    /*
     * Place the data into the parcel
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAccessToken);
        parcel.writeString(mTokenType);
        parcel.writeString(mRefreshToken);
        parcel.writeInt(mExpires);
        parcel.writeString(mScope);
        parcel.writeLong(mLastResult);
        parcel.writeString(mCallResult);
    }
}
