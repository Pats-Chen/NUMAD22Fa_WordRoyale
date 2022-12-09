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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendListActivity extends AppCompatActivity {
    private RecyclerView friendListRV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friendListRV = findViewById(R.id.friendListRV);
    }

    public class FriendAdapter extends RecyclerView.Adapter<FriendListActivity.FriendViewHolder> {
        private Context context;
        private List<String> friendIDList;
        private List<String> friendNameList;

        FriendAdapter(Context context, List<String> friendIDList, List<String> friendNameList) {
            this.context = context;
            this.friendIDList = friendIDList;
            this.friendNameList = friendNameList;
        }

        @NonNull
        @Override
        public FriendListActivity.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.activity_friend_list_item, parent, false);
            return new FriendListActivity.FriendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendListActivity.FriendViewHolder holder, int position) {
            holder.friendID.setText(String.valueOf(friendIDList.get(position)));
            holder.friendName.setText(String.valueOf(friendNameList.get(position)));
            holder.friendID.setOnClickListener(view -> {
//                Intent intent = new Intent(context, FriendActivity.class);
                Bundle cardBundle = new Bundle();
//                cardBundle.putString("CARD ID", cardIDList.get(position));
//                cardBundle.putString("CARD FRONT", cardFrontList.get(position));
//                cardBundle.putString("CARD BACK", cardBackList.get(position));
//                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return friendIDList.size();
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendID;
        TextView friendName;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            friendID = itemView.findViewById(R.id.friendListItemID);
            friendName = itemView.findViewById(R.id.friendListItemName);

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
