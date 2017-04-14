package com.gdgmidevfest.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

/**
 * @author trux
 */
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    private static final String CLIENT_ID = "accounts.google.com";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = authentication.getCredentials().toString();

        try {
            Optional<GooglePrincipal> principal = validateToken(token);
            if(principal.isPresent()) {
                Authentication result = new AuthToken(principal, token);
                result.setAuthenticated(true);
                return result;
            } else {
                return null;
            }
        } catch (GeneralSecurityException e) {
            throw new SecurityException("Security expection", e);
        } catch (IOException e) {
            throw new SecurityException("IO expection", e);
        }
    }

    private Optional<GooglePrincipal> validateToken(String token) throws GeneralSecurityException, IOException {
        final JacksonFactory jacksonFactory = new JacksonFactory();
        NetHttpTransport transport = new NetHttpTransport();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(token);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            GooglePrincipal result = new GooglePrincipal();

            // Print user identifier
            result.setUserName(payload.getSubject());
            System.out.println("User ID: " + result.getUserName());

            result.setEmail(payload.getEmail());
            result.setPhotoUrl((String) payload.get("picture"));

            return Optional.of(result);

        } else {
            System.out.println("Invalid ID token.");
            return Optional.empty();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthToken.class.equals(authentication);
    }
}
