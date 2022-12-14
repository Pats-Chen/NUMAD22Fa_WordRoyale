package edu.northeastern.numad22fa_wordroyale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private TextView userEmail;
    private TextView userUID;
    private TextView userHighScore;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (userAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        userEmail = findViewById(R.id.mainUserEmail);
        userUID = findViewById(R.id.mainUserUID);
        userHighScore = findViewById(R.id.mainUserHighScore);

        DatabaseReference userRef = rootRef.child("users").child(userAuth.getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userEmail.setText(user.getUserEmail());
                    userUID.setText(user.getUserUID());
                    userHighScore.setText(String.valueOf(user.getUserHighScore()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logout(View v) {
        userAuth.signOut();
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(loginActivityIntent);
        finish();
    }

    public void cardListActivity(View v) {
        Intent cardListActivityIntent = new Intent(this, CardListActivity.class);
        startActivity(cardListActivityIntent);
    }

    public void deckListActivity(View v) {
        Intent deckListActivityIntent = new Intent(this, DeckListActivity.class);
        startActivity(deckListActivityIntent);
    }

    public void friendListActivity(View v) {
        Intent friendListActivityIntent = new Intent(this, FriendListActivity.class);
        startActivity(friendListActivityIntent);
    }
}