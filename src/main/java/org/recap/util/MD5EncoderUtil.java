package org.recap.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sheiks on 28/06/17.
 */
public class MD5EncoderUtil {

    public String getMD5EncodingString(String text) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = getMd5();
            byte[] messageDigest = md.digest(text.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0".concat(hashtext);
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(e);
        }
    }

    public boolean matching(String orig, String compare){
        String md5 = null;
        try{
            MessageDigest md = getMd5();
            md.update(compare.getBytes());
            byte[] digest = md.digest();
            md5 = new BigInteger(1, digest).toString(16);

            return md5.equals(orig);

        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static MessageDigest getMd5() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5");
    }

}
