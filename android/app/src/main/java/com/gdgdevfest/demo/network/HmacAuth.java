package com.gdgdevfest.demo.network;

import android.util.Base64;
import android.util.Log;

import com.gdgdevfest.demo.Settings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author trux
 * 1/6/15
 */
public class HmacAuth {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String HMAC_PRIVATE = "4c4a3f0d-3dff-475a-afcc-6ec86fc0b126";

    public String createHmacString(String userName) {
        try {

            String md5 = HmacAuth.createMd5Hash(Settings.HMAC_URL);

            String hmacString = "GET" + md5 + Settings.HMAC_URL;

            String signature = HmacAuth.calculateRFC2104HMAC(hmacString, HMAC_PRIVATE);

            return   userName + ":" + signature;

        } catch (NoSuchAlgorithmException ax) {
            Log.d(getClass().getName(), "Exception generating hash", ax);
            return "";
        } catch (SignatureException sx) {
            Log.d(this.getClass().getName(), "Exception generating signature", sx);
            return "";
        } catch(UnsupportedEncodingException ux) {
            Log.d(this.getClass().getName(), "Unsupported encoding");
            return "";
        }
    }

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
    private static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException
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
            result = Base64.encodeToString(rawHmac, Base64.NO_WRAP);

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    private static String createMd5Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        byte[] bytesOfInput = input.getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] digest = md.digest(bytesOfInput);

        return Base64.encodeToString(digest, Base64.NO_WRAP);
    }
}
