package com.seg.medex;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * contains helper methods
 */
public class Utility {

    /**
     * This checks if the password provided abides by our criteria
     * @param passwordUnhashed the string representation of a password
     * @return true if the password abides by our criteria
     */
    public static boolean validPassword(String passwordUnhashed) {
        return passwordUnhashed.length() >= 8;
    }

    /**
     * This checks if the name abides by our criteria
     * @param name the name
     * @return true if the name abides by our criteria
     */
    public static boolean validName(String name) {
        return name.length() > 0 && isAlpha(name);
    }

    /**
     * Checks if the user was able to properly input the password twice
     * @param passwordUnhashed the string representation of the password
     * @param confirmPasswordUnhashed the string representation of the password
     * @return true if both parameters are equal
     */
    public static boolean passwordsMatch(String passwordUnhashed, String confirmPasswordUnhashed) {
        return passwordUnhashed.equals(confirmPasswordUnhashed);
    }

    /**
     * Checks if the username abides by our criteria
     * @param username the username
     * @return true if the username abides by our criteria
     */
    public static boolean validUsername(String username) {
     //   Log.d("UTILITY", isAlphanumeric(username) + " : " + username);
        return username.length() > 0 && username.length() <= 20 && isAlphanumeric(username);
    }

    /**
     * Checks if the email abides by format xxx@xxx.xxx
     * @param email the inputted email
     * @return true if the email abides by the format xxx@xxx.xxx
     */
    public static boolean validEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return  email.length() > 0 && email.matches(regex);
    }


    /**
     * checks if a string is alphanumeric
     * @param s a string
     * @return true if a string is alphanumeric
     */
    public static boolean isAlphanumeric(String s) {
        char[] alphaNumeric = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        for(int i = 0; i<s.length(); i++) {
            if(!(includes(alphaNumeric, s.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a string is alpha
     * @param s a string
     * @return true if the string is alpha
     */
    public static boolean isAlpha(String s) {
        char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        s = s.toLowerCase();
        for(int i = 0; i<s.length(); i++) {
            if(!(includes(alpha, s.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String s){
        char[] alpha = {'1','2','3','4','5','6','7','9','0'};
        for(int i = 0; i<s.length(); i++) {
            if(!(includes(alpha, s.charAt(i)))) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if c is in arr
     * @param arr a char array
     * @param c a char
     * @return true if c is in arr
     */
    public static boolean includes(char[] arr, char c){
        for(char x : arr) {
            if(x==c) {return true;}
        }
        return false;
    }

    /**
     * Checks if number provided is indeed numerical (up to 1000)
     * @param x is a int
     * @return true if x is numerical (up to 1000)
     */
    public static boolean isNumerical(int x){
        for(int i = 0; i <= 1000; i++){
            if(i == x){
                return true;
            }
        }
        return false;
    }


    /**
     * checks if a string is alphanumeric
     * @param s a string
     * @return true if a string is alphanumeric
     */
    public static boolean isAlphanumericPostalCode(String s) {
       if (s.length() <= 6){
           char[] alphaNumeric = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
           for(int i = 0; i<s.length(); i++) {
               if(!(includes(alphaNumeric, s.charAt(i)))) {
                   return false;
               }
           }
           return true;
       }
       else{
           return false;
       }
    }

    public static String convertDayToString(int year, int month, int day) {
        return year + "/" + month + "/" + day;
    }

    public static int convertDaytoWeekday(String stringDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.ENGLISH);
        Date date = sdf.parse(stringDate);
        Log.d("UTILITY: ", date.toString());
        sdf.applyPattern("EEE");
        String dateString = sdf.format(date);


        if(dateString.equals("Sun")) {
            return 6;
        } else if (dateString.equals("Mon")) {
            return 0;
        }else if (dateString.equals("Tue")) {
            return 1;

        }else if (dateString.equals("Wed")) {
            return 2;

        }else if (dateString.equals("Thu")) {
            return 3;

        }else if (dateString.equals("Fri")) {
            return 4;

        }
        return 5;
    }

    public static String convertTimeToFormat(String time) {

        String parsedTime = "";

        if(time.substring(1,2).equals(":")){
            parsedTime = "0" + time;
        } else {
            parsedTime = time;
        }

        if(parsedTime.length() == 4) {
            parsedTime = parsedTime.substring(0, 3) + "0" + parsedTime.substring(3);
        }

        int minutes = Integer.valueOf(parsedTime.substring(3, 5));
        int fifteenInterval = minutes/15;
        String hour = "";
        String minute = "";
        if(fifteenInterval == 3) {
            minute = "00";
            hour = String.valueOf(Integer.valueOf(parsedTime.substring(0, 2))+1);
            if (hour.equals("24")) {
                hour="0";
            }
            if(Integer.valueOf(hour)<10) {
                return "0" + hour + ":" + minute;
            } else {
                return hour + ":" + minute;
            }
        }
        return parsedTime.substring(0, 2) + ":" + (fifteenInterval+1) * 15;
    }
}
