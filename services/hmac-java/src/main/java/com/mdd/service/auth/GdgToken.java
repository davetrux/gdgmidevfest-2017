package com.mdd.service.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author trux
 */
public class GdgToken extends UsernamePasswordAuthenticationToken {
    public GdgToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public GdgToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
