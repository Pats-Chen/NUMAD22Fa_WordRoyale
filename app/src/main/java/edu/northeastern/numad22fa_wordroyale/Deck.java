package edu.northeastern.numad22fa_wordroyale;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private String deckName;
    private String deckCreatorUID;
    private List<Card> cardList;

    public Deck() {
        this.deckName = null;
        this.deckCreatorUID = null;
        this.cardList = new ArrayList<>();
    }

    public Deck(String deckName) {
        this.deckName = deckName;
        this.deckCreatorUID = null;
        this.cardList = new ArrayList<>();
    }

    public String getDeckName() {
        return this.deckName;
    }

    public String getDeckCreatorUID() {
        return this.deckCreatorUID;
    }

    public List<Card> getCardList() {
        return this.cardList;
    }

    public int getDeckSize() {
        return this.cardList.size();
    }

    public void setDeckCreatorUID(String creatorUID) {
        this.deckCreatorUID = creatorUID;
    }
}
