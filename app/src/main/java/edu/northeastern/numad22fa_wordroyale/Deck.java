package edu.northeastern.numad22fa_wordroyale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Deck {
    private String deckName;
    private String deckCreatorUID;
    private long deckSize;

    public Deck() {

    }

    public Deck(String deckName) {
        this.deckName = deckName;
        this.deckCreatorUID = null;
        this.deckSize = 0;
    }

    public String getDeckName() {
        return this.deckName;
    }

    public String getDeckCreatorUID() {
        return this.deckCreatorUID;
    }

    public long getDeckSize() {
        return this.deckSize;
    }

    public void setDeckCreatorUID(String creatorUID) {
        this.deckCreatorUID = creatorUID;
    }
}
