package com.mdd.service.auth;


import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;


public class AuthFilter implements ContainerRequestFilter {
    private static final Logger log = Logger.getLogger(AuthFilter.class.getName());

    private static final String HMAC_PRIVATE = "4c4a3f0d-3dff-475a-afcc-6ec86fc0b126";

    //This should really be from a user store, but for a demo it's just a constant
    private static final String HMAC_USER = "CodeMash";

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {

        //Get the HTTP authorization header
        String authHeader = containerRequest.getHeaderValue("authorization");

        log.warning(String.format("Header: %s", authHeader));

        if(authHeader == null || !authHeader.startsWith("HMAC")){
            log.severe("No authentication info found");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        boolean authenticated = false;

        String[] creds = authHeader.replaceFirst("HMAC ", "").split(":", 2);

        //If login or password fail
        if(creds == null || creds.length != 2){
            log.severe("Invalid auth header - missing id or signature");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        if(!creds[0].equalsIgnoreCase(HMAC_USER)){
            log.severe("Invalid user");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        String url = containerRequest.getRequestUri().toString();

        try {
            String md5 = HmacAuth.createMd5Hash(url);

            String hmacString = containerRequest.getMethod() + md5 + url;

            String calcSignature = HmacAuth.calculateRFC2104HMAC(hmacString, HMAC_PRIVATE);

            authenticated = creds[1].equals(calcSignature);

        } catch (SignatureException e) {
            log.severe("Invalid signature calculation");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } catch (NoSuchAlgorithmException e) {
            log.severe("Md5 algorithm missing");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } catch (UnsupportedEncodingException e) {
            log.severe("UTF-8 encoding missing");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        if(authenticated) {
            return containerRequest;
        }
        else {
            log.severe("Authentication failed");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}
