package com.seg.medex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rw.keyboardlistener.KeyboardUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * The activity for the Sign Up page.
 */
public class SignupActivity extends AppCompatActivity {



    /**
     * Selected item from the spinner.
     */
    private String spinnerSelection;

    /**
     * The dropdown menu called "Spinner."
     */
    private Spinner spinner;

    /**
     * Email check mark image.
     */
    private ImageView emailCheck;

    /**
     * Email "X" image.
     */
    private ImageView emailX;

    /**
     * Email indeterminate circular progress bar.
     */
    private ProgressBar emailCircle;

    /**
     * Username check mark image.
     */
    private ImageView usernameCheck;

    /**
     * Username "X" image.
     */
    private ImageView usernameX;

    /**
     * Username indeterminate circular progress bar.
     */
    private ProgressBar usernameCircle;

    /**
     * Password check mark image.
     */
    private ImageView passwordCheck;

    /**
     * Password "X" image.
     */
    private ImageView passwordX;

    /**
     * Confirm password check mark image.
     */
    private ImageView confirmPasswordCheck;

    /**
     * Username text field.
     */
    private EditText username;

    /**
     * Password text field.
     */
    private EditText password;

    /**
     * Confirm password "X" image.
     */
    private ImageView confirmPasswordX;

    /**
     * Email text field.
     */
    private EditText email;

    /**
     * Confirm password text field.
     */
    private EditText confirmPassword;

    /**
     * Sign up button.
     */
    private Button signUpButton;

    /**
     * Sign up circle.
     */
    private ProgressBar signUpCircle;

    /**
     * The Firebase Firestore database object.
     */
    private FirebaseFirestore db;


    /**
     * Everything that happens when the activity gets created/loaded.
     * @param savedInstanceState the saved instance from the last time you loaded the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initialize variables and hide certain ones that we don't yet need
        initializeVariables();
        invisibleElements();

        //creates Spinner Adapter so that I can actually use it and defaults an item.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //sets all the listeners so that they listen to specific actions (more description in the actual methods)
        setUsernameListener();
        setEmailListener();
        //setPasswordListener();
        //setConfirmPasswordListener();
        setConfirmPasswordEnterListener();
        setOnTouchListener();
        setKeyboardListener();
    }

    /**
     * What happens when we click the signup button, or when we press enter on the last text box.
     * Clears focus on all the texts, which allows checkers to go through and specify if the
     * text fields are valid or not, checks the database to see if the email or username already
     * exists on there, and if all the checks pass, then calls a method to send the information to
     * the database. Some checks are duplicated as a safety measure and act as covers to the holes
     * I discovered before.
     * @param view the view that this method is associated with (the button in our case)
     */
    public void onSignupClick(View view) {

        //disable the button so that we can't spam click and add several of the same accounts before a callback is reached
        //every time we return from this function with a result, the button is re-enabled.
        findViewById(R.id.signup_button_layout).setEnabled(false);

        ImageView[] xes = new ImageView[]{confirmPasswordX, usernameX, emailX, passwordX};

        for(ImageView x: xes) {
            if(x.getVisibility() == View.VISIBLE) {
                x.setVisibility(View.INVISIBLE);
            }
        }
        //some variables to make things easier to refer to instead of writing .getText().toString() every time
        final String emailText = email.getText().toString();
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();
        final String confirmPasswordText = confirmPassword.getText().toString();

        //clears the focus of all the text boxes (which triggers a check and a graphic is displayed showing you if a value is correct or not)
        email.clearFocus();
        username.clearFocus();
        password.clearFocus();
        confirmPassword.clearFocus();

        //checks if the email is valid and displays graphic accordingly
        if (!Utility.validEmail(emailText) && emailX.getVisibility() != View.VISIBLE) {
            emailCircle.setVisibility(View.INVISIBLE);
            emailCheck.setVisibility(View.INVISIBLE);
            emailX.setVisibility(View.VISIBLE);
            invalidEmail();
            findViewById(R.id.signup_button_layout).setEnabled(true);
        } else {
            emailCircle.setVisibility(View.INVISIBLE);
            emailCheck.setVisibility(View.VISIBLE);
            emailX.setVisibility(View.INVISIBLE);
        }

        //checks if the username is valid and displays graphic accordingly
        if (!Utility.validUsername(usernameText) && usernameX.getVisibility() != View.VISIBLE) {
            usernameCircle.setVisibility(View.INVISIBLE);
            usernameCheck.setVisibility(View.INVISIBLE);
            usernameX.setVisibility(View.VISIBLE);
            invalidUsername();
            findViewById(R.id.signup_button_layout).setEnabled(true);
        } else {
            usernameCircle.setVisibility(View.INVISIBLE);
            usernameCheck.setVisibility(View.VISIBLE);
            usernameX.setVisibility(View.INVISIBLE);
        }

        //checks if password is valid and displays a graphic accordingly
        if (!Utility.validPassword(passwordText) && passwordX.getVisibility() != View.VISIBLE) {
            passwordCheck.setVisibility(View.INVISIBLE);
            passwordX.setVisibility(View.VISIBLE);
            invalidPassword();
            findViewById(R.id.signup_button_layout).setEnabled(true);
        } else {
            passwordCheck.setVisibility(View.VISIBLE);
            passwordX.setVisibility(View.INVISIBLE);
        }

        //checks if the confirmed password matches the password text and displays a graohic accordingly
        if ((!Utility.passwordsMatch(passwordText, confirmPasswordText) && confirmPasswordX.getVisibility() != View.VISIBLE) || !Utility.validPassword(confirmPasswordText)) {
            confirmPasswordCheck.setVisibility(View.INVISIBLE);
            confirmPasswordX.setVisibility(View.VISIBLE);
            passwordsDontMatch();
            findViewById(R.id.signup_button_layout).setEnabled(true);
        } else {
            confirmPasswordCheck.setVisibility(View.VISIBLE);
            confirmPasswordX.setVisibility(View.INVISIBLE);
        }

        if(emailX.getVisibility() == View.VISIBLE
                || usernameX.getVisibility() == View.VISIBLE
                || passwordX.getVisibility() == View.VISIBLE
                || confirmPasswordX.getVisibility() == View.VISIBLE) {
            return;
        }

        //checks the database if the username is already in there
        signUpCircle.setVisibility(View.VISIBLE);
        signUpButton.setText("");
        db.collection("users")
                .whereEqualTo("username", username.getText().toString().toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    //gets a return message from the server
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //if the request didn't fail
                        if (task.isSuccessful()) {
                            //if the result is empty (username doesn't exist)
                            if (task.getResult().isEmpty()) {
                                if(!visibleX()) {
                                    //checks the database to see if the email already exists
                                    db.collection("users")
                                            .whereEqualTo("email", emailText.toLowerCase())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                //gets a return message from the server
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    //if the request didn't fail
                                                    if (task.isSuccessful()) {
                                                        //if the result is empty (email doesn't exist)
                                                        if (task.getResult().isEmpty()) {
                                                            findViewById(R.id.signup_button_layout).setEnabled(true);
                                                            if(!visibleX()) {
                                                                //gets the spinner item and converts it to the according account type number
                                                                spinnerSelection = spinner.getSelectedItem().toString();
                                                                int accountType = spinnerSelection.toLowerCase().startsWith("c") ? 0 : 1;
                                                                //adds information to an Account object
                                                                Account account = new Account(usernameText, passwordText, accountType, emailText);
                                                                logUserInfo(account);
                                                                //sends the account info
                                                                sendUserInfo(account);
                                                                signUpCircle.setVisibility(View.INVISIBLE);
                                                                signUpButton.setText(R.string.sign_up);
                                                                startProfile();
                                                            }
                                                        } else {
                                                            //if the email exists, don't send and show an "X" symbol
                                                            findViewById(R.id.signup_button_layout).setEnabled(true);
                                                            signUpCircle.setVisibility(View.INVISIBLE);
                                                            signUpButton.setText(R.string.sign_up);
                                                            emailCircle.setVisibility(View.INVISIBLE);
                                                            emailCheck.setVisibility(View.INVISIBLE);
                                                            emailX.setVisibility(View.VISIBLE);
                                                            emailExists();
                                                        }
                                                    }
                                                }
                                            });
                                }
                            } else {
                                //if the username exists, don't send and show an "X" symbol
                                findViewById(R.id.signup_button_layout).setEnabled(true);
                                signUpCircle.setVisibility(View.INVISIBLE);
                                signUpButton.setText(R.string.sign_up);
                                usernameCircle.setVisibility(View.INVISIBLE);
                                usernameCheck.setVisibility(View.INVISIBLE);
                                usernameX.setVisibility(View.VISIBLE);
                                usernameExists();
                            }
                        }
                    }
                });

    }

    private boolean visibleX() {
        return (emailX.getVisibility() == View.VISIBLE
                || usernameX.getVisibility() == View.VISIBLE
                || passwordX.getVisibility() == View.VISIBLE
                || confirmPasswordX.getVisibility() == View.VISIBLE);
    }

    /**
     * Send the user info
     * @param account the Account object that is sent to this method with all the account information.
     */
    private void sendUserInfo(Account account) {
        //Firebase accepts HashMaps, so we put the account information in lowercase (for consistency) in one
        Map<String, Object> user = new HashMap<>();
        user.put("username", account.getUsername().toLowerCase());
        user.put("password", account.getPassword().toLowerCase());
        user.put("account_type", account.getAccountType());
        user.put("email", account.getEmail().toLowerCase());
        user.put("created_profile", false);
        if (account.getAccountType() == 1) {
            ArrayList<String> times = new ArrayList<>();
            for(int i = 0; i<7; i++) {
                times.add(" - ");
            }
            user.put("start_times", times);
            user.put("end_times", times);
            user.put("services", new ArrayList<String>());
            user.put("ratings", new ArrayList<>());
        }

        //sends off the HashMap to the server
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SIGNUP: ", "DocumentSnapshot written with ID: " + documentReference.getId());
                        successfulLogin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SIGNUP: ", "ERROR: ", e);
                        failedLogin();
                    }
                });
    }
    
    /**
     * Saves the user info locally
     * @param account the Account object that is sent to this method with all the account information.
     */

    private void logUserInfo(Account account) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", account.getUsername().toLowerCase());
        editor.putString("password", account.getPassword().toLowerCase());
        editor.putString("email", account.getEmail().toLowerCase());
        editor.putInt("account_type", account.getAccountType());
        editor.putBoolean("created_profile", account.isCompleteProfile());
        editor.putBoolean("light_mode", true);
        editor.putBoolean("logged_in", true);
        editor.apply();
    }

    /**
     * Message shown when a login/signup has been successful.
     */
    private void successfulLogin() {
        Toast.makeText(this, "Success! Signed up and logged in.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message shown when a login/signup has failed.
     */
    private void failedLogin() {
        Toast.makeText(this, "Failed! Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message shown when an invalid username has been input.
     */
    private void invalidUsername() {
        Toast.makeText(this, "Invalid username.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message shown when a username already exists in the database.
     */
    private void usernameExists() {
        Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message shown when an invalid username has been input.
     */
    private void invalidEmail() {
        Toast.makeText(this, "Invalid email.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message shown when an email already exists in the database.
     */
    private void emailExists() {
        Toast.makeText(this, "Email already exists.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message shown when an invalid password has been input.
     */
    private void invalidPassword() {
        Toast.makeText(this, "Invalid password: make sure that your password is at least 8 characters.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message shown when a password doesn't match it's confirmed password.
     */
    private void passwordsDontMatch() {
        Toast.makeText(this, "Passwords don't match. Try again.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener on the email text box. Runs a database check on the email as soon as you
     * click off or, more technically, focus is cleared from the email text box.
     * Shows an symbol accordingly.
     */
    private void setEmailListener() {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !email.getText().toString().isEmpty()) {
                    if (!Utility.validEmail(email.getText().toString())) {
                        emailCircle.setVisibility(View.INVISIBLE);
                        emailCheck.setVisibility(View.INVISIBLE);
                        emailX.setVisibility(View.VISIBLE);
                        invalidEmail();
                        findViewById(R.id.signup_button_layout).setEnabled(true);
                        return;
                    } else {
                        emailCircle.setVisibility(View.INVISIBLE);
                        emailCheck.setVisibility(View.VISIBLE);
                        emailX.setVisibility(View.INVISIBLE);
                        findViewById(R.id.signup_button_layout).setEnabled(true);
                    }
                    emailCircle.setVisibility(View.VISIBLE);
                    emailCheck.setVisibility(View.INVISIBLE);
                    emailX.setVisibility(View.INVISIBLE);
                    db.collection("users")
                            .whereEqualTo("email", email.getText().toString().toLowerCase())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            emailCircle.setVisibility(View.INVISIBLE);
                                            emailX.setVisibility(View.INVISIBLE);
                                            emailCheck.setVisibility(View.VISIBLE);
                                        } else {
                                            emailCircle.setVisibility(View.INVISIBLE);
                                            emailCheck.setVisibility(View.INVISIBLE);
                                            emailX.setVisibility(View.VISIBLE);
                                            emailExists();
                                        }
                                    }
                                }
                            });
                } else {
                    emailCircle.setVisibility(View.INVISIBLE);
                    emailCheck.setVisibility(View.INVISIBLE);
                    emailX.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Listener on the username text box. Runs a database check on the username as soon as you
     * click off or, more technically, focus is cleared from the username text box. Shows a
     * symbol accordingly.
     */
    private void setUsernameListener() {
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !username.getText().toString().isEmpty()) {
                    if (!Utility.validUsername(username.getText().toString().toLowerCase())) {
                        usernameCircle.setVisibility(View.INVISIBLE);
                        usernameCheck.setVisibility(View.INVISIBLE);
                        usernameX.setVisibility(View.VISIBLE);
                        findViewById(R.id.signup_button_layout).setEnabled(true);
                        invalidUsername();
                        return;
                    } else {
                        usernameCircle.setVisibility(View.INVISIBLE);
                        usernameCheck.setVisibility(View.VISIBLE);
                        usernameX.setVisibility(View.INVISIBLE);
                        findViewById(R.id.signup_button_layout).setEnabled(true);
                    }
                    usernameCircle.setVisibility(View.VISIBLE);
                    usernameCheck.setVisibility(View.INVISIBLE);
                    usernameX.setVisibility(View.INVISIBLE);
                    db.collection("users")
                            .whereEqualTo("username", username.getText().toString().toLowerCase())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            usernameCircle.setVisibility(View.INVISIBLE);
                                            usernameX.setVisibility(View.INVISIBLE);
                                            usernameCheck.setVisibility(View.VISIBLE);
                                        } else {
                                            usernameCircle.setVisibility(View.INVISIBLE);
                                            usernameCheck.setVisibility(View.INVISIBLE);
                                            usernameX.setVisibility(View.VISIBLE);
                                            usernameExists();
                                        }
                                    }
                                }
                            });
                } else {
                    usernameCircle.setVisibility(View.INVISIBLE);
                    usernameCheck.setVisibility(View.INVISIBLE);
                    usernameX.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Listener on the password text box. Checks for the password validity as soon as
     * the focus is cleared from the text box. Shows a symbol accordingly.
     */
    private void setPasswordListener() {
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !password.getText().toString().isEmpty()) {
                    if (Utility.validPassword(password.getText().toString())) {
                        passwordCheck.setVisibility(View.VISIBLE);
                        passwordX.setVisibility(View.INVISIBLE);
                    } else {
                        passwordCheck.setVisibility(View.INVISIBLE);
                        passwordX.setVisibility(View.VISIBLE);
                        invalidPassword();
                    }
                } else {
                    passwordCheck.setVisibility(View.INVISIBLE);
                    passwordX.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Listener on the confirm password text box. Checks if the password matches in the
     * first and second box. Shows an symbol accordingly. Also, if enter is pressed, since
     * this is the last text box, it will automatically send the info off as though the user
     * has pressed the sign up button.
     */
    private void setConfirmPasswordListener() {
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !confirmPassword.getText().toString().isEmpty()) {
                    if (Utility.passwordsMatch(password.getText().toString(), confirmPassword.getText().toString())) {
                        confirmPasswordCheck.setVisibility(View.VISIBLE);
                        confirmPasswordX.setVisibility(View.INVISIBLE);
                    } else {
                        confirmPasswordCheck.setVisibility(View.INVISIBLE);
                        confirmPasswordX.setVisibility(View.VISIBLE);
                        passwordsDontMatch();
                    }
                } else {
                    confirmPasswordCheck.setVisibility(View.INVISIBLE);
                    confirmPasswordX.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Listener for the enter key on the keyboard
     * when on the confirm password text box so that it
     * presses the signup button and clears the focus on press.
     */
    public void setConfirmPasswordEnterListener() {
        //hides keyboard and sends text box info as soon as enter is pressed
        confirmPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    confirmPassword.clearFocus();
                    onSignupClick(signUpButton);
                    View view = findViewById(R.id.light_login);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * AESTHETIC method that changes the sign up button color on press, and on release
     * it executes the onSignupClick() method.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener() {
        signUpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        signUpButton.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        signUpButton.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        onSignupClick(v);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
    }

    /**
     * Deselects all the text boxes when the keyboard is brought down.
     */
    private void setKeyboardListener() {
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if(!isVisible) {
                    email.clearFocus();
                    username.clearFocus();
                    password.clearFocus();
                    confirmPassword.clearFocus();
                }
            }
        });
    }

    /**
     * Initialized all variables. Made this into its own function because it crowded up the onCreate() method.
     */
    private void initializeVariables() {
        //SPINNER
        this.spinner = findViewById(R.id.spinner);

        //Firebase Database
        this.db = FirebaseFirestore.getInstance();

        //email
        this.emailCircle = findViewById(R.id.email_circle);
        this.emailCheck = findViewById(R.id.email_check);
        this.emailX = findViewById(R.id.email_x);
        this.email = findViewById(R.id.email);
        this.email.setText("");

        //username
        this.usernameCircle = findViewById(R.id.username_circle);
        this.usernameCheck = findViewById(R.id.username_check);
        this.usernameX = findViewById(R.id.username_x);
        this.username = findViewById(R.id.username);
        this.username.setText("");

        //password
        this.password = findViewById(R.id.last_name);
        this.passwordCheck = findViewById(R.id.password_check);
        this.passwordX = findViewById(R.id.password_x);
        this.password.setText("");

        //confirm password
        this.confirmPassword = findViewById(R.id.password2);
        this.confirmPasswordCheck = findViewById(R.id.password2_check);
        this.confirmPasswordX = findViewById(R.id.password2_x);
        this.confirmPassword.setText("");

        //signup button
        this.signUpButton = findViewById(R.id.signup_button);
        this.signUpCircle = findViewById(R.id.sign_up_circle);

    }

    private void startProfile() {
        SharedPreferences preferences = getSharedPreferences("ID", 0);

        startActivity(new Intent(this, ProfileActivity.class));
        finish();
        return;
    }

    /**
     * Makes the elements that we don't need (such as the checkbox) invisible when you just load up the
     * app. It'd make it a bit hostile to have red x's everywhere before you even did something wrong.
     */
    private void invisibleElements() {

        //username
        this.usernameCheck.setVisibility(View.INVISIBLE);
        this.usernameX.setVisibility(View.INVISIBLE);
        //username circle recolor and hide
        Drawable usernameDrawable = usernameCircle.getIndeterminateDrawable().mutate();
        usernameDrawable.setColorFilter(0xFF134153, android.graphics.PorterDuff.Mode.SRC_IN);
        emailCircle.setProgressDrawable(usernameDrawable);
        this.usernameCircle.setVisibility(View.INVISIBLE);

        //email
        this.emailCheck.setVisibility(View.INVISIBLE);
        this.emailX.setVisibility(View.INVISIBLE);
        //email circle recolor and hide
        Drawable emailDrawable = emailCircle.getIndeterminateDrawable().mutate();
        emailDrawable.setColorFilter(0xFF134153, android.graphics.PorterDuff.Mode.SRC_IN);
        emailCircle.setProgressDrawable(emailDrawable);
        this.emailCircle.setVisibility(View.INVISIBLE);

        //password
        this.passwordX.setVisibility(View.INVISIBLE);
        this.passwordCheck.setVisibility(View.INVISIBLE);

        //confirm password
        this.confirmPasswordX.setVisibility(View.INVISIBLE);
        this.confirmPasswordCheck.setVisibility(View.INVISIBLE);

        this.signUpCircle.setVisibility(View.INVISIBLE);

    }

    /**
     * Starts up the login activity and closes this one.
     * @param view The View associated with the class (namely the "log in instead" button.
     */
    public void loginInstead(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
