package com.devil.chatapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.Models.Message;
import com.devil.chatapplication.R;
import com.devil.chatapplication.databinding.ItemReceiveBinding;
import com.devil.chatapplication.databinding.ItemReceivePhotoBinding;
import com.devil.chatapplication.databinding.ItemSendBinding;
import com.devil.chatapplication.databinding.ItemSendPhotoBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder> {
    private Context context;
    private final OnMessageTouchListener messageTouchListener;
    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;
    final int ITEM_SEND_IMAGE = 3;
    final int ITEM_RECEIVE_IMAGE = 4;
    long startClickTime;
    public int[] reaction =new int[]{

            R.drawable.cool,
            R.drawable.hi,
            R.drawable.happy,
            R.drawable.said,
            R.drawable.smile,
            R.drawable.wow
    };


    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options,OnMessageTouchListener onMessageTouchListener) {
        super(options);
        this.messageTouchListener=onMessageTouchListener;
    }


   public interface OnMessageTouchListener{
        void onMessageTouch(Message message,int pos);
   }

    @Override
    public int getItemViewType(int position) {

        if (Objects.equals(FirebaseAuth.getInstance().getUid(), getItem(position).getSenderid())) {
            if (getItem(position).getUrl() != null) {
                return ITEM_SEND_IMAGE;
            }
            return ITEM_SEND;
        } else {
            if (getItem(position).getUrl() != null) {
                return ITEM_RECEIVE_IMAGE;
            }
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
        } else if (viewType == ITEM_SEND_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_send_photo, parent, false);
            return new sendImageViewholder(view);
        } else if (viewType == ITEM_RECEIVE_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_receive_photo, parent, false);
            return new receiveImageViewholder(view);
        }


        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receive, parent, false);
        return new receiveViewholder(view);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Message model) {

        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.item_fall_down));
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reaction)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            messageTouchListener.onMessageTouch(model,pos);
            return true; // true is closing popup, false is requesting a new selection
        });


        switch (holder.getItemViewType()) {
            case ITEM_SEND:
                ((sendViewholder) holder).bind(model);

                if (model.getFeeling()>-1){
                    ((sendViewholder) holder).sendBinding.sendMassageEmoji.setVisibility(View.VISIBLE);
                    ((sendViewholder) holder).sendBinding.sendMassageEmoji.setImageResource(reaction[model.getFeeling()]);
                }
                ((sendViewholder) holder).sendBinding.sendMessageTv.setOnTouchListener((view, motionEvent) -> {
                    view.setOnLongClickListener(view1 -> {
                        popup.onTouch(view1, motionEvent);
                        return false;
                    });
                    return false;
                });
                break;
            case ITEM_SEND_IMAGE:
                if (holder instanceof sendImageViewholder) {
                    ((sendImageViewholder) holder).bind(model);
                    if (model.getFeeling()>-1){
                    ((sendImageViewholder) holder).sendPhotoBinding.sendMassageEmoji.setVisibility(View.VISIBLE);
                    ((sendImageViewholder) holder).sendPhotoBinding.sendMassageEmoji.setImageResource(reaction[model.getFeeling()]);
                    }
                    ((sendImageViewholder) holder).sendPhotoBinding.sendMessageIv.setOnTouchListener((view, motionEvent) -> {
                        view.setOnLongClickListener(view1 -> {
                            popup.onTouch(view1, motionEvent);
                            return false;
                        });
                        return false;
                    });
                }
                break;
            case ITEM_RECEIVE_IMAGE:
                if (holder instanceof receiveImageViewholder) {
                    ((receiveImageViewholder) holder).bind(model);
                    if (model.getFeeling()>-1){
                    ((receiveImageViewholder) holder).receivePhotoBinding.receiveMassageEmoji.setVisibility(View.VISIBLE);
                    ((receiveImageViewholder) holder).receivePhotoBinding.receiveMassageEmoji.setImageResource(reaction[model.getFeeling()]);
                    }
                    ((receiveImageViewholder) holder).receivePhotoBinding.receiveMessageIv.setOnTouchListener((view, motionEvent) -> {
                        view.setOnLongClickListener(view1 -> {
                            popup.onTouch(view1, motionEvent);
                            return false;
                        });
                        return false;
                    });
                }
                break;
            case ITEM_RECEIVE:
                if (holder instanceof receiveViewholder) {
                    ((receiveViewholder) holder).bind(model);
                    if (model.getFeeling()>-1){
                    ((receiveViewholder) holder).receiverBinding.receiveMassageEmoji.setVisibility(View.VISIBLE);
                    ((receiveViewholder) holder).receiverBinding.receiveMassageEmoji.setImageResource(reaction[model.getFeeling()]);
                    }
                    ((receiveViewholder) holder).receiverBinding.receiveMessageTv.setOnTouchListener((view, motionEvent) -> {
                        view.setOnLongClickListener(view1 -> {
                            popup.onTouch(view1, motionEvent);
                            return false;
                        });
                        return false;
                    });
                }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected static class sendViewholder extends RecyclerView.ViewHolder {
        ItemSendBinding sendBinding;

        public sendViewholder(@NonNull View view) {
            super(view);
            sendBinding = ItemSendBinding.bind(view);
        }

        public void bind(Message model) {

            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

            sendBinding.sendtimeTv.setText(formatter.format(model.getTimestamp()));

            sendBinding.sendMessageTv.setText(model.getMessage());
        }
    }

    protected static class sendImageViewholder extends RecyclerView.ViewHolder {
        ItemSendPhotoBinding sendPhotoBinding;

        public sendImageViewholder(@NonNull View view) {
            super(view);
            sendPhotoBinding = ItemSendPhotoBinding.bind(view);
        }

        public void bind(Message model) {
            Glide
                    .with(itemView.getContext())
                    .load(model.getUrl())
                    .centerCrop()
                    .into(sendPhotoBinding.sendMessageIv);
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a",Locale.ENGLISH);
            sendPhotoBinding.sendtimeTv.setText(formatter.format(model.getTimestamp()));
        }
    }

    protected static class receiveImageViewholder extends RecyclerView.ViewHolder {
        ItemReceivePhotoBinding receivePhotoBinding;

        public receiveImageViewholder(@NonNull View view) {
            super(view);
            receivePhotoBinding = ItemReceivePhotoBinding.bind(view);
        }

        public void bind(Message model) {
            Glide
                    .with(itemView.getContext())
                    .load(model.getUrl())
                    .centerCrop()
                    .into(receivePhotoBinding.receiveMessageIv);

            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a",Locale.ENGLISH);
            receivePhotoBinding.recivetimeTv.setText(formatter.format(model.getTimestamp()));
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
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a",Locale.ENGLISH);

            receiverBinding.recivetimeTv.setText(formatter.format(model.getTimestamp()));
        }
    }
}
