package Security;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class DiffieHelman {

    //private static byte[] key;
    //private static SecretKeySpec secretCode;

    private PublicKey pKey;
    byte[] key;
    static Random random = new SecureRandom();
    private static  String SecretCode = "999ImSecret";
    private static String salt = "terceSmI999";
    KeyAgreement keyA;

    public DiffieHelman() { setKeyPairGenerator(); }

    public PublicKey getPKey() {return pKey;}
    protected Key keyGenerator() {return new SecretKeySpec(key, "AES");}

    private void setKeyPairGenerator() {
        KeyPairGenerator keyPG = null;
        try{
            keyPG = KeyPairGenerator.getInstance("EC");
            keyPG.initialize(128);
            KeyPair keyP = keyPG.generateKeyPair();
            pKey = keyP.getPublic();
            keyA = KeyAgreement.getInstance("ECDH");
            keyA.init(keyP.getPrivate());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

//    public void setKey(String KEY) {
//        MessageDigest sh;
//        try {
//            key = KEY.getBytes("UTF-8");
//            sh = MessageDigest.getInstance("SHA-1");
//            key = sh.digest(key);
//            key = Arrays.copyOf(key, 16);
//            secretCode = new SecretKeySpec(key, "AES");
//        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

    public void setPublicKey (PublicKey pKey) {
        try {
            keyA.doPhase(pKey,true);
            key = keyA.generateSecret();
        } catch ( InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encryption(byte[] ENC) {
        try {
            // setKey(code);
            byte[] ivp = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivs = new IvParameterSpec(ivp);

            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec ks = new PBEKeySpec(SecretCode.toCharArray(), salt.getBytes(), 65536, 128);
            SecretKey spec = skf.generateSecret(ks);
            SecretKeySpec secretKeySpec = new SecretKeySpec(spec.getEncoded(),"AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivs);
            return Base64.getEncoder().encode(cipher.doFinal(ENC));
        } catch (Exception e) {
            System.out.println("Some errors occurred during encryption" + e.toString());
        }
        return null;
    }

    public static byte[] decryption(byte[] DECR) {
        try {
            //setKey(code);

            byte[] ivp = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivs = new IvParameterSpec(ivp);

            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec ks = new PBEKeySpec(SecretCode.toCharArray(), salt.getBytes(), 65536, 128);
            SecretKey spec = skf.generateSecret(ks);
            SecretKeySpec secretKeySpec = new SecretKeySpec(spec.getEncoded(),"AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivs);
            byte[] decryptedS = cipher.doFinal(Base64.getMimeDecoder().decode(DECR));
            return decryptedS;
        } catch (Exception e) {
            System.out.println("Some errors occurred during decryption " + e.toString());
        }
        return null;
    }
}
