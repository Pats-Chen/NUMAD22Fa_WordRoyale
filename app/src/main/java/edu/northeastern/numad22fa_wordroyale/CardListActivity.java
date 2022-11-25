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
    private List<String> cardIDList;
    private List<String> cardFrontList;
    private List<String> cardBackList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        cardListRV = findViewById(R.id.cardListRV);

        cardIDList = new ArrayList<>();
        cardFrontList = new ArrayList<>();
        cardBackList = new ArrayList<>();

        cardListAdapter = new CardAdapter(this, cardIDList, cardFrontList, cardBackList);
        cardListRV.setAdapter(cardListAdapter);
        cardListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private Context context;
        private List<String> cardIDList;
        private List<String> cardFrontList;
        private List<String> cardBackList;

        CardAdapter(Context context, List<String> cardIDList, List<String> cardFrontList,
                    List<String> cardBackList) {
            this.context = context;
            this.cardIDList = cardIDList;
            this.cardFrontList = cardFrontList;
            this.cardBackList = cardBackList;
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
            holder.cardID.setText(String.valueOf(cardIDList.get(position)));
            holder.cardFrontTV.setText(String.valueOf(cardFrontList.get(position)));
            holder.cardBackTV.setText(String.valueOf(cardBackList.get(position)));
            holder.cardID.setOnClickListener(view -> {
                Intent intent = new Intent(context, CardActivity.class);
                Bundle cardBundle = new Bundle();
                cardBundle.putString("CARD ID", cardIDList.get(position));
                cardBundle.putString("CARD FRONT", cardFrontList.get(position));
                cardBundle.putString("CARD BACK", cardBackList.get(position));
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return cardIDList.size();
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

            itemView.setOnClickListener(view -> {
//                if (recyclerViewInterface != null) {
//                    int position = getAbsoluteAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        recyclerViewInterface.onLinkClick(position);
//                    }
//                }
            });
        }
    }
}
