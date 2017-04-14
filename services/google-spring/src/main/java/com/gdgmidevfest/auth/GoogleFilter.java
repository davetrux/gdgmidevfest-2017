package com.gdgmidevfest.auth;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author trux
 */
public class GoogleFilter extends AbstractAuthenticationProcessingFilter {

    private static String AUTH_HEADER = "Authorization";
    private static String AUTH_PREFIX = "GDG";

    public GoogleFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        String rawValue = getHeaderValue(request, AUTH_HEADER);

        if(rawValue.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Missing header");
        }

        if(!rawValue.startsWith(AUTH_PREFIX)) {
            throw new AuthenticationCredentialsNotFoundException("Improper header");
        }

        String token = rawValue.substring(AUTH_PREFIX.length() + 1);

        Authentication auth = new AuthToken(null, token);

        return this.getAuthenticationManager().authenticate(auth);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    private String getHeaderValue(HttpServletRequest request, String headerParameterName) {
        return (request.getHeader(headerParameterName) != null) ? request.getHeader(headerParameterName) : "";
    }


    @Override
    /**
     * Because we require the API client to send credentials with every request, we must authenticate on every request
     */
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }


}
