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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeckListActivity extends AppCompatActivity {
    public static final String TAG = "DeckListActivity";
    private RecyclerView deckListRV;
    private DeckListActivity.DeckAdapter deckListAdapter;
    private List<Deck> deckList;
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
        setContentView(R.layout.activity_deck_list);

        deckList = new ArrayList<>();
        deckListAdapter = new DeckListActivity.DeckAdapter(this, deckList);
        rootRef.child("users")
                .child(userAuth.getCurrentUser().getUid())
                .child("deckList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Deck deck = dataSnapshot.getValue(Deck.class);
                            deckList.add(deck);
                        }
                        deckListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        deckListRV = findViewById(R.id.deckListRV);
        deckListRV.setHasFixedSize(true);
        deckListRV.setAdapter(deckListAdapter);
        deckListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    public void newDeckDialog(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_new_deck, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("CREATE A NEW DECK!");

        TextInputLayout newDeckNameLayout = dialogView.findViewById(R.id.newDeckNameInputLayout);

        dialogBuilder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String newDeckName = newDeckNameLayout.getEditText().getText().toString().trim();

                saveNewDeckToDatabase(newDeckName);
            }
        });

        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        dialogBuilder.create().show();
    }

    public void saveNewDeckToDatabase(String newDeckName) {
        if (newDeckName != null) {
            Deck newDeck = new Deck(newDeckName);
            newDeck.setDeckCreatorUID(userAuth.getCurrentUser().getUid());
            //TODO: check if deck name is used.
            rootRef.child("users")
                    .child(userAuth.getCurrentUser().getUid())
                    .child("deckList")
                    .child(newDeckName)
                    .setValue(newDeck);
        } else {
            Log.e(TAG, "Empty deck names are not allowed!");
        }
    }

    public class DeckAdapter extends RecyclerView.Adapter<DeckListActivity.DeckViewHolder> {
        private Context context;
        private List<Deck> deckList;

        DeckAdapter(Context context, List<Deck> deckList) {
            this.context = context;
            this.deckList = deckList;
        }

        @NonNull
        @Override
        public DeckListActivity.DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.activity_deck_list_item, parent, false);
            return new DeckListActivity.DeckViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeckListActivity.DeckViewHolder holder, int position) {
            holder.deckNameTV.setText(deckList.get(position).getDeckName());
            holder.deckCreatorUIDTV.setText(deckList.get(position).getDeckCreatorUID());
            holder.deckSizeTV.setText(deckList.get(position).getDeckSize());

            holder.itemView.setOnClickListener(view -> {
                //TODO: Test Activity
//                Intent intent = new Intent(context, CardActivity.class);
//                Bundle cardBundle = new Bundle();
//                cardBundle.putString("CARD ID", cardList.get(position).getCardID());
//                cardBundle.putString("CARD FRONT", cardList.get(position).getCardFront());
//                cardBundle.putString("CARD BACK", cardList.get(position).getCardBack());
//                cardBundle.putString("CARD DIFFICULTY", cardList.get(position).getCardDifficulty());
//                intent.putExtras(cardBundle);
//                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return deckList.size();
        }
    }

    public class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView deckNameTV;
        TextView deckCreatorUIDTV;
        TextView deckSizeTV;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);

            deckNameTV = itemView.findViewById(R.id.deckListItemName);
            deckCreatorUIDTV = itemView.findViewById(R.id.deckListItemCreatorUID);
            deckSizeTV = itemView.findViewById(R.id.deckListItemSize);
        }
    }
}
