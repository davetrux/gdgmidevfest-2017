package com.mdd.service.auth;

/**
 * POJO for HMAC Incoming data
 *
 * @author truxall
 */
public class HmacInput {

    private String hash;
    private String url;
    private String method;
    private String body;


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}