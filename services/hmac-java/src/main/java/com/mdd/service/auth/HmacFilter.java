package com.mdd.service.auth;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.mdd.service.Constants;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author trux
 */
public class HmacFilter extends AbstractAuthenticationProcessingFilter {

    private static final String HEADER_MISSING = "No HMAC Header found";
    private static final int SPLIT_LIMIT = 2;
    private static final Logger LOG = Logger.getLogger(HmacFilter.class.getName());

    protected HmacFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String authHeader = request.getHeader(Constants.AUTH_HEADER);

        if(Strings.isNullOrEmpty(authHeader) || !authHeader.startsWith(Constants.HEADER_PREFIX)){
            //Improper header, fail authentication
            throw new AuthenticationCredentialsNotFoundException(HEADER_MISSING);
        }

        String[] creds = authHeader.replaceFirst(Constants.HEADER_PREFIX, "").split(":", SPLIT_LIMIT);

        LOG.info(creds[0]);
        LOG.info(creds[1]);
        LOG.info(request.getMethod());
        LOG.info(request.getRequestURI());
        LOG.info(request.getRequestURL().toString());
        LOG.info("Done");

        HmacInput input = new HmacInput();

        input.setHash(creds[1]);
        input.setMethod(request.getMethod());
        input.setUrl(request.getRequestURL().toString());
        //input.setUrl("https://orion.digitalhpe.com/gdg/hmac/api/names/3");
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            input.setBody(CharStreams.toString(request.getReader()));
        } else {
            input.setBody("");
        }

        GdgToken token = new GdgToken(creds[0], input);

        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    /**
     * Because we require the API client to send credentials with every request, we must authenticate on every request
     */
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}