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

    //TODO: totally pointless and insecure, but gotta get this going
    private static final byte[] messageSalt = {
        (byte)0xfc, (byte)0x76, (byte)0x80, (byte)0xae,
        (byte)0xfd, (byte)0x82, (byte)0xbe, (byte)0xee,
    };

    public static Cipher storageEncryptionCipher;
    public static Cipher storageDecryptionCipher;
    public static Cipher messageEncryptionCipher;
    public static Cipher messageDecryptionCipher;

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

    public static byte[] charArrayToByteArray(char[] in) {
        byte[] ret = new byte[in.length];
        int i;
        for(i = 0; i < in.length; i++) {
            ret[i] = (byte)(in[i] & 0xFF);
        }
        return ret;
    }

    public static char[] byteArrayToCharArray(byte[] in) {
        char[] ret = new char[in.length];
        int i;
        for(i = 0; i < in.length; i++) {
            ret[i] = (char)(in[i] & 0xFF);
        }
        return ret;
    }

    public static void genStorageCiphers(char[] pass, byte[] salt) {
        try {
            storageEncryptionCipher = genCipher(pass, salt, KEY_FACTORY, ITERATION_COUNT, Cipher.ENCRYPT_MODE);
            storageDecryptionCipher = genCipher(pass, salt, KEY_FACTORY, ITERATION_COUNT, Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            Log.d(LOG_TAG, "genStorageCiphers", e);
        }
    }

    public static void genMessageCiphers(char[] pass) {
        try {
            messageEncryptionCipher = genCipher(pass, messageSalt, KEY_FACTORY, ITERATION_COUNT, Cipher.ENCRYPT_MODE);
            messageDecryptionCipher = genCipher(pass, messageSalt, KEY_FACTORY, ITERATION_COUNT, Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            Log.d(LOG_TAG, "genStorageCiphers", e);
        } 
    }

    private static Cipher genCipher(char[] pass, byte[] salt, String factory, int iterations, int mode)
        throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
                          InvalidKeyException, InvalidAlgorithmParameterException {
        PBEKeySpec keySpec = new PBEKeySpec(pass, salt, iterations, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(factory);
        SecretKey key = keyFactory.generateSecret(keySpec);
        AlgorithmParameterSpec spec = new PBEParameterSpec(salt, iterations);
        Cipher cipher = Cipher.getInstance(factory);
        cipher.init(mode, key, spec);
        return cipher;
    }

    public static String encryptMessageText(CharSequence plaintext) {
        return encryptText(fromCharSeqToChars(plaintext), messageEncryptionCipher);
    }

    public static String encryptText(CharSequence plaintext) {
        return encryptText(fromCharSeqToChars(plaintext), storageEncryptionCipher);
    }

    public static String encryptText(char[] plaintext) {
        return encryptText(plaintext, storageEncryptionCipher);
    }

    private static String encryptText(char[] plaintext, Cipher cipher) {
        byte[] plaintextBytes = charArrayToByteArray(plaintext);
        byte[] ciphertext = {};

        try {
            ciphertext = cipher.doFinal(plaintextBytes);
        } catch (Exception e) {
            Log.d(LOG_TAG, "encryptText", e);
        }

        //turn into hex so storage as a string is possible (db)
        return toHexString(ciphertext);
    }

    public static CharSequence decryptText(String ciphertextString) {
        return decryptText(ciphertextString, storageDecryptionCipher);
    }

    public static CharSequence decryptMessageText(String ciphertext) {
        return decryptText(ciphertext, messageDecryptionCipher);
    }

    private static CharSequence decryptText(String ciphertextString, Cipher cipher) {
        byte[] ciphertext = hexStringToBytes(ciphertextString);
        byte[] plaintext = {};

        //can't decrypt an empty string...exceptions gallore
        if(ciphertextString.length() == 0)
            return (CharSequence)ciphertextString;

        try {
            plaintext = cipher.doFinal(ciphertext);
        } catch(Exception e) {
            Log.d(LOG_TAG, "decryptText", e);
        }

        //TODO: dont' convert to string as interim...insecure
        return (CharSequence)(new String(plaintext));
    }
}
