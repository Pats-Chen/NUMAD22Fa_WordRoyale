package edu.northeastern.numad22fa_wordroyale;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userEmail;
    private String userUID;
    private String password;
    private List<Card> cardList;
    private String nextCardID;
    private Integer userHighScore;
    private List<Deck> deckList;
    private List<String> friendList;

    public User() {

    }

    public User(String userEmail, String password) {
        this.userEmail = userEmail;
        this.userUID = null;
        this.password = password;
        this.cardList = new ArrayList<>();
        this.nextCardID = "0001";
        this.userHighScore = 0;
        this.deckList = new ArrayList<>();
        this.friendList = new ArrayList<>();
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

    public String getNextCardID() {
        return this.nextCardID;
    }

    public Integer getUserHighScore() {
        return this.userHighScore;
    }

    public List<Deck> getUserDeckList() {
        return this.deckList;
    }

    public List<String> getUserFriendList() {
        return this.friendList;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public void setUserHighScore(int userHighScore) {
        this.userHighScore = userHighScore;
    }

    public void addCardToList(Card card) {
        this.cardList.add(card);
    }

    public void addFriendToList(String friendUID) {
        this.friendList.add(friendUID);
    }
}
