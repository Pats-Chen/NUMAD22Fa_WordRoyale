package edu.northeastern.numad22fa_wordroyale;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
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
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SensorEventListener mSensorEventListener;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    private boolean isShuffledFlag;

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

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
                double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
                accelerationPreviousValue = accelerationCurrentValue;

                if (changeInAcceleration > 5 && isShuffledFlag == false) {
                    Log.e(TAG, testCardList.get(0).getCardFront());
                    Collections.shuffle(testCardList);
                    Toast.makeText(TestActivity.this, "Deck shuffled!", Toast.LENGTH_SHORT).show();
                    isShuffledFlag = true;
                    Log.e(TAG, testCardList.get(0).getCardFront());
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> initializeQuestion(), 1000);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

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
                            mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                            Toast.makeText(TestActivity.this, "Shake your phone to shuffle the deck in 3 seconds!", Toast.LENGTH_SHORT).show();
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            mSensorManager.unregisterListener(mSensorEventListener);
                        }, 3000);

                        initializeQuestion();
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
                cardDifficultyTV.setAlpha(1);

                if (isFront) {
                    textFrontAnimatorSet.setTarget(cardFrontTV);
                    textBackAnimatorSet.setTarget(cardBackTV);
                    textFrontAnimatorSet.start();
                    textBackAnimatorSet.start();
                    cardFrontTV.setAlpha(0);
                    cardBackTV.setAlpha(1);
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

    public void initializeQuestion() {
        testScore = 0;
        cardPositionCursor = 0;

        cardFront = testCardList.get(cardPositionCursor).getCardFront();
        cardBack = testCardList.get(cardPositionCursor).getCardBack();
        cardCreatorUID = testCardList.get(cardPositionCursor).getCardCreatorUID();
        cardDifficulty = testCardList.get(cardPositionCursor).getCardDifficulty();

        cardFrontTV.setText(cardFront);
        cardFrontTV.setAlpha(1);
        cardFrontTV.setCameraDistance(8000 * scale);

        cardBackTV.setText(cardBack);
        cardBackTV.setAlpha(0);
        cardBackTV.setCameraDistance(8000 * scale);

        cardDifficultyTV.setText(String.format(getResources().getString(R.string.card_card_difficulty_hint), cardDifficulty));
        cardDifficultyTV.setAlpha(0);
        cardCreatorUIDTV.setText(cardCreatorUID);

        textFrontAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip_front));
        textBackAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip_back));
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
                                    Integer pastHighScore = task.getResult().getValue(Integer.class);
                                    if (pastHighScore == null) {
                                        Log.e(TAG, String.valueOf(pastHighScore));
                                    }
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
            super.onBackPressed();
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
                cardBackTV.setAlpha(0);
                cardFrontTV.setAlpha(1);
                isFront = true;

                cardFrontTV.setText(cardFront);
                cardBackTV.setText(cardBack);
                cardDifficultyTV.setText(String.format(getResources().getString(R.string.card_card_difficulty_hint), cardDifficulty));
                cardDifficultyTV.setAlpha(0);
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
        cardDifficultyTV.setAlpha(1);
        cardPositionCursor += 1;
        if (isFront) {
            textFrontAnimatorSet.setTarget(cardFrontTV);
            textBackAnimatorSet.setTarget(cardBackTV);
            textFrontAnimatorSet.start();
            textBackAnimatorSet.start();
            cardFrontTV.setAlpha(0);
            cardBackTV.setAlpha(1);
            isFront = false;
        }
    }
}
