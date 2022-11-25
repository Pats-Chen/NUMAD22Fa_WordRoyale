package edu.northeastern.numad22fa_wordroyale;

public class User {
    private String userEmail;
    private String password;

    public User() {

    }

    public User(String userEmail, String password) {
        this.userEmail = userEmail;
        this.password = password;
    }

//    public String getUserID() {
//        return this.userID;
//    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public String getPassword() {
        return this.password;
    }
}
