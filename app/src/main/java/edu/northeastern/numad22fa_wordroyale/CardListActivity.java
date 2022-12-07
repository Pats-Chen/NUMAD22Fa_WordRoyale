package edu.northeastern.numad22fa_wordroyale;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CardListActivity extends AppCompatActivity {
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

    public void newCard(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(getLayoutInflater().inflate(R.layout.dialog_create_new_card, null));
        dialogBuilder.setTitle("CREATE A NEW CARD!");

        TextInputEditText newCardFrontET = findViewById(R.id.newCardFrontET);
        TextInputEditText newCardBackET = findViewById(R.id.newCardBackET);


        dialogBuilder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String newCardFront = newCardFrontET.getText().toString();
                String newCardBack = newCardBackET.getText().toString();
                if (newCardFront != null && newCardBack != null) {
                    //TODO: create new card in firebase;
                }
            }
        });
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        dialogBuilder.create().show();
    }

}
