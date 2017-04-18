package com.gdgdevfest.demo;

/**
 * @author trux 1/3/15.
 */
public class Settings {

    private static final String REST_URL = "/api/names/11";

    public static final String BASE_URL = "https://torch.digitalhpe.com/";
    public static final String HMAC_URL = "https://torch.digitalhpe.com/hmac/api/names/3";

    public static final int OAUTH_REFRESH_TIMEOUT = 600;
    public static final int OAUTH_TOKEN_TIMEOUT = 30;

    public static final String OAUTH_BASE = "http://codemash-oauth.appspot.com";
    public static final String OAUTH_LOGIN = OAUTH_BASE + "/oauth/token?grant_type=password&client_id=codemash-client&username=%s&password=%s";
    public static final String OAUTH_REFRESH = OAUTH_BASE + "/oauth/token?client_id=codemash-client&grant_type=refresh_token&refresh_token=%s";

    public static final String OAUTH_URL = OAUTH_BASE + REST_URL;
}