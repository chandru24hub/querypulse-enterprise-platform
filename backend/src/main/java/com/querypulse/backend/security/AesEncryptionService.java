package com.querypulse.backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class AesEncryptionService {

    private static final String SECRET_KEY =
        "QueryPulseAES123"; // 16 chars

    public String encrypt(
            String value
    ) {

        try {

            SecretKeySpec key =
                    new SecretKeySpec(
                            SECRET_KEY.getBytes(
        StandardCharsets.UTF_8
),
                            "AES"
                    );

            Cipher cipher =
                    Cipher.getInstance(
                            "AES"
                    );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key
            );

            byte[] encrypted =
                    cipher.doFinal(
                            value.getBytes()
                    );

            return Base64
                    .getEncoder()
                    .encodeToString(
                            encrypted
                    );

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Encryption failed",
                    ex
            );
        }
    }

    public String decrypt(
            String encryptedValue
    ) {

        try {

            SecretKeySpec key =
                    new SecretKeySpec(
                            SECRET_KEY.getBytes(
        StandardCharsets.UTF_8
),
                            "AES"
                    );

            Cipher cipher =
                    Cipher.getInstance(
                            "AES"
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key
            );

            byte[] decoded =
                    Base64
                            .getDecoder()
                            .decode(
                                    encryptedValue
                            );

            return new String(
                    cipher.doFinal(
                            decoded
                    )
            );

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Decryption failed",
                    ex
            );
        }
    }
}