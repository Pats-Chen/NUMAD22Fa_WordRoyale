package edu.northeastern.numad22fa_wordroyale;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CardListActivity extends AppCompatActivity {
    private RecyclerView cardListRV;
    private CardAdapter cardListAdapter;
    private List<Card> cardList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        cardListRV = findViewById(R.id.cardListRV);

        cardList = new ArrayList<>();

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
            holder.cardID.setText(cardList.get(position).getCardID());
            holder.cardFrontTV.setText(cardList.get(position).getCardFront());
            holder.cardBackTV.setText(cardList.get(position).getCardBack());
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, CardActivity.class);
                Bundle cardBundle = new Bundle();
                cardBundle.putString("CARD ID", cardList.get(position).getCardID());
                cardBundle.putString("CARD FRONT", cardList.get(position).getCardFront());
                cardBundle.putString("CARD BACK", cardList.get(position).getCardBack());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardID;
        TextView cardFrontTV;
        TextView cardBackTV;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            cardID = itemView.findViewById(R.id.cardListItemID);
            cardFrontTV = itemView.findViewById(R.id.cardListItemFront);
            cardBackTV = itemView.findViewById(R.id.cardListItemBack);
        }
    }
}
