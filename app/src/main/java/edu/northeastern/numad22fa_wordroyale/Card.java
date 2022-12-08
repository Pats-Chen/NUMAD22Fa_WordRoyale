package edu.northeastern.numad22fa_wordroyale;

public class Card {
    private String cardID;
    private String cardCreatorUID;
    private String cardFront;
    private String cardBack;
    private String cardDifficulty;

    public Card() {
        this.cardID = null;
        this.cardCreatorUID = null;
        this.cardFront = null;
        this.cardBack = null;
        if (cardBack.length() > 8) {
            this.cardDifficulty = "HARD";
        }
        else {
            this.cardDifficulty = "EASY";
        }
    }

    public Card(String cardID, String cardFront, String cardBack) {
        this.cardID = cardID;
        this.cardCreatorUID = null;
        this.cardFront = cardFront;
        this.cardBack = cardBack;
        if (cardBack.length() > 8) {
            this.cardDifficulty = "HARD";
        }
        else {
            this.cardDifficulty = "EASY";
        }
    }

    public String getCardID() {
        return this.cardID;
    }

    public String getCardCreatorUID() {
        return this.cardCreatorUID;
    }

    public String getCardFront() {
        return this.cardFront;
    }

    public String getCardBack() {
        return this.cardBack;
    }

    public String getCardDifficulty() {
        return this.cardDifficulty;
    }

    public void setCardCreatorUID(String cardCreatorUID) {
        this.cardCreatorUID = cardCreatorUID;
    }
}
