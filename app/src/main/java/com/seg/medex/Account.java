package com.seg.medex;

public class Account {
    private String username;
    private String password;
    private int accountType;
    private String token;

    public Account(String username, String passsword, int accountType, String token) {
        this.username = username;
        this.password = Crypto.getHash(passsword);
        this.accountType = accountType;
        this.token = token;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = Crypto.getHash(password);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
