package edu.northeastern.numad22fa_wordroyale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
//    private Button loginActivityButton;
//    private Button cardActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        loginActivityButton = findViewById(R.id.mainButtonLoginActivity);
//        cardActivityButton = findViewById(R.id.mainButtonCardActivity);
    }

    public void cardActivity(View v) {
        Intent cardActivityIntent = new Intent(this, CardActivity.class);
        startActivity(cardActivityIntent);
    }

    public void loginActivity(View v) {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(loginActivityIntent);
    }
}