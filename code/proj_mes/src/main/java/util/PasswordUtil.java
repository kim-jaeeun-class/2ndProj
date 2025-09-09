package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class PasswordUtil {
    // 권장 파라미터
    private static final String PREFERRED_ALG = "PBKDF2WithHmacSHA256"; // JRE에 따라 미지원이면 SHA1로 폴백
    private static final String FALLBACK_ALG  = "PBKDF2WithHmacSHA1";
    private static final int    ITERATIONS    = 120_000;
    private static final int    KEY_LENGTH    = 256; // bits (32 bytes)
    private static final int    SALT_LEN      = 16;  // bytes

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64E = Base64.getEncoder();
    private static final Base64.Decoder B64D = Base64.getDecoder();

    private PasswordUtil() {}

    /** 비밀번호 해시 생성 (형식: PBKDF2$ALG$ITER$SALT_B64$HASH_B64) */
    public static String hash(String password) {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password required");

        byte[] salt = new byte[SALT_LEN];
        RNG.nextBytes(salt);

        String alg = availableAlg();
        byte[] dk  = pbkdf2(alg, password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        return "PBKDF2$" + alg + "$" + ITERATIONS + "$" + B64E.encodeToString(salt) + "$" + B64E.encodeToString(dk);
    }

    /** 평문과 저장 해시 일치 여부 확인 */
    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) return false;
        try {
            String[] p = stored.split("\\$");
            if (p.length != 5 || !"PBKDF2".equals(p[0])) return false;
            String alg = p[1];
            int iter   = Integer.parseInt(p[2]);
            byte[] salt = B64D.decode(p[3]);
            byte[] expected = B64D.decode(p[4]);

            byte[] actual = pbkdf2(alg, password.toCharArray(), salt, iter, expected.length * 8);
            return constantTimeEquals(actual, expected);
        } catch (Exception e) {
            return false;
        }
    }

    /** 재해시 필요 여부(정책 변경 시 사용) */
    public static boolean needsRehash(String stored) {
        try {
            String[] p = stored.split("\\$");
            String alg = p[1];
            int iter   = Integer.parseInt(p[2]);
            return !PREFERRED_ALG.equals(alg) || iter < ITERATIONS;
        } catch (Exception e) {
            return true;
        }
    }

    private static String availableAlg() {
        try {
            SecretKeyFactory.getInstance(PREFERRED_ALG);
            return PREFERRED_ALG;
        } catch (NoSuchAlgorithmException e) {
            // 일부 구버전 JRE 호환
            return FALLBACK_ALG;
        }
    }

    private static byte[] pbkdf2(String alg, char[] pwd, byte[] salt, int iter, int bits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pwd, salt, iter, bits);
            return SecretKeyFactory.getInstance(alg).generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 error", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
