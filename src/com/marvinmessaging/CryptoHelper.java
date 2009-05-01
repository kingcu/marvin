package com.marvinmessaging;

import android.util.Log;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.NoSuchPaddingException;

import java.security.SecureRandom;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class CryptoHelper {
    private static final String LOG_TAG = "MarvinMessaging";
    private static final String KEY_FACTORY = "PBEWithSHA256And128BitAES-CBC-BC";

    //TODO: come up with less arbitrary number
    private static final int ITERATION_COUNT = 50;

    public static byte[] generateSalt() {
        byte[] salt = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        return salt;
    }

    public static byte[] generateHash(char[] pass, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec keySpec = new PBEKeySpec(pass, salt, ITERATION_COUNT, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_FACTORY);
        SecretKey key = keyFactory.generateSecret(keySpec);

        return key.getEncoded();
    }

    public static String toHexString(byte bytes[]) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            buf.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1));
        }
        return buf.toString();
    }

    public static byte[] hexStringToBytes(String hex) {
        byte [] bytes = new byte [hex.length() / 2];
        int j = 0;

        for (int i = 0; i < hex.length(); i += 2) {
            try {
                String hexByte = hex.substring(i, i+2);
                Integer I = new Integer(0);
                I = Integer.decode("0x"+hexByte);
                int k = I.intValue ();
                bytes[j++] = new Integer(k).byteValue();
            } catch (NumberFormatException e) {
                Log.d(LOG_TAG, "hexStringToBytes", e);
                return bytes;
            } catch (StringIndexOutOfBoundsException e) {
                Log.d(LOG_TAG, "hexStringToBytes", e);
                return bytes;
            }
        }
        return bytes;
    }

    public static boolean checkPassword(char[] plaintextPassword, String passwordHash, String passwordSalt) {
       byte[] calculatedHash = null;
       byte[] savedSalt = hexStringToBytes(passwordSalt);
       byte[] savedHash = hexStringToBytes(passwordHash);

       if(savedHash.length == 0)
           return true; //if they are both empty this will fall through (first time, no set pass)

       try {
           calculatedHash = generateHash(plaintextPassword, savedSalt);
       } catch (Exception e) {
           Log.d(LOG_TAG, "checkPassword", e);
           return false;
       }

       if(calculatedHash != null) {
           if(Arrays.equals(calculatedHash, savedHash))
               return true;
           else
               return false;
       }
       return false;
    }

    /** 
     * we can't store a password as a string, since in Java
     * strings are immutable, and thus we can't null out and
     * guarantee the password doesn't hang around after GC;
     * CharSequence doesn't have this problem, and that's what
     * an EditText returns; So, we go from CharSequence to
     * an array of bytes; We want a byte array anyway, for crypto.
     *
     */
    public static char[] fromCharSeqToChars(CharSequence seq) {
        char[] ret = new char[seq.length()];
        int i;

        for(i = 0; i < seq.length(); i++) {
            ret[i] = seq.charAt(i);
        }   
        return ret;
    }   

    /*
       private Cipher createCipher(String pass, byte[] salt, String factory, int mode)
       throws NoSuchAlgorithmException,
                  InvalidKeySpecException,
                  NoSuchPaddingException,
                  InvalidKeyException,
                  InvalidAlgorithmParameterException {

//make the keyspec with passed in values, and a 128bit key
SecretKey key = createKey(pass, salt);
AlgorithmParameterSpec algoSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
Cipher cipher = Cipher.getInstance(factory);
        cipher.init(mode, key, algoSpec);
        return cipher;
    }
    */
}
