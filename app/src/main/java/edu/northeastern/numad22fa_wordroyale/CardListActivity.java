package edu.northeastern.numad22fa_wordroyale;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardListActivity extends AppCompatActivity {
    public static final String TAG = "CardListActivity";
    private RecyclerView cardListRV;
    private CardAdapter cardListAdapter;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private FirebaseRecyclerOptions<Card> options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_card_list);

        options = new FirebaseRecyclerOptions.Builder<Card>()
                .setQuery(rootRef.child("users").child(userAuth.getCurrentUser().getUid()).child("cardList"), Card.class)
                .build();
        cardListAdapter = new CardAdapter(options);

        cardListRV = findViewById(R.id.cardListRV);
        cardListRV.setHasFixedSize(true);
        cardListRV.setAdapter(cardListAdapter);
        cardListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        cardListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cardListAdapter.stopListening();
    }

    public void newCardDialog(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_new_card, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("CREATE A NEW CARD!");

        TextInputLayout newCardFrontLayout = dialogView.findViewById(R.id.newCardFrontInputLayout);
        TextInputLayout newCardBackLayout = dialogView.findViewById(R.id.newCardBackInputLayout);

        dialogBuilder.setPositiveButton("CREATE", (dialog, id) -> {
            String newCardFront = newCardFrontLayout.getEditText().getText().toString().trim();
            String newCardBack = newCardBackLayout.getEditText().getText().toString().trim();

            saveNewCardToDatabase(newCardFront, newCardBack);
        });

        dialogBuilder.setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        dialogBuilder.create().show();
    }

    public void saveNewCardToDatabase(String newCardFront, String newCardBack) {
        if (newCardFront != null && newCardBack != null) {
            rootRef.child("users")
                    .child(userAuth.getCurrentUser().getUid())
                    .child("nextCardID").get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error getting data", task.getException());
                        } else {
                            Log.d(TAG, String.valueOf(task.getResult().getValue()));
                            String newCardID = String.valueOf(task.getResult().getValue());

                            Card newCard = new Card(newCardID, newCardFront, newCardBack);
                            newCard.setCardCreatorUID(userAuth.getCurrentUser().getUid());

                            rootRef.child("users")
                                    .child(userAuth.getCurrentUser().getUid())
                                    .child("cardList")
                                    .child(newCardID)
                                    .setValue(newCard);

                            String nextCardIDPlus = Integer.toString(Integer.parseInt(newCardID) + 1);
                            String nextCardID = String.format("%1$" + 4 + "s", nextCardIDPlus).replace(' ', '0');
                            rootRef.child("users")
                                    .child(userAuth.getCurrentUser().getUid())
                                    .child("nextCardID").setValue(nextCardID);
                            Toast.makeText(CardListActivity.this, "Card created successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e(TAG, "Empty cards are not allowed!");
        }
    }

    public class CardAdapter extends FirebaseRecyclerAdapter<Card, CardViewHolder> {

        public CardAdapter(@NonNull FirebaseRecyclerOptions<Card> options) {
            super(options);
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.activity_card_list_item, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull Card model) {
            holder.cardFrontTV.setText(model.getCardFront());
            holder.cardBackTV.setText(model.getCardBack());
            holder.cardDifficulty.setText(model.getCardDifficulty());
            holder.cardCreatorUID.setText(model.getCardCreatorUID());
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(CardListActivity.this, CardActivity.class);
                Bundle cardBundle = new Bundle();
                cardBundle.putString("CARD ID", model.getCardID());
                cardBundle.putString("CARD FRONT", model.getCardFront());
                cardBundle.putString("CARD BACK", model.getCardBack());
                cardBundle.putString("CARD DIFFICULTY", model.getCardDifficulty());
                cardBundle.putString("CARD CREATOR UID", model.getCardCreatorUID());
                intent.putExtras(cardBundle);
                CardListActivity.this.startActivity(intent);
            });
        }
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardFrontTV;
        TextView cardBackTV;
        TextView cardDifficulty;
        TextView cardCreatorUID;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            cardFrontTV = itemView.findViewById(R.id.cardListItemFront);
            cardBackTV = itemView.findViewById(R.id.cardListItemBack);
            cardDifficulty = itemView.findViewById(R.id.cardListItemDifficulty);
            cardCreatorUID = itemView.findViewById(R.id.cardListItemCreatorUID);
        }
    }
}
