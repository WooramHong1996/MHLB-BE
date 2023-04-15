package com.gigajet.mhlb.global.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class AESUtil {
    public static String alg = "AES/CBC/PKCS5Padding";
    @Value("${aes.key}")
    private String key;

    public String encrypt(String text) {
        final String iv = key.substring(0, 16);
        String encryptedValue = "";
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            encryptedValue = Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("encryptAES128 error : {}", e.getMessage());
        }
        return encryptedValue;
    }
}
