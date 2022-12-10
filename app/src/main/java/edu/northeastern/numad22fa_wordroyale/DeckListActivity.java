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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
    private FirebaseRecyclerOptions<Deck> options;

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

        options = new FirebaseRecyclerOptions.Builder<Deck>()
                .setQuery(rootRef.child("users").child(userAuth.getCurrentUser().getUid()).child("deckList"), Deck.class)
                .build();
        deckListAdapter = new DeckListActivity.DeckAdapter(options);

        deckListRV = findViewById(R.id.deckListRV);
        deckListRV.setHasFixedSize(true);
        deckListRV.setAdapter(deckListAdapter);
        deckListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        deckListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        deckListAdapter.stopListening();
    }

    public void newDeckDialog(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_new_deck, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("CREATE A NEW DECK!");

        TextInputLayout newDeckNameLayout = dialogView.findViewById(R.id.newDeckNameInputLayout);

        dialogBuilder.setPositiveButton("CREATE", (dialog, id) -> {
            String newDeckName = newDeckNameLayout.getEditText().getText().toString().trim();

            saveNewDeckToDatabase(newDeckName);
        });

        dialogBuilder.setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

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

    public class DeckAdapter extends FirebaseRecyclerAdapter<Deck, DeckListActivity.DeckViewHolder> {

        public DeckAdapter(@NonNull FirebaseRecyclerOptions<Deck> options) {
            super(options);
        }

        @NonNull
        @Override
        public DeckListActivity.DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.activity_deck_list_item, parent, false);
            return new DeckListActivity.DeckViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull DeckListActivity.DeckViewHolder holder, int position, @NonNull Deck model) {
            holder.deckNameTV.setText(model.getDeckName());
            holder.deckCreatorUIDTV.setText(model.getDeckCreatorUID());
            holder.deckSizeTV.setText(model.getDeckSize() + "/30");
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
