package edu.northeastern.numad22fa_wordroyale;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private String deckName;
    private List<Card> cardList;

    public Deck() {
        this.cardList = new ArrayList<>();
    }

    public Deck(String deckName) {
        this.deckName = deckName;
        this.cardList = new ArrayList<>();
    }

    public String getDeckName() {
        return this.deckName;
    }

    public List<Card> getCardList() {
        return this.cardList;
    }

    public int getDeckSize() {
        return this.cardList.size();
    }
}
