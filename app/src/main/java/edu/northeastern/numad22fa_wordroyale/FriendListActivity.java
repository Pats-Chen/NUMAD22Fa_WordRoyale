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

public class FriendListActivity extends AppCompatActivity {
    public static final String TAG = "FriendListActivity";
    private RecyclerView friendListRV;
    private FriendListActivity.FriendAdapter friendListAdapter;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private FirebaseRecyclerOptions<String> options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_friend_list);

        options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(rootRef.child("users").child(userAuth.getCurrentUser().getUid()).child("friendList"), String.class)
                .build();
        friendListAdapter = new FriendListActivity.FriendAdapter(options);


        friendListRV = findViewById(R.id.friendListRV);
        friendListRV.setHasFixedSize(true);
        friendListRV.setAdapter(friendListAdapter);
        friendListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        friendListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        friendListAdapter.stopListening();
    }

    public void addFriendDialog(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_new_friend, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("ADD A NEW FRIEND!");

        TextInputLayout addFriendUIDLayout = dialogView.findViewById(R.id.addFriendUIDInputLayout);

        dialogBuilder.setPositiveButton("ADD", (dialog, id) -> {
            String newFriendUID = addFriendUIDLayout.getEditText().getText().toString().trim();

            saveNewFriendToDatabase(newFriendUID);
        });

        dialogBuilder.setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        dialogBuilder.create().show();
    }

    public void saveNewFriendToDatabase(String newFriendUID) {
        if (newFriendUID != null) {
            rootRef.child("users")
                    .child(newFriendUID)
                    .get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(FriendListActivity.this, "Can not find this user!", Toast.LENGTH_SHORT).show();
                        } else {
                            rootRef.child("users")
                                    .child(newFriendUID)
                                    .child("friendList")
                                    .child(userAuth.getCurrentUser().getUid())
                                    .setValue(userAuth.getCurrentUser().getUid());

                            rootRef.child("users")
                                    .child(userAuth.getCurrentUser().getUid())
                                    .child("friendList")
                                    .child(newFriendUID)
                                    .setValue(newFriendUID);
                            Toast.makeText(FriendListActivity.this, "Add new friend successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e(TAG, "Empty user UIDs are not allowed!");
        }
    }

    public void sendDeckToFriend(String friendUID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_send_deck_to_friend, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("WRITE DOWN THE DECK NAME TO SEND IT!");

        TextInputLayout selectDeckNameLayout = dialogView.findViewById(R.id.selectDeckNameInputLayout);

        dialogBuilder.setPositiveButton("SEND", (dialog, id) -> {
            String selectDeckName = selectDeckNameLayout.getEditText().getText().toString().trim();

            saveDeckToFriendDatabase(friendUID, selectDeckName);
        });

        dialogBuilder.setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

        dialogBuilder.create().show();
    }

    public void saveDeckToFriendDatabase(String friendUID, String selectDeckName) {
        rootRef.child("users")
                .child(userAuth.getCurrentUser().getUid())
                .child("deckList")
                .child(selectDeckName)
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(FriendListActivity.this, "Failed to find the deck!", Toast.LENGTH_SHORT).show();
                    } else {
                        Deck selectedDeck = task.getResult().getValue(Deck.class);

                        if (selectedDeck.getDeckSize() != 30) {
                            Toast.makeText(FriendListActivity.this, "Only full decks can be sent to friends!", Toast.LENGTH_SHORT).show();
                        } else {
                            rootRef.child("users")
                                    .child(friendUID)
                                    .child("deckList")
                                    .child(selectedDeck.getDeckName())
                                    .setValue(selectedDeck);
                            Toast.makeText(FriendListActivity.this, "Deck is sent successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public class FriendAdapter extends FirebaseRecyclerAdapter<String, FriendListActivity.FriendViewHolder> {

        public FriendAdapter(@NonNull FirebaseRecyclerOptions<String> options) {
            super(options);
        }

        @NonNull
        @Override
        public FriendListActivity.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.activity_friend_list_item, parent, false);
            return new FriendListActivity.FriendViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull FriendListActivity.FriendViewHolder holder, int position, @NonNull String model) {
            holder.friendUID.setText(model);
            holder.itemView.setOnClickListener(view -> sendDeckToFriend(model));
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendUID;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            friendUID = itemView.findViewById(R.id.friendListItemUID);
        }
    }
}
