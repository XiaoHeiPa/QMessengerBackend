package org.qbychat.backend.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

public class TOTPUtils {
    private static final String HMAC_ALGO = "HmacSHA1";
    private static final int TIME_STEP_SECONDS = 30;
    private static final int TOTP_DIGITS = 6;

    public String generateTOTP(String secret) {
        long timeIndex = System.currentTimeMillis() / 1000 / TIME_STEP_SECONDS;
        return generateTOTP(secret, timeIndex);
    }

    private String generateTOTP(String secret, long timeIndex) {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        byte[] timeBytes = ByteBuffer.allocate(8).putLong(timeIndex).array();

        try {
            Key key = new SecretKeySpec(secretBytes, HMAC_ALGO);
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(key);
            byte[] hash = mac.doFinal(timeBytes);

            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7F) << 24) |
                    ((hash[offset + 1] & 0xFF) << 16) |
                    ((hash[offset + 2] & 0xFF) << 8) |
                    (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, TOTP_DIGITS);
            return String.format("%0" + TOTP_DIGITS + "d", otp);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error generating TOTP", e);
        }
    }

}
