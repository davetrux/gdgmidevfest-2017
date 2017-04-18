package com.mdd.service.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author trux
 */
public class GdgProvider implements AuthenticationProvider {

    public static final String USER_ROLE = "ROLE_USER";
    private static final Logger LOG = Logger.getLogger(GdgProvider.class.getName());

    //Really each user would have their own key, probably in a data store somewhere
    private static final String HMAC_PRIVATE = "4c4a3f0d-3dff-475a-afcc-6ec86fc0b126";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        HmacInput input = (HmacInput) authentication.getCredentials();
        //String user = authentication.getPrincipal().toString();
        GdgToken token;

        try {
            String md5 = HmacAuth.createMd5Hash(input.getUrl() + input.getBody());

            LOG.info(md5);

            String hmacString = input.getMethod() + md5 + input.getUrl();

            LOG.info(hmacString);

            String calcSignature = HmacAuth.calculateRFC2104HMAC(hmacString, HMAC_PRIVATE);

            LOG.info(calcSignature);

            boolean authenticated = input.getHash().equals(calcSignature);

            if(authenticated) {
                List<GrantedAuthority> grantedAuths = new ArrayList<>();
                grantedAuths.add(new SimpleGrantedAuthority(USER_ROLE));
                token = new GdgToken(authentication.getPrincipal().toString(), input, grantedAuths);
                token.setAuthenticated(true);
            } else {
                token = null;
//                token = new GdgToken(authentication.getPrincipal().toString(), input);
//                token.setAuthenticated(false);
            }
            return token;

        } catch (SignatureException e) {
            String msg = "Invalid signature calculation";
            LOG.severe(msg);
        } catch (NoSuchAlgorithmException e) {
            LOG.severe("Md5 algorithm missing");
        } catch (UnsupportedEncodingException e) {
            LOG.severe("UTF-8 encoding missing");
        } catch (Exception ex) {

            LOG.severe(ex.getMessage());
        }
        token = new GdgToken(authentication.getPrincipal().toString(), input);
        token.setAuthenticated(false);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
