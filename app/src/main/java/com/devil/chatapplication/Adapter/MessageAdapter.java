package com.devil.chatapplication.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devil.chatapplication.Models.Message;
import com.devil.chatapplication.R;
import com.devil.chatapplication.databinding.ItemReceiveBinding;
import com.devil.chatapplication.databinding.ItemSendBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder> {
    private Context context;
    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;


    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }


    @Override
    public int getItemViewType(int position) {

        if (Objects.equals(FirebaseAuth.getInstance().getUid(), getItem(position).getSenderid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        if (viewType == ITEM_SEND) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_send, parent, false);
            return new sendViewholder(view);
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receive, parent, false);
        return new receiveViewholder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Message model) {
        switch (holder.getItemViewType()) {
            case ITEM_SEND:
                ((sendViewholder) holder).bind(model);
                break;
            case ITEM_RECEIVE:
                ((receiveViewholder) holder).bind(model);
        }
    }

    protected static class sendViewholder extends RecyclerView.ViewHolder {
        ItemSendBinding sendBinding;

        public sendViewholder(@NonNull View view) {
            super(view);
            sendBinding = ItemSendBinding.bind(view);
        }

        public void bind(Message model) {
//            String timeAgo = (String) DateUtils.getRelativeTimeSpanString(model.getTimestamp().getSeconds() * 1000);
//            sendBinding.sendtimeTv.setText(timeAgo);
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");

            sendBinding.sendtimeTv.setText(formatter.format(model.getTimestamp()));
            sendBinding.sendMessageTv.setText(model.getMessage());
        }
    }

    protected static class receiveViewholder extends RecyclerView.ViewHolder {
        ItemReceiveBinding receiverBinding;

        public receiveViewholder(@NonNull View view) {
            super(view);
            receiverBinding = ItemReceiveBinding.bind(view);
        }

        public void bind(Message model) {
            receiverBinding.receiveMessageTv.setText(model.getMessage());
//            String timeAgo = (String) DateUtils.getRelativeTimeSpanString(model.getTimestamp().getSeconds() * 1000);
//            receiverBinding.recivetimeTv.setText(timeAgo);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

            receiverBinding.recivetimeTv.setText(formatter.format(model.getTimestamp()));
        }
    }
}
