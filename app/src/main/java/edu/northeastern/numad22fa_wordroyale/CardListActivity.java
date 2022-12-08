package edu.northeastern.numad22fa_wordroyale;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CardListActivity extends AppCompatActivity {
    public static final String TAG = "CardListActivity";
    private RecyclerView cardListRV;
    private CardAdapter cardListAdapter;
    private List<Card> cardList;
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
        setContentView(R.layout.activity_card_list);
        cardListRV = findViewById(R.id.cardListRV);

        cardList = new ArrayList<>();
        cardList.add(new Card("0001", "测试", "ceshi"));
        cardList.add(new Card("0002", "单词", "danci"));

        cardListAdapter = new CardAdapter(this, cardList);
        cardListRV.setAdapter(cardListAdapter);
        cardListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private Context context;
        private List<Card> cardList;

        CardAdapter(Context context, List<Card> cardList) {
            this.context = context;
            this.cardList = cardList;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.activity_card_list_item, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            holder.cardFrontTV.setText(cardList.get(position).getCardFront());
            holder.cardBackTV.setText(cardList.get(position).getCardBack());
            holder.cardDifficulty.setText(cardList.get(position).getCardDifficulty());
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, CardActivity.class);
                Bundle cardBundle = new Bundle();
                cardBundle.putString("CARD ID", cardList.get(position).getCardID());
                cardBundle.putString("CARD FRONT", cardList.get(position).getCardFront());
                cardBundle.putString("CARD BACK", cardList.get(position).getCardBack());
                cardBundle.putString("CARD DIFFICULTY", cardList.get(position).getCardDifficulty());
                intent.putExtras(cardBundle);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardFrontTV;
        TextView cardBackTV;
        TextView cardDifficulty;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            cardFrontTV = itemView.findViewById(R.id.cardListItemFront);
            cardBackTV = itemView.findViewById(R.id.cardListItemBack);
            cardDifficulty = itemView.findViewById(R.id.cardListItemDifficulty);
        }
    }

    public void newCardDialog(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_new_card, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("CREATE A NEW CARD!");

        TextInputLayout newCardFrontLayout = dialogView.findViewById(R.id.newCardFrontInputLayout);
        TextInputLayout newCardBackLayout = dialogView.findViewById(R.id.newCardBackInputLayout);

        dialogBuilder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String newCardFront = newCardFrontLayout.getEditText().getText().toString().trim();
                String newCardBack = newCardBackLayout.getEditText().getText().toString().trim();

                saveNewCardToDatabase(newCardFront, newCardBack);
            }
        });

        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

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
                        }
                    });
        } else {
            Log.e(TAG, "Empty cards are not allowed!");
        }
    }
}
