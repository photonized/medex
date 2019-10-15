package com.seg.medex;
import android.util.Log;


public class Utility {

    public static boolean validPassword(String passwordUnhashed) {
        return passwordUnhashed.length() >= 8;
    }

    public static boolean passwordsMatch(String passwordUnhashed, String confirmPasswordUnhashed) {
        return passwordUnhashed.equals(confirmPasswordUnhashed);
    }

    public static boolean validUsername(String username) {
        Log.d("UTILITY", String.valueOf(isAlphanumeric(username)) + " : " + username);
        return username.length() > 0 && username.length() <= 20 && isAlphanumeric(username);
    }
    //TODO: test this, i have no idea if it works
    public static boolean validEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return  email.length() > 0 && email.matches(regex);
    }

    private static boolean isAlphanumeric(String s) {
        char[] alphaNumeric = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        for(int i = 0; i<s.length(); i++) {
            if(!(includes(alphaNumeric, s.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean includes(char[] arr, char c){
        for(char x : arr) {
            if(x==c) {return true;}
        }
        return false;
    }

}
