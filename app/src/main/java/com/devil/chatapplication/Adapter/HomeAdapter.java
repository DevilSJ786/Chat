package com.devil.chatapplication.Adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.ChatActivity;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.UserlayoutBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class HomeAdapter extends FirestoreRecyclerAdapter<userProfile, HomeAdapter.userViewholder> {
    private Context context;
    private String senderId, receiverId, senderRoom;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;


    public HomeAdapter(@NonNull FirestoreRecyclerOptions<userProfile> options, Context c) {
        super(options);
        this.context = c;


    }

    @Override
    public int getItemViewType(int position) {
//        Log.d("TAG", "getItemViewType: "+getItem(position).getUid());
        return super.getItemViewType(position);
    }

    @Override
    protected void onBindViewHolder(@NonNull userViewholder holder, int position, @NonNull userProfile model) {
        holder.bind.usernametv.setText(model.getName());

        firestore = FirebaseFirestore.getInstance();
        senderId = FirebaseAuth.getInstance().getUid();
        receiverId = model.getUid();
        senderRoom = receiverId + senderId;
        documentReference = firestore.collection("chats").document(senderRoom);
        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {

                holder.bind.lastMessage.setText(value.getString("lastMessage"));
//                SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
//                holder.bind.lastimeSeen.setText(formatter.format(value.getTimestamp("timestamp").toDate()));
                String timeAgo = (String) DateUtils.getRelativeTimeSpanString(Objects.requireNonNull(value.getTimestamp("timestamp")).getSeconds() * 1000);
                holder.bind.lastimeSeen.setText(timeAgo);
            }
//                       holder.bind.lastimeSeen.setText(value);
        });


        Log.d("subhash", "onbindholder: " + context);
        Glide
                .with(context)
                .load(model.getImage())
                .centerCrop()
                .into(holder.bind.imageRecycler);
        holder.bind.cardviewrecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("itemId", model.getUid());
                view.getContext().startActivity(intent);

            }
        });
    }

    @NonNull
    @Override
    public userViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        UserlayoutBinding binding1 = UserlayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new userViewholder(binding1);
    }

    protected static class userViewholder extends RecyclerView.ViewHolder {
        UserlayoutBinding bind;

        public userViewholder(@NonNull UserlayoutBinding b) {
            super(b.getRoot());
            bind = b;
            Log.d("rohit", "holder: ");
        }
    }
}
