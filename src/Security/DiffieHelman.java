package Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class DiffieHelman {

    private static byte[] key;
    private static SecretKeySpec secretCode;

    public static void setKey(String KEY) {
        MessageDigest sh;
        try {
            key = KEY.getBytes("UTF-8");
            sh = MessageDigest.getInstance("SHA-1");
            key = sh.digest(key);
            key = Arrays.copyOf(key, 16);
            secretCode = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encryption(String ENC, String code) {
        try {
            setKey(code);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretCode);
            return Base64.getEncoder().encodeToString(cipher.doFinal(ENC.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        return null;
    }

    public static String decryption(String DEC, String code) {
        try {
            setKey(code);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretCode);
            return new String(cipher.doFinal(Base64.getDecoder().decode(DEC)));
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        return null;
    }
}
