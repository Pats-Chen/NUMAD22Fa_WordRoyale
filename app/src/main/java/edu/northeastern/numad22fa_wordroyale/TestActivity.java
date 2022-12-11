package edu.northeastern.numad22fa_wordroyale;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    public static final String TAG = "TestActivity";
    private AnimatorSet textFrontAnimatorSet;
    private AnimatorSet textBackAnimatorSet;
    private float scale;
    private boolean isFront;
    private String deckName;
    private Deck testDeck;
    private List<Card> testCardList;
    private int cardPositionCursor;
    private TextView cardFrontTV;
    private TextView cardBackTV;
    private TextView cardDifficultyTV;
    private TextView cardCreatorUIDTV;
    private String cardFront;
    private String cardBack;
    private String cardDifficulty;
    private String cardCreatorUID;
    private int testScore;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener listener;
    private boolean isRegisterSuccess;
    private boolean isShuffledFlag;
    private long lastTime;
    private long timeInterval;
    private boolean isInitPosition = false;
    private float lastX;
    private float lastY;
    private float lastZ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        Bundle deckListBundle = intent.getExtras();
        deckName = deckListBundle.getString("DECK NAME");

        isFront = true;
        scale = getApplicationContext().getResources().getDisplayMetrics().density;
        cardFrontTV = findViewById(R.id.testTVCardFront);
        cardBackTV = findViewById(R.id.testTVCardBack);
        cardDifficultyTV = findViewById(R.id.testTVCardDifficulty);
        cardCreatorUIDTV = findViewById(R.id.testTVCardCreatorUID);

        textFrontAnimatorSet = new AnimatorSet();
        textBackAnimatorSet = new AnimatorSet();

        rootRef.child("users")
                .child(userAuth.getCurrentUser().getUid())
                .child("deckList")
                .child(deckName)
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                    } else {
                        testDeck = task.getResult().getValue(Deck.class);
                        Log.d(TAG, String.valueOf(testDeck.getDeckSize()));

                        if (testDeck != null) {
                            testCardList = new ArrayList<>();
                            testDeck.getCardList().forEach((cardID, card) -> testCardList.add(card));
                        }

                        if (testCardList.size() == 30) {
                            isShuffledFlag = false;
                            Toast.makeText(TestActivity.this, "Shake your phone to shuffle the deck!", Toast.LENGTH_SHORT).show();
                        }

                        //TODO: shuffle when shaking the phone
                        testScore = 0;
                        cardPositionCursor = 0;

                        cardFront = testCardList.get(cardPositionCursor).getCardFront();
                        cardBack = testCardList.get(cardPositionCursor).getCardBack();
                        cardCreatorUID = testCardList.get(cardPositionCursor).getCardCreatorUID();
                        cardDifficulty = testCardList.get(cardPositionCursor).getCardDifficulty();

                        cardFrontTV.setText(cardFront);
                        cardFrontTV.setVisibility(View.VISIBLE);
                        cardFrontTV.setCameraDistance(8000 * scale);

                        cardBackTV.setText(cardBack);
                        cardBackTV.setVisibility(View.GONE);
                        cardBackTV.setCameraDistance(8000 * scale);

                        cardDifficultyTV.setText(("CARD DIFFICULTY: " + cardDifficulty));
                        cardDifficultyTV.setVisibility(View.GONE);
                        cardCreatorUIDTV.setText(cardCreatorUID);

                        textFrontAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip_front));
                        textBackAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip_back));
                    }
                });
    }

    public void tryQuestion(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_answer, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("WRITE DOWN YOUR ANSWER!");

        TextInputLayout testAnswerLayout = dialogView.findViewById(R.id.testAnswerInputLayout);

        dialogBuilder.setPositiveButton("CONFIRM", (dialog, id) -> {
            String testAnswer = testAnswerLayout.getEditText().getText().toString().trim();

            if (testAnswer != null) {
                if (testAnswer.compareTo(cardBack) == 0) {
                    if (cardDifficulty.compareTo("EASY") == 0) {
                        testScore += 2;
                    } else {
                        testScore += 4;
                    }
                }
                cardPositionCursor += 1;
                cardDifficultyTV.setVisibility(View.VISIBLE);

                if (isFront) {
                    textFrontAnimatorSet.setTarget(cardFrontTV);
                    textBackAnimatorSet.setTarget(cardBackTV);
                    textFrontAnimatorSet.start();
                    textBackAnimatorSet.start();
                    cardFrontTV.setVisibility(View.GONE);
                    cardBackTV.setVisibility(View.VISIBLE);
                    isFront = false;
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> nextQuestion(), 2500);
            } else {
                Toast.makeText(TestActivity.this, "Empty answers are not allowed!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        dialogBuilder.create().show();
    }

    public void nextQuestion() {
        if (cardPositionCursor >= 30) {
            rootRef.child("users")
                    .child(userAuth.getCurrentUser().getUid())
                    .child("userHighScore")
                    .get().addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "Error getting data", task.getException());
                                } else {
                                    int pastHighScore = task.getResult().getValue(Integer.class);
                                    if (testScore > pastHighScore) {
                                        rootRef.child("users")
                                                .child(userAuth.getCurrentUser().getUid())
                                                .child("userHighScore")
                                                .setValue(testScore);
                                        Toast.makeText(TestActivity.this, "New High Score!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                    });

            Intent deckListActivityIntent = new Intent(this, DeckListActivity.class);
            startActivity(deckListActivityIntent);
            finish();
        } else {
            cardFront = testCardList.get(cardPositionCursor).getCardFront();
            cardBack = testCardList.get(cardPositionCursor).getCardBack();
            cardCreatorUID = testCardList.get(cardPositionCursor).getCardCreatorUID();
            cardDifficulty = testCardList.get(cardPositionCursor).getCardDifficulty();

            if (!isFront) {
                textFrontAnimatorSet.setTarget(cardBackTV);
                textBackAnimatorSet.setTarget(cardFrontTV);
                textFrontAnimatorSet.start();
                textBackAnimatorSet.start();
                cardBackTV.setVisibility(View.GONE);
                cardFrontTV.setVisibility(View.VISIBLE);
                isFront = true;

                cardFrontTV.setText(cardFront);
                cardBackTV.setText(cardBack);
                cardDifficultyTV.setText("CARD DIFFICULTY: " + cardDifficulty);
                cardDifficultyTV.setVisibility(View.GONE);
                cardCreatorUIDTV.setText(cardCreatorUID);
            }
        }
    }

    public void skipQuestion(View v) {
        revealAnswer();

        Handler handler = new Handler();
        handler.postDelayed(() -> nextQuestion(), 2500);
    }

    public void revealAnswer() {
        cardDifficultyTV.setVisibility(View.VISIBLE);
        cardPositionCursor += 1;
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
}
