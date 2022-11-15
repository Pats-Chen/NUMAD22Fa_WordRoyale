package edu.northeastern.numad22fa_wordroyale;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CardActivity extends AppCompatActivity {
    private AnimatorSet textFrontAnimatorSet;
    private AnimatorSet textBackAnimatorSet;
    private float scale;
    private boolean isFront;
    private TextView cardFrontTV;
    private TextView cardBackTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        isFront = true;
        scale = getApplicationContext().getResources().getDisplayMetrics().density;
        cardFrontTV = findViewById(R.id.cardTVCardFront);
        cardFrontTV.setVisibility(View.VISIBLE);
        cardFrontTV.setCameraDistance(8000 * scale);

        cardBackTV = findViewById(R.id.cardTVCardBack);
        cardBackTV.setVisibility(View.GONE);
        cardBackTV.setCameraDistance(8000 * scale);

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
}
