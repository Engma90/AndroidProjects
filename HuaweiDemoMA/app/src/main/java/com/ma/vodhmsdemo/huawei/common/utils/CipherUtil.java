package com.ma.vodhmsdemo.huawei.common.utils;


import android.util.Log;

import com.huawei.hms.support.log.common.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class CipherUtil {
    /**
     * Verify signature information.
     * @param content Result string.
     * @param sign Signature string.
     * @param publicKey Payment public key.
     * @return Whether the verification is successful.
     */
    public static boolean doCheck(String content, String sign, String publicKey) {
        if (sign == null) {
            return false;
        }
        if (publicKey == null) {
            return false;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = null;
            signature = java.security.Signature.getInstance("SHA256WithRSA");

            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));

            byte[] bsign = Base64.decode(sign);
            return signature.verify(bsign);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            //e.printStack();
            Log.e("CipherUtil", Objects.requireNonNull(e.getMessage()));
        }
        return false;
    }
}
