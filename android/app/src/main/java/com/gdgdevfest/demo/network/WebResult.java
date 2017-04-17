package com.gdgdevfest.demo.network;

import com.gdgdevfest.demo.Person;

import java.util.List;

/**
 * @author trux 1/3/15.
 */
public class WebResult {
    private int mCode;
    private String mBody;
    private List<Person> mPersonList;

    public int getHttpCode() {
        return mCode;
    }

    public void setHttpCode(int mCode) {
        this.mCode = mCode;
    }

    public String getHttpBody() {
        return mBody;
    }

    public void setHttpBody(String mResult) {
        this.mBody = mResult;
    }

    public List<Person> getmPersonList() {
        return mPersonList;
    }

    public void setmPersonList(List<Person> mPersonList) {
        this.mPersonList = mPersonList;
    }
}
