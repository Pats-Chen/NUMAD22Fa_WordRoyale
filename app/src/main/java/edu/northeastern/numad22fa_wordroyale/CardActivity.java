package edu.northeastern.numad22fa_wordroyale;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CardActivity extends AppCompatActivity {
    private AnimatorSet imageAnimatorSet;
    private AnimatorSet textAnimatorSet;
    private float scale;
    private ImageView cardFrontIV;
    private ImageView cardBackIV;
    private TextView cardFrontTV;
    private TextView cardBackTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        scale = getApplicationContext().getResources().getDisplayMetrics().density;
        cardFrontIV = findViewById(R.id.cardIVCardFront);
        cardBackIV = findViewById(R.id.cardIVCardBack);
        cardFrontTV = findViewById(R.id.cardTVCardFront);
        cardBackTV = findViewById(R.id.cardTVCardBack);

        cardFrontIV.setCameraDistance(8000 * scale);
        cardBackIV.setCameraDistance(8000 * scale);

        cardFrontTV.setCameraDistance(8000 * scale);
        cardBackTV.setCameraDistance(8000 * scale);

        imageAnimatorSet = new AnimatorSet();
        textAnimatorSet = new AnimatorSet();
        imageAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip));
        textAnimatorSet.play(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.animator_card_flip));
    }

    public void cardFlip(View v) {
        imageAnimatorSet.setTarget(cardFrontIV);
        textAnimatorSet.setTarget(cardFrontTV);
        imageAnimatorSet.start();
        textAnimatorSet.start();
    }
}
