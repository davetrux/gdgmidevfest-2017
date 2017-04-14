package com.gdgmidevfest.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * @author trux
 */
public class AuthToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;

    public AuthToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
