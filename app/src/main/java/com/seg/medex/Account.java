package com.seg.medex;

public class Account {
    private String username;
    private String password;
    private int accountType;
    private String email;
    private String firstName = "";
    private String lastName = "";
    private boolean completeProfile = false;

    public Account(String username, String passsword, int accountType, String email) {
        this.username = username;
        this.password = Crypto.getHash(passsword);
        this.accountType = accountType;
        this.email = email;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isCompleteProfile() {
        return completeProfile;
    }

    public void setPassword(String password) {
        this.password = Crypto.getHash(password);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCompleteProfile(boolean isComplete) {
        this.completeProfile = isComplete;
    }
}
