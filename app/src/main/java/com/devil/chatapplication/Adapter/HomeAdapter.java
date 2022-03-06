package com.devil.chatapplication.Adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.ChatActivity;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.UserlayoutBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HomeAdapter extends FirestoreRecyclerAdapter<userProfile, HomeAdapter.userViewholder> {
    private Context context;


    public HomeAdapter(@NonNull FirestoreRecyclerOptions<userProfile> options, Context c) {
        super(options);
        this.context=c;


    }

    @Override
    protected void onBindViewHolder(@NonNull userViewholder holder, int position, @NonNull userProfile model) {
        holder.bind.tvUserrecycler.setText(model.getName());
        holder.bind.tvStatusrecycler.setText(model.getStatus());
        Log.d("subhash", "onbindholder: " + context);
        Glide
                .with(context)
                .load(model.getImage())
                .centerCrop()
                .into(holder.bind.imageRecycler);
        holder.bind.cardviewrecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent intent=new Intent(view.getContext(), ChatActivity.class);
                         intent.putExtra("itemId",model.getUid());
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
