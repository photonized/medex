package com.seg.medex;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

/**
 * Contains cryptography based helper methods
 */
public class Crypto {

  /**
   * generates a SHA-256 representation of the String data that is passed to this method
   * @param text text to be encrypted
   * @return encrypted text
   */
  public static String getHash(String text) {
    MessageDigest md = null;
    //checks if SHA-256 works on the environment
    try {
      md = MessageDigest.getInstance("SHA-256");
    }
    catch(NoSuchAlgorithmException e) {
      System.out.println("Sorry but SHA-256 is not supported in this environment");
    }
    // Change this to UTF-16 if needed
    md.update(text.getBytes(Charset.forName("UTF-8")));
    byte[] digest = md.digest();

    String hex = String.format("%064x", new BigInteger(1, digest));
    return hex;
  }

  /**
   * checks if the hash is the hash representation of the string
   * @param s the string in its regular form
   * @param hash the string in its encrypted form
   * @return true if the hash is the hash representation of the string, otherwise false
   */
  public static boolean verifyHash(String s, String hash) {
     return hash.equals(getHash(s));
  }
}
