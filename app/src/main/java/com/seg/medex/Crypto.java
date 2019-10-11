package com.seg.medex;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class Crypto {

  //generates hash from a string
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

  //verifies that the passed hash is generated from the passed string
  public static boolean verifyHash(String s, String hash) {
     return hash.equals(getHash(s));
  }
}
