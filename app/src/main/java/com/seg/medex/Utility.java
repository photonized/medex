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
        Log.d("UTILITY", isAlphanumeric(username) + " : " + username);
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
    private static boolean isAlphanumeric(String s) {
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
    private static boolean isAlpha(String s) {
        char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        s = s.toLowerCase();
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
    private static boolean includes(char[] arr, char c){
        for(char x : arr) {
            if(x==c) {return true;}
        }
        return false;
    }
//    /**
//     * Checks if name (service) already exists in the services database
//     * @param name service name
//     * @return true if name is in services database
//     */
//
//    private boolean checkServices(String name){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("service").whereEqualTo("email", name.toLowerCase())
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot query = task.getResult();
//                    if(!(query.isEmpty())){
//                        returnTrue();
//                    }
//                }
//            }
//        });
//        return false;
//
//
//    }
//    /**
//     * @return true
//     */
//    private boolean returnTrue(){
//        return true;
//    }
}
