package util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final class CryptoUtil {
    private static final String JNDI_KEY = "java:comp/env/crypto/AESKey"; // << 이 이름으로 조회
    private static final int IV_LEN = 12;     // 96-bit IV
    private static final int TAG_LEN = 128;   // 128-bit tag
    private static final SecureRandom RNG = new SecureRandom();

    // lazy 로딩 (static 초기화에서 JNDI 붙잡지 않음)
    private static volatile SecretKey CACHED_KEY;

    private CryptoUtil() {}

    private static SecretKey key() {
        if (CACHED_KEY == null) {
            synchronized (CryptoUtil.class) {
                if (CACHED_KEY == null) {
                    try {
                        String b64 = (String) new InitialContext().lookup(JNDI_KEY);
                        byte[] raw = Base64.getDecoder().decode(b64);
                        if (raw.length != 32) {
                            throw new IllegalStateException("AES-256 key(32 bytes) required, but got " + raw.length);
                        }
                        CACHED_KEY = new SecretKeySpec(raw, "AES");
                        System.out.println("[CryptoUtil] AES key loaded: " + raw.length + " bytes");
                    } catch (NamingException e) {
                        // 원인 로그를 남기고 명확히 터뜨림
                        e.printStackTrace();
                        throw new RuntimeException("JNDI lookup failed: " + JNDI_KEY, e);
                    } catch (IllegalArgumentException ie) {
                        ie.printStackTrace();
                        throw new RuntimeException("Base64 decode failed for AES key", ie);
                    }
                }
            }
        }
        return CACHED_KEY;
    }

    /** AES-GCM encrypt. returns "GCM$ivB64$cipherB64" */
    public static String encrypt(String plaintext, String aad) {
        if (plaintext == null) return null;
        try {
            SecretKey k = key();
            byte[] iv = new byte[IV_LEN];
            RNG.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, k, new GCMParameterSpec(TAG_LEN, iv));
            if (aad != null) c.updateAAD(aad.getBytes(StandardCharsets.UTF_8));
            byte[] ct = c.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return "GCM$" + Base64.getEncoder().encodeToString(iv)
                       + "$" + Base64.getEncoder().encodeToString(ct);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /** AES-GCM decrypt from "GCM$ivB64$cipherB64" */
    public static String decrypt(String packed, String aad) {
        if (packed == null) return null;
        try {
            SecretKey k = key();
            String[] p = packed.split("\\$");
            if (p.length != 3 || !"GCM".equals(p[0])) throw new IllegalArgumentException("Bad ciphertext format");
            byte[] iv = Base64.getDecoder().decode(p[1]);
            byte[] ct = Base64.getDecoder().decode(p[2]);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, k, new GCMParameterSpec(TAG_LEN, iv));
            if (aad != null) c.updateAAD(aad.getBytes(StandardCharsets.UTF_8));
            byte[] pt = c.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
