package com.mdd.service.auth;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * @author trux
 */

public class HmacAuth {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static final Logger LOG = Logger.getLogger(HmacAuth.class.getName());
    /**
     * Computes RFC 2104-compliant HMAC signature.
     * * @param data
     * The data to be signed.
     * @param key
     * The signing key.
     * @return
     * The Base64-encoded RFC 2104-compliant HMAC signature.
     * @throws
     * java.security.SignatureException when signature generation fails
     */
    public static String calculateRFC2104HMAC(String data, String key)
            throws java.security.SignatureException
    {
        String result;
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
            result = DatatypeConverter.printBase64Binary(rawHmac);

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    public static String createMd5Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        LOG.info(input);

        byte[] bytesOfInput = input.getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] digest = md.digest(bytesOfInput);

        return DatatypeConverter.printBase64Binary(digest);
    }
}
