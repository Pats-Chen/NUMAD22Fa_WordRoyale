package edu.northeastern.numad22fa_wordroyale;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardActivity extends AppCompatActivity {
    private AnimatorSet textFrontAnimatorSet;
    private AnimatorSet textBackAnimatorSet;
    private float scale;
    private boolean isFront;
    private String cardID;
    private String cardFront;
    private String cardBack;
    private String cardDifficulty;
    private String cardCreatorUID;
    private TextView cardFrontTV;
    private TextView cardBackTV;
    private TextView cardDifficultyTV;
    private TextView cardCreatorUIDTV;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_card);

        Intent intent = getIntent();
        Bundle cardBundle = intent.getExtras();
        cardID = cardBundle.getString("CARD ID");
        cardFront = cardBundle.getString("CARD FRONT");
        cardBack = cardBundle.getString("CARD BACK");
        cardDifficulty = cardBundle.getString("CARD DIFFICULTY");
        cardCreatorUID = cardBundle.getString("CARD CREATOR UID");

        isFront = true;
        scale = getApplicationContext().getResources().getDisplayMetrics().density;
        cardFrontTV = findViewById(R.id.cardTVCardFront);
        cardFrontTV.setVisibility(View.VISIBLE);
        cardFrontTV.setCameraDistance(8000 * scale);
        cardFrontTV.setText(cardFront);

        cardBackTV = findViewById(R.id.cardTVCardBack);
        cardBackTV.setVisibility(View.GONE);
        cardBackTV.setCameraDistance(8000 * scale);
        cardBackTV.setText(cardBack);

        cardDifficultyTV = findViewById(R.id.cardTVCardDifficulty);
        cardDifficultyTV.setText("CARD DIFFICULTY: " + cardDifficulty);
        cardCreatorUIDTV = findViewById(R.id.cardTVCardCreatorUID);
        cardCreatorUIDTV.setText("CARD CREATOR UID: " + cardCreatorUID);

        textFrontAnimatorSet = new AnimatorSet();
        textBackAnimatorSet = new AnimatorSet();
        textFrontAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip_front));
        textBackAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip_back));
    }

    public void cardFrontToBack(View v) {
        if (isFront) {
            textFrontAnimatorSet.setTarget(cardFrontTV);
            textBackAnimatorSet.setTarget(cardBackTV);
            textFrontAnimatorSet.start();
            textBackAnimatorSet.start();
            cardFrontTV.setVisibility(View.GONE);
            cardBackTV.setVisibility(View.VISIBLE);
            isFront = false;
        }
    }

    public void cardBackToFront(View v) {
        if (!isFront) {
            textFrontAnimatorSet.setTarget(cardBackTV);
            textBackAnimatorSet.setTarget(cardFrontTV);
            textFrontAnimatorSet.start();
            textBackAnimatorSet.start();
            cardBackTV.setVisibility(View.GONE);
            cardFrontTV.setVisibility(View.VISIBLE);
            isFront = true;
        }
    }

    public void deleteCard(View v) {
        //TODO: delete card.
    }

    public void addCardToDeck(View v) {
        //TODO: add card to deck.
    }
}
