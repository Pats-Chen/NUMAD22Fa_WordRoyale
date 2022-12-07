package edu.northeastern.numad22fa_wordroyale;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userEmail;
    private String userUID;
    private String password;
    private List<Card> cardList;
    private List<String> friendList;

    public User() {

    }

    public User(String userEmail, String password) {
        this.userEmail = userEmail;
        this.userUID = null;
        this.password = password;
        this.cardList = new ArrayList<>();
        this.cardList.add(new Card("0000", "CARD COUNTER", "0000"));
        this.friendList = new ArrayList<>();
        this.friendList.add(this.userUID);
    }

    public String getUserUID() {
        return this.userUID;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public String getPassword() {
        return this.password;
    }

    public List<Card> getUserCardList() {
        return this.cardList;
    }

    public List<String> getUserFriendList() {
        return this.friendList;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
