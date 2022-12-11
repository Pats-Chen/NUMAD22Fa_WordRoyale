package edu.northeastern.numad22fa_wordroyale;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardActivity extends AppCompatActivity {
    public static final String TAG = "CardActivity";
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
        cardDifficultyTV.setText(String.format(getResources().getString(R.string.card_card_difficulty_hint), cardDifficulty));
        cardCreatorUIDTV = findViewById(R.id.cardTVCardCreatorUID);
        cardCreatorUIDTV.setText(cardCreatorUID);

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

    public void addCardToDeck(View v) {
        Card thisCard = new Card(cardID, cardFront, cardBack);
        thisCard.setCardCreatorUID(cardCreatorUID);

        selectDeckDialog(thisCard);
    }

    public void selectDeckDialog(Card thisCard) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_deck, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("ADD THE CARD TO A DECK!");

        TextInputLayout selectDeckNameLayout = dialogView.findViewById(R.id.selectDeckNameInputLayout);

        dialogBuilder.setPositiveButton("CONFIRM", (dialog, id) -> {

            String selectedDeckName = selectDeckNameLayout.getEditText().getText().toString().trim();

            if (selectedDeckName == null) {
                Toast.makeText(CardActivity.this, "Deck name can not be empty!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                rootRef.child("users")
                        .child(userAuth.getCurrentUser().getUid())
                        .child("deckList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(selectedDeckName)) {
                            if (snapshot.child(selectedDeckName).child("cardHashMap").hasChild(thisCard.getCardID())) {
                                Toast.makeText(CardActivity.this, "This card is already in this deck!", Toast.LENGTH_SHORT).show();
                            } else {
                                rootRef.child("users")
                                        .child(userAuth.getCurrentUser().getUid())
                                        .child("deckList")
                                        .child(selectedDeckName)
                                        .child("cardList")
                                        .child(thisCard.getCardID())
                                        .setValue(thisCard);

                                rootRef.child("users")
                                        .child(userAuth.getCurrentUser().getUid())
                                        .child("deckList")
                                        .child(selectedDeckName)
                                        .child("deckSize")
                                        .get().addOnCompleteListener(task -> {
                                            if (!task.isSuccessful()) {
                                                Log.e(TAG, "Error getting data", task.getException());
                                            } else {
                                                Log.d(TAG, String.valueOf(task.getResult().getValue()));
                                                Long deckSize = (Long) task.getResult().getValue();


                                                rootRef.child("users")
                                                        .child(userAuth.getCurrentUser().getUid())
                                                        .child("deckList")
                                                        .child(selectedDeckName)
                                                        .child("deckSize")
                                                        .setValue(deckSize + 1);
                                            }
                                        });

                                Toast.makeText(CardActivity.this, "Card added successfully!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CardActivity.this, "This deck does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        dialogBuilder.setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        dialogBuilder.create().show();
    }
}
